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

package org.jhotdraw.application.action;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.Arrays;
import javax.print.DocPrintJob;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import org.jhotdraw.application.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;
/**
 * Presents a printer dialog to the user and then prints the DocumentView to the
 * chosen printer.
 * <p>
 * This action requires that the documentView has the following additional methods:
 * <pre>
 * public void find();
 * </pre>
 * <p>
 * The FindAction invokes this method using Java Reflection. Thus there is
 * no Java Interface that the DocumentView needs to implement.
 * 
 * @author Werner Randelshofer
 * @version 1.0 March 21, 2007 Created.
 * @see org.jhotdraw.draw.DrawingPageable
 */
public class FindAction extends AbstractDocumentViewAction {
    public final static String ID = "Edit.find";
    
    /** Creates a new instance. */
    public FindAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        DocumentView documentView = getCurrentView();
        try {
            Methods.invoke(documentView, "find");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }
}
