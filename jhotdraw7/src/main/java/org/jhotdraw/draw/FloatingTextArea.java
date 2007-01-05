/*
 *  @(#)FloatingTextArea.java 1.0.1 2006-02-27
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
package org.jhotdraw.draw;

import java.awt.geom.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * A FloatingTextArea overlays an editor on top of an area in a drawing.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2006-02-27 Derived from JHotDraw 5.4b1.
 */
public class FloatingTextArea {
    /**
     * A scroll pane to allow for vertical scrolling while editing
     */
    protected JScrollPane editScrollContainer;
    /**
     * The actual editor
     */
    protected JTextArea editWidget;
    /**
     * The drawing view.
     */
    protected DrawingView view;
    
    
    /**
     * Constructor for the FloatingTextArea object
     */
    public FloatingTextArea() {
        editWidget = new JTextArea();
        editWidget.setWrapStyleWord(true);
        editWidget.setLineWrap(true);
        editScrollContainer = new JScrollPane(editWidget,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editScrollContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        editScrollContainer.setBorder(BorderFactory.createLineBorder(Color.black));
    }
    
    
    /**
     * Creates the overlay within the given container.
     * @param view the DrawingView
     */
    public void createOverlay(DrawingView view) {
        createOverlay(view, null);
    }
    
    public void requestFocus() {
        editWidget.requestFocus();
    }
    
    /**
     * Creates the overlay for the given Container using a
     * specific font.
     * @param view the DrawingView
     * @param figure the figure holding the text
     */
    public void createOverlay(DrawingView view, TextHolderFigure figure) {
        view.getComponent().add(editScrollContainer, 0);
        if (figure != null) {
        Font f = figure.getFont();
        // FIXME - Should scale with fractional value!
        f = f.deriveFont(f.getStyle(), (float) (figure.getFontSize() * view.getScaleFactor()));
        editWidget.setFont(f);
            editWidget.setForeground(figure.getTextColor());
            editWidget.setBackground(figure.getFillColor());
            editWidget.setTabSize(figure.getTabSize());
        }
        this.view = view;
    }
    
    
    /**
     * Positions and sizes the overlay.
     * @param r the bounding Rectangle2D.Double for the overlay
     * @param text the text to edit
     */
    public void setBounds(Rectangle2D.Double r, String text) {
        editWidget.setText(text);
        editScrollContainer.setBounds(view.drawingToView(r));
        editScrollContainer.setVisible(true);
        editWidget.setCaretPosition(0);
        editWidget.requestFocus();
    }
    
    
    /**
     * Gets the text contents of the overlay.
     * @return The text value
     */
    public String getText() {
        return editWidget.getText();
    }
    
    
    /**
     * Gets the preferred size of the overlay.
     * @param cols Description of the Parameter
     * @return The preferredSize value
     */
    public Dimension getPreferredSize(int cols) {
        return new Dimension(editWidget.getWidth(), editWidget.getHeight());
    }
    
    
    /**
     * Removes the overlay.
     */
    public void endOverlay() {
        view.getComponent().requestFocus();
        if (editScrollContainer != null) {
            editScrollContainer.setVisible(false);
            view.getComponent().remove(editScrollContainer);
            
            Rectangle bounds = editScrollContainer.getBounds();
            view.getComponent().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
