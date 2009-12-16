/*
 * @(#)SaveAction.java
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app.action;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.URI;
import org.jhotdraw.app.*;
import org.jhotdraw.io.*;
import org.jhotdraw.util.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.chooser.URIChooser;
import org.jhotdraw.gui.chooser.JFileURIChooser;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.net.URIUtil;

/**
 * SaveAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class SaveAction extends AbstractViewAction {

    public final static String ID = "file.save";
    private boolean saveAs;
    private Component oldFocusOwner;

    /** Creates a new instance. */
    public SaveAction(Application app) {
        this(app, false);
    }

    /** Creates a new instance. */
    public SaveAction(Application app, boolean saveAs) {
        super(app);
        this.saveAs = saveAs;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    public void actionPerformed(ActionEvent evt) {
        final View view = getActiveView();
        if (view.isEnabled()) {
            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);

            if (!saveAs && view.getURI() != null && view.canSaveTo(view.getURI())) {
                saveViewToURI(view, view.getURI());
            } else {
                URIChooser fileChooser = view.getSaveChooser();

                JSheet.showSaveSheet(fileChooser, view.getComponent(), new SheetListener() {

                    public void optionSelected(final SheetEvent evt) {
                        if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                            final URI uri;
                            if ((evt.getChooser()instanceof JFileURIChooser) && (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter)) {
                                uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
                            } else {
                                uri = evt.getChooser().getSelectedURI();
                            }
                            saveViewToURI(view, uri);
                        } else {
                            view.setEnabled(true);
                            if (oldFocusOwner != null) {
                                oldFocusOwner.requestFocus();
                            }
                        }
                    }
                });
            }
        }
    }

    protected void saveViewToURI(final View view, final URI file) {
        view.execute(new Worker() {

            protected Object construct() throws IOException {
                view.write(file);
                return null;
            }

            @Override
            protected void done(Object value) {
                view.setURI(file);
                view.markChangesAsSaved();
                int multiOpenId = 1;
                for (View p : view.getApplication().views()) {
                    if (p != view && p.getURI() != null && p.getURI().equals(file)) {
                        multiOpenId = Math.max(multiOpenId, p.getMultipleOpenId() + 1);
                    }
                }
                getApplication().addRecentURI(file);
                view.setMultipleOpenId(multiOpenId);
            }

            @Override
            protected void failed(Throwable value) {
                String message;
                if ((value instanceof Throwable) && ((Throwable) value).getMessage() != null) {
                    message = ((Throwable) value).getMessage();
                } else {
                    message = value.toString();
                }
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                JSheet.showMessageSheet(getActiveView().getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css") +
                        "<b>" + labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(file)) + "</b><p>" +
                        ((message == null) ? "" : message),
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            protected void finished() {
                view.setEnabled(true);
                SwingUtilities.getWindowAncestor(view.getComponent()).toFront();
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        });
    }

}
