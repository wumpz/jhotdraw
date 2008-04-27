/*
 * @(#)ColorWheel.java  1.0  August 27, 2005
 *
 * Copyright (c) 2007-2008 by the original authors of AnyWikiDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the AnyWikiDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * The ColorWheel displays a hue/saturation wheel of an HSL or an HSV ColorSystem. 
 * The user can click at the wheel to pick a color on the ColorWheel. 
 * The ColorWheel should be used together with a color slider for HSL luminance
 * or HSV value.
 *
 * @author  Werner Randelshofer
 * @version 1.0 August 27, 2005 Created.
 */
public class ColorWheel extends JPanel {

    protected Insets wheelInsets;
    private Image colorWheelImage;
    protected ColorWheelImageProducer colorWheelProducer;
    protected ColorSliderModel model;

    private class MouseHandler implements MouseListener, MouseMotionListener {
        public void mouseClicked(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            update(e);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            update(e);
        }

        public void mouseReleased(MouseEvent e) {
            update(e);
        }

        private void update(MouseEvent e) {
            float[] hsb = getColorAt(e.getX(), e.getY());
            model.setComponentValue(0, hsb[0]);
            model.setComponentValue(1, hsb[1]);

            // FIXME - We should only repaint the damaged area
            repaint();
        }
    }
    private MouseHandler mouseHandler;

    private class ModelHandler implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            repaint();
        }
    }
    private ModelHandler modelHandler;

    /**
     * Creates a new instance.
     */
    public ColorWheel() {
        wheelInsets = new Insets(0,0,0,0);
        model = new DefaultColorSliderModel(new HSVRGBColorSystem());
        initComponents();
        colorWheelProducer = createWheelProducer(0, 0);
        modelHandler = new ModelHandler();
        model.addChangeListener(modelHandler);
        installMouseListeners();
        setOpaque(false);
    }
    
    protected void installMouseListeners() {
        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setModel(ColorSliderModel m) {
        if (model != null) {
            model.removeChangeListener(modelHandler);
        }
        model = m;
        if (model != null) {
            model.addChangeListener(modelHandler);
        colorWheelProducer = createWheelProducer(getWidth(), getHeight());
            repaint();
        }
    }
    
    public void setWheelInsets(Insets newValue) {
        wheelInsets = newValue;
        repaint();
    }
    public Insets getWheelInsets() {
        return wheelInsets;
    }

    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }

    public ColorSliderModel getModel() {
        return model;
    }

    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        paintWheel(g);
        paintThumb(g);
    }

    protected ColorWheelImageProducer createWheelProducer(int w, int h) {
        return new ColorWheelImageProducer(model.getColorSystem(), w, h);
    }
    
    protected void paintWheel(Graphics2D g) {
        int w = getWidth() - wheelInsets.left - wheelInsets.right;
        int h = getHeight() - wheelInsets.top - wheelInsets.bottom;

        if (colorWheelImage == null || colorWheelImage.getWidth(this) != w || colorWheelImage.getHeight(this) != h) {
            if (colorWheelImage != null) {
                colorWheelImage.flush();
            }
            colorWheelProducer = createWheelProducer(w, h);
            colorWheelImage = createImage(colorWheelProducer);
        }

        colorWheelProducer.setBrightness(model.getComponentValue(2));
        colorWheelProducer.regenerateColorWheel();

        g.drawImage(colorWheelImage, wheelInsets.left, wheelInsets.top, this);
    }

    protected void paintThumb(Graphics2D g) {
        Point p = getThumbLocation();

        g.setColor(Color.white);
        g.fillRect(p.x - 1, p.y - 1, 2, 2);
        g.setColor(Color.black);
        g.drawRect(p.x - 2, p.y - 2, 3, 3);
    }

    protected Point getCenter() {
        int w = getWidth() - wheelInsets.left - wheelInsets.right;
        int h = getHeight() - wheelInsets.top - wheelInsets.bottom;

        return new Point(
                wheelInsets.left + w / 2,
                wheelInsets.top + h / 2);
    }
    protected Point getThumbLocation() {
        return getColorLocation(
                model.getComponentValue(0),
                model.getComponentValue(1),
                model.getComponentValue(2));
    }

    protected Point getColorLocation(Color c) {
        Point p = colorWheelProducer.getColorLocation(c, 
                getWidth() - wheelInsets.left - wheelInsets.right,
                getHeight() - wheelInsets.top - wheelInsets.bottom);
        p.x += wheelInsets.left;
        p.y += wheelInsets.top;
        return p;
    }

    protected Point getColorLocation(float hue, float saturation, float brightness) {
        Point p = colorWheelProducer.getColorLocation(hue, saturation, brightness, 
                getWidth() - wheelInsets.left - wheelInsets.right,
                getHeight() - wheelInsets.top - wheelInsets.bottom);
        p.x += wheelInsets.left;
        p.y += wheelInsets.top;
        return p;
    }

    protected float[] getColorAt(int x, int y) {
float[] cc = colorWheelProducer.getColorAt(x - wheelInsets.left, y - wheelInsets.top,
                getWidth() - wheelInsets.left - wheelInsets.right,
                getHeight() - wheelInsets.top - wheelInsets.bottom);        
        return cc;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
