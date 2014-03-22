/*
 * @(#)CrossPlatformApplication.java
 * 
 * Copyright (c) 2013 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the  
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */

package org.jhotdraw.app;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * {@code CrossPlatformApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CrossPlatformApplication extends SDIApplication {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        super.init();
        ResourceBundleUtil.putPropertyNameModifier("os", "other", "default");
    }

    @Override
    protected void initLookAndFeel() {
        try {
            String lafName = UIManager.getCrossPlatformLookAndFeelClassName();
            
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            
            UIManager.setLookAndFeel(lafName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (UIManager.getString("OptionPane.css") == null) {
            UIManager.put("OptionPane.css", "<head>"
                    + "<style type=\"text/css\">"
                    + "b { font: 13pt \"Dialog\" }"
                    + "p { font: 11pt \"Dialog\"; margin-top: 8px }"
                    + "</style>"
                    + "</head>");
        }
    }

}
