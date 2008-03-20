/*
 * @(#)ExportableView.java  1.0  January 2, 2007
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.awt.*;
import java.io.*;
import javax.swing.*;
import org.jhotdraw.app.*;

/**
 * ExportableView is implemented by View's that support the ExportAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 2, 2007 Created.
 */
public interface ExportableView extends View {
    /**
     * Gets the file chooser for exporting the view.
     */
  public JFileChooser getExportChooser();
 
  /**
   * Exports the view. 
   * By convention this method is never invoked on the AWT Event Dispatcher Thread. 
   *
   * @param f The file to which export the view.
   * @param filter The FileFilter that was used to choose the file. This can be null.
   * @param accessory The Accessory used by the JFileChooser. This can be null.
   */
  public void export(File f, javax.swing.filechooser.FileFilter filter, Component accessory) throws IOException;
}
