/*
 * @(#)SaveAsAction.java  1.0  28. September 2005
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
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
    public final static String ID = "saveAs";

    /** Creates a new instance. */
    public SaveAsAction(Application app) {
        super(app, true);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
}