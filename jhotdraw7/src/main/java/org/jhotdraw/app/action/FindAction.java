/*
 * @(#)FindAction.java  1.0  March 21, 2007
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

package org.jhotdraw.app.action;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.Arrays;
import javax.print.DocPrintJob;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;
/**
 * Presents a printer dialog to the user and then prints the Project to the
 * chosen printer.
 * <p>
 * This action requires that the project has the following additional methods:
 * <pre>
 * public void find();
 * </pre>
 * <p>
 * The FindAction invokes this method using Java Reflection. Thus there is
 * no Java Interface that the Project needs to implement.
 *
 * @see org.jhotdraw.draw.DrawingPageable
 *
 * @author Werner Randelshofer
 * @version 1.0 March 21, 2007 Created.
 */
public class FindAction extends AbstractProjectAction {
    public final static String ID = "find";
    
    /** Creates a new instance. */
    public FindAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        Project project = getCurrentProject();
        try {
            Methods.invoke(project, "find");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }
}
