/*
 * @(#)FocusWindowAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.window;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.URI;
import javax.swing.*;
import org.jhotdraw.api.app.View;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.*;

/**
 * Requests focus for a Frame.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FocusWindowAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "window.focus";
    private View view;
    private PropertyChangeListener ppc;

    /**
     * Creates a new instance.
     */
    public FocusWindowAction(View view) {
        this.view = view;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
        labels.configureAction(this, ID);
        setEnabled(view != null);
        ppc = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(View.TITLE_PROPERTY)) {
                    putValue(Action.NAME, evt.getNewValue());
                }
            }
        };
        if (view != null) {
            view.addPropertyChangeListener(ppc);
        }
    }

    public void dispose() {
        setView(null);
    }

    public void setView(View newValue) {
        if (view != null) {
            view.removePropertyChangeListener(ppc);
        }
        view = newValue;
        if (view != null) {
            view.addPropertyChangeListener(ppc);
        }
    }

    @Override
    public Object getValue(String key) {
        if (Action.NAME.equals(key) && view != null) {
            return getTitle();
        } else {
            return super.getValue(key);
        }
    }

    private String getTitle() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
        String title = labels.getString("unnamedFile");
        if (view != null) {
            URI uri = view.getURI();
            if (uri == null) {
                title = labels.getString("unnamedFile");
            } else {
                title = URIUtil.getName(uri);
            }
            if (view.hasUnsavedChanges()) {
                title += "*";
            }
            title = (labels.getFormatted("internalFrame.title", title,
                    view.getApplication() == null ? "" : view.getApplication().getName(), view.getMultipleOpenId()));
        }
        return title;
    }


    private Component getRootPaneContainer() {
        return SwingUtilities.getRootPane(
                view.getComponent()).getParent();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        /*
        JFrame frame = getFrame();
        if (frame != null) {
        frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
        frame.toFront();
        frame.requestFocus();
        JRootPane rp = SwingUtilities.getRootPane(view.getComponent());
        if (rp != null && (rp.getParent() instanceof JInternalFrame)) {
        ((JInternalFrame) rp.getParent()).toFront();
        }
        view.getComponent().requestFocus();
        } else {
        Toolkit.getDefaultToolkit().beep();
        }*/
        Component rpContainer = getRootPaneContainer();
        if (rpContainer instanceof Frame) {
            Frame frame = (Frame) rpContainer;
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
        } else if (rpContainer instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) rpContainer;
            frame.toFront();
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                // Don't care.
            }
        }
        view.getComponent().requestFocusInWindow();
    }
}
