/*
 * @(#)SaveAsAction.java  1.0  28. September 2005
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
import org.jhotdraw.app.Application;

/**
 * SaveAsAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 28. September 2005 Created.
 */
public class SaveAsAction extends SaveAction {
    public final static String ID = "file.saveAs";

    /** Creates a new instance. */
    public SaveAsAction(Application app) {
        super(app, true);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
}