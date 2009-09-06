/*
 * @(#)LoadAction.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
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

import org.jhotdraw.util.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * Loads a file into the current view.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class LoadAction extends AbstractSaveBeforeAction {

    public final static String ID = "file.load";

    /** Creates a new instance. */
    public LoadAction(Application app) {
        this(app,null);
    }
    /** Creates a new instance. */
    public LoadAction(Application app, View view) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, "file.open");
    }

    protected JFileChooser getFileChooser(View view) {
        return view.getOpenChooser();
    }

    public void doIt(View view) {
        JFileChooser fileChooser = getFileChooser(view);
        if (fileChooser.showOpenDialog(view.getComponent()) == JFileChooser.APPROVE_OPTION) {
            loadFile(view, fileChooser.getSelectedFile());
        } else {
            view.setEnabled(true);
        }
    }

    public void loadFile(final View view, final File file) {
 
        view.setEnabled(false);

        // Open the file
        view.execute(new Worker() {

            protected Object construct() throws IOException {
                view.read(file);
                return null;
            }

            @Override
            protected void done(Object value) {
                view.setFile(file);
                view.setEnabled(true);
                getApplication().addRecentFile(file);
            }

            @Override
            protected void failed(Throwable value) {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css") +
                        "<b>" + labels.getFormatted("file.load.couldntLoad.message", file.getName()) + "</b><br>" +
                        ((value == null) ? "" : value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                    public void optionSelected(SheetEvent evt) {
                        view.clear();
                        view.setEnabled(true);
                    }
                });
            }

        });
    }
}
