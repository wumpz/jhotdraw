/*
 * @(#)NewFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.file;

import org.jhotdraw.util.*;
import java.awt.event.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * Creates a new view.
 * <p>
 * This action is called when the user selects the New item in the File
 * menu.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 * <p>
 * This action is designed for applications which automatically create
 * a new view for each opened file. This action goes together with
 * {@link OpenFileAction} and {@link CloseFileAction}. It should
 * not be used together with {@link NewWindowAction}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewFileAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.new";
    
    /** Creates a new instance. */
    public NewFileAction(Application app) {
        this(app,ID);
    }
    public NewFileAction(Application app, String id) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, id);
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        Application app = getApplication();
        final View newView = app.createView();
        int multiOpenId = 1;
        for (View existingP : app.views()) {
            if (existingP.getURI() == null) {
                multiOpenId = Math.max(multiOpenId, existingP.getMultipleOpenId() + 1);
            }
        }
        newView.setMultipleOpenId(multiOpenId);
        app.add(newView);
        newView.execute(new Runnable() {
            @Override
            public void run() {
                newView.clear();
            }
        });
        app.show(newView);
    }
}
