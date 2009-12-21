/*
 * @(#)LoadDirectoryAction.java
 * 
 * Copyright (c) 2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package org.jhotdraw.app.action.file;

import org.jhotdraw.app.*;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Les the user save unsaved changes of the active view, then presents
 * an {@code URIChooser} and then loads the selected URI into the active view.
 * <p>
 * This action is called when the user selects the Load Directory item in the File
 * menu. The menu item is automatically created by the application.
 * <p>
 * This action requires that the active view implements the
 * {@link org.jhotdraw.app.DirectoryView} interface.
 * <p>
 * This action is designed for applications which do not automatically
 * create a new view for each opened file. This action goes together with
 * {@code FileClearAction}, {@code FileNewWindowAction}, {@code LoadFileAction},
 * {@code LoadDirectoryAction} and {@code FileCloseAction}.
 * This action should not be used together with {@code FileOpenAction}.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link ApplicationModel#initApplication}. The views created by
 * {@code ApplicationModel} must implement the {@link DirectoryView} interface.
 * <p>
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below define together the contracts
 * of a smaller framework inside of the JHotDraw framework for document oriented
 * applications.<br>
 * Contract: {@link org.jhotdraw.app.DirectoryView}, {@link LoadDirectoryAction}.
 * <hr>
 *
 * @author Werner Randelshofer, Staldenmattweg 2, CH-6405 Immensee
 * @version $Id$
 */
public class LoadDirectoryAction extends LoadFileAction {
    public final static String ID = "file.loadDirectory";

    /** Creates a new instance. */
    public LoadDirectoryAction(Application app) {
        this(app,null);
    }
    /** Creates a new instance. */
    public LoadDirectoryAction(Application app, View view) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    @Override
    protected URIChooser getFileChooser(View view) {
        return ((DirectoryView) view).getOpenDirectoryChooser();
    }
}
