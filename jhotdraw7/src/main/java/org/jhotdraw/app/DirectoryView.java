/*
 * @(#)DirectoryView.java  1.0  2009-02-08
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

package org.jhotdraw.app;

import javax.swing.JFileChooser;

/**
 * Interface for views which can select a directory.
 *
 * @author Werner Randelshofer, Staldenmattweg 2, CH-6405 Immensee
 * @version 1.0 2009-02-08 Created.
 */
public interface DirectoryView extends View {
    /**
     * Gets the file chooser for opening a directory for the view.
     */
    public JFileChooser getOpenDirectoryChooser();

}
