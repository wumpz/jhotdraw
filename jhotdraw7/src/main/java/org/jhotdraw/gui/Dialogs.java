/*
 * @(#)BackgroundTask.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * A utility class for dialogs.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Dialogs {

    /**
     * Prevent instance creation.
     */
    private Dialogs() {

    }

    public static Color showColorChooserDialog(JColorChooser chooser, Component component,
            String title, Color initialColor) throws HeadlessException {

        final JColorChooser pane = chooser;

        Dialogs.ColorTracker ok = new Dialogs.ColorTracker(pane);
        JDialog dialog = JColorChooser.createDialog(component, title, true, pane, ok, null);

        dialog.setVisible(true); // blocks until user brings dialog down...

        return ok.getColor();
    }

    private static class ColorTracker implements ActionListener, Serializable {

        private static final long serialVersionUID = 1L;
        JColorChooser chooser;
        Color color;

        public ColorTracker(JColorChooser c) {
            chooser = c;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            color = chooser.getColor();
        }

        public Color getColor() {
            return color;
        }
    }

}
