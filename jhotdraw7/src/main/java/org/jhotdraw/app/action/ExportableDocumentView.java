/*
 * @(#)ExportableDocumentView.java  1.0  January 2, 2007
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.application.action;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import org.jhotdraw.application.*;

/**
 * ExportableDocumentView is implemented by DocumentView's that support the ExportAction.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 January 2, 2007 Created.
 */
public interface ExportableDocumentView extends DocumentView {
    /**
     * Gets the file chooser for exporting the documentView.
     */
  public JFileChooser getExportChooser();
 
  /**
   * Exports the documentView. 
   * By convention this method is never invoked on the AWT Event Dispatcher Thread. 
   *
   * @param f The file to which export the documentView.
   * @param filter The FileFilter that was used to choose the file. This can be null.
   * @param accessory The Accessory used by the JFileChooser. This can be null.
   */
  public void export(File f, javax.swing.filechooser.FileFilter filter, Component accessory) throws IOException;
}
