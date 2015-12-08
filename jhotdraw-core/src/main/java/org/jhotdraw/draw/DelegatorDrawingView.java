/*
 * Copyright (C) 2015 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;

/**
 * Simple implementation of AbstractDrawingView.
 * @author tw
 */
public abstract class DelegatorDrawingView extends AbstractDrawingView {
    private JComponent drawTo;

    public DelegatorDrawingView(JComponent drawTo) {
        this.drawTo = drawTo;
    }

    public DelegatorDrawingView() {
    }

    public void setDrawTo(JComponent drawTo) {
        this.drawTo = drawTo;
    }

    public JComponent getDrawTo() {
        return drawTo;
    }

    @Override
    public abstract AffineTransform getDrawingToViewTransform();

    @Override
    public void repaint(Rectangle r) {
        drawTo.repaint(r);
    }

    @Override
    public Color getBackground() {
        return drawTo.getBackground();
    }

    @Override
    public void repaint() {
        drawTo.repaint();
    }

    @Override
    public int getWidth() {
        return drawTo.getWidth();
    }

    @Override
    public int getHeight() {
        return drawTo.getHeight();
    }

    @Override
    public void revalidate() {
        drawTo.revalidate();
    }

    @Override
    public void setCursor(Cursor c) {
        drawTo.setCursor(c);
    }

    @Override
    public void requestFocus() {
        drawTo.requestFocus();
    }

    @Override
    public JComponent getComponent() {
        return drawTo;
    }

    @Override
    public double getScaleFactor() {
        return 1;
    }

    @Override
    public void setScaleFactor(double newValue) {
        
    }

    @Override
    public void setEnabled(boolean newValue) {
        drawTo.setEnabled(newValue);
    }

    @Override
    public boolean isEnabled() {
        return drawTo.isEnabled();
    }

    @Override
    public void addMouseListener(MouseListener l) {
        drawTo.addMouseListener(l);
    }

    @Override
    public void removeMouseListener(MouseListener l) {
        drawTo.removeMouseListener(l);
    }

    @Override
    public void addKeyListener(KeyListener l) {
        drawTo.addKeyListener(l);
    }

    @Override
    public void removeKeyListener(KeyListener l) {
        drawTo.removeKeyListener(l);
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener l) {
        drawTo.addMouseMotionListener(l);
    }

    @Override
    public void removeMouseMotionListener(MouseMotionListener l) {
        drawTo.removeMouseMotionListener(l);
    }

    @Override
    public void removeMouseWheelListener(MouseWheelListener l) {
        drawTo.removeMouseWheelListener(l);
    }

    @Override
    public void addMouseWheelListener(MouseWheelListener l) {
        drawTo.addMouseWheelListener(l);
    }
}
