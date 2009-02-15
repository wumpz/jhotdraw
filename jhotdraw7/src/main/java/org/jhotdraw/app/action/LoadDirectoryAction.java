/*
 * @(#)LoadDirectoryAction.java  1.0  2009-02-08
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

package org.jhotdraw.app.action;

import javax.swing.JFileChooser;
import org.jhotdraw.app.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * LoadDirectoryAction.
 *
 * @author Werner Randelshofer, Staldenmattweg 2, CH-6405 Immensee
 * @version 1.0 2009-02-08 Created.
 */
public class LoadDirectoryAction extends LoadAction {
    public final static String ID = "file.loadDirectory";

    /** Creates a new instance. */
    public LoadDirectoryAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, "file.openDirectory");
    }
    @Override
    protected JFileChooser getFileChooser(View view) {
        return ((DirectoryView) view).getOpenDirectoryChooser();
    }

}
