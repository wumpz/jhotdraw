/*
 * @(#)JComponentPopup.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 * 
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.gui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * This is an extension of the Swing {@code JPopupMenu} which can be used to
 * display a {@code JComponent} in a popup menu.
 * <p>
 * Unlike {@code JPopupMenu}, the popup will stay open if the {@code JComponent}
 * opens a popup menu of its own.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class JComponentPopup extends JPopupMenu {

    private class Handler implements AWTEventListener {

        @Override
        public void eventDispatched(AWTEvent ev) {
            if (!(ev instanceof MouseEvent) || !(ev.getSource() instanceof Component)) {
                // We are interested in MouseEvents only
                return;
            }
            MouseEvent me = (MouseEvent) ev;
            Component src = (Component) ev.getSource();
            Component invoker = JComponentPopup.this.getInvoker();

            if (ev.getID() == MouseEvent.MOUSE_PRESSED) {
                // Close popup if the mouse press occured on a component which is
                // not descending from this popup menu, but has the same
                // window ancestor.
                if (!SwingUtilities.isDescendingFrom(src, JComponentPopup.this)
                        && SwingUtilities.getWindowAncestor(src)
                        == SwingUtilities.getWindowAncestor(invoker)) {
                    JLayeredPane srcLP = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, src);
                    Component srcLPChild = src;
                    while (srcLPChild.getParent() != srcLP) {
                        srcLPChild = srcLPChild.getParent();
                    }
                    if (srcLPChild == null || srcLP.getLayer(srcLPChild) < JLayeredPane.POPUP_LAYER) {
                        JComponentPopup.this.setVisible(false);
                    }
                }
            } else if (ev.getID() == MouseEvent.MOUSE_CLICKED) {
                // Close popup if a double click occured on the popup component.
                if (me.getClickCount() == 2
                        && SwingUtilities.isDescendingFrom(src, JComponentPopup.this)) {
                    JComponentPopup.this.setVisible(false);
                }
            }
        }
    };
    private Handler handler = new Handler();

    public JComponentPopup() {
        setLightWeightPopupEnabled(false);


    }

    @Override
    public void menuSelectionChanged(boolean isIncluded) {
        // Don't let the MenuSelectionManager hide this popup.
        return;


    }

    @Override
    public void setVisible(boolean newValue) {
        // Attach/detach AWTEventListener on "visible" property change.
        if (isVisible() != newValue) {
            if (newValue) {
                Toolkit.getDefaultToolkit().addAWTEventListener(handler, AWTEvent.MOUSE_EVENT_MASK);


            } else {
                Toolkit.getDefaultToolkit().removeAWTEventListener(handler);


            }
            super.setVisible(newValue);

        }
    }
}
