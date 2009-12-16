/*
 * @(#)ExportAction.java
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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

import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.URI;
import org.jhotdraw.app.*;
import org.jhotdraw.gui.chooser.JFileURIChooser;
import org.jhotdraw.gui.chooser.URIChooser;
import org.jhotdraw.net.URIUtil;

/**
 * Presents a file chooser to the user and then exports the 
 * {@link org.jhotdraw.app.View} to the chosen file.
 * <p>
 * This action requires that the view implements the 
 * {@link org.jhotdraw.app.ExportableView} interface.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below define together the contracts
 * of a smaller framework inside of the JHotDraw framework for document oriented
 * applications.<br>
 * Contract: {@link org.jhotdraw.app.ExportableView}, {@link org.jhotdraw.app.action.ExportAction}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExportAction extends AbstractViewAction {

    public final static String ID = "file.export";
    private Component oldFocusOwner;

    /** Creates a new instance. */
    public ExportAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    public void actionPerformed(ActionEvent evt) {
        final ExportableView view = (ExportableView) getActiveView();
        if (view.isEnabled()) {
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");

            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);

            URIChooser fileChooser = view.getExportChooser();

            JSheet.showSheet(fileChooser, view.getComponent(), labels.getString("filechooser.export"), new SheetListener() {

                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final URI uri = evt.getChooser().getSelectedURI();
                        if (evt.getChooser()instanceof JFileURIChooser) {
                        exportView(view, uri, evt.getFileChooser().getFileFilter(), evt.getFileChooser().getAccessory());
                        } else {
                        exportView(view, uri, null,null);
                        }
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

    protected void exportView(final ExportableView view, final URI uri,
            final javax.swing.filechooser.FileFilter filter,
            final Component accessory) {
        view.execute(new Worker() {

            protected Object construct() throws IOException {
                view.export(uri, filter, accessory);
                return null;
            }

            protected void failed(Throwable value) {
                System.out.flush();
                ((Throwable) value).printStackTrace();
                // FIXME localize this error messsage
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css") +
                        "<b>Couldn't export to the file \"" + URIUtil.getName(uri) + "\".<p>" +
                        "Reason: " + value,
                        JOptionPane.ERROR_MESSAGE);
            }

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
