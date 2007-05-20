/*
 * @(#)ViewSourceAction.java  1.0  19. Mai 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.svg.action;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import javax.swing.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.io.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * ViewSourceAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 19. Mai 2007 Created.
 */
public class ViewSourceAction extends AbstractProjectAction {
    private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
    
    public final static String ID = "viewSource";
    
    /** Creates a new instance. */
    public ViewSourceAction(Application app) {
        super(app);
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        SVGProject p = (SVGProject) getCurrentProject();
        SVGOutputFormat format = new SVGOutputFormat();
        format.setPrettyPrint(true);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            format.write(buf, p.getDrawing());
            String source = buf.toString("UTF-8");
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(p.getComponent())
                    );
            dialog.setTitle(labels.getFormatted(ID+".dialogTitle",p.getFile() == null ?
                "unnamed" : p.getFile().getName()));
            dialog.setResizable(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            JTextArea ta = new JTextArea(source);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            JScrollPane sp = new JScrollPane(ta);
            //sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            dialog.getContentPane().add(sp);
            dialog.setSize(400, 400);
            dialog.setLocationByPlatform(true);
            dialog.setVisible(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
