/**
 * @(#)HarmonicColorWheel.java  1.0  April 19, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.beans.*;
import java.util.ArrayList;
import javax.swing.event.*;

/**
 * HarmonicColorWheel.
 *
 * @author Werner Randelshofer
 * @version 1.0 HarmonicColorWheel Created.
 */
public class HarmonicColorWheel extends ColorWheel {

    private static class Segment {

        public float hMin;
        public float hMax;
        public float sMin;
        public float sMax;
        public float bMin;
        public float bMax;
        public String name;

        public Segment(String name, int index) {
            this.name = name;
            index = index - 1;
            this.hMin = (index / 8 - 0.5f) / 12f;
            this.hMax = (index / 8 + 0.5f) / 12f;
            if (index % 8 < 4) {
                this.sMin = 1;
                this.sMax = 1;
                this.bMin = .5f + (index % 8) / 10f;
                this.bMax = .5f + (index % 8 + 1) / 10f;
            } else {
                this.sMin = 1f - (index % 8 + 1) / 5f;
                this.sMax = 1f - (index % 8) / 5f;
                this.bMin = 1;
                this.bMax = 1;
            }
        }

        public Segment(String name, float hmin, float hmax, float smin, float smax, float bmin, float bmax) {
            this.name = name;
            this.hMin = hmin;
            this.hMax = hmax;
            this.sMin = smin;
            this.sMax = smax;
            this.bMin = bmin;
            this.bMax = bmax;
        }

        public boolean contains(float h, float s, float b) {
            return hMin <= h && h <= hMax &&
                    sMin <= s && s <= sMax &&
                    bMin <= b && b <= bMax;
        }

        public boolean contains(float[] hsb) {
            float h = hsb[0];
            h = h - (float) Math.floor(h);
            if (h < 0) {
                h = 1 + h;
            }

            if (hMin < 0) {

                return (1f + hMin <= h || h <= hMax) &&
                        sMin <= hsb[1] && hsb[1] <= sMax &&
                        bMin <= hsb[2] && hsb[2] <= bMax;
            } else {

                return hMin <= h && h <= hMax &&
                        sMin <= hsb[1] && hsb[1] <= sMax &&
                        bMin <= hsb[2] && hsb[2] <= bMax;
            }
        }
    }
    private ArrayList<Segment> emotions;
    private HarmonicColorModel harmonicModel;
    private int selectedIndex = -1;
    private float handleRadius = 4f;
    private float baseRadius = 7f;

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
            int x = e.getX();
            int y = e.getY();
            int closestIndex = -1;
            if (harmonicModel != null && harmonicModel.size() > 0) {
                int closestError = Integer.MAX_VALUE;
                for (int i = 0,  n = harmonicModel.size(); i < n; i++) {
                    CompositeColor c = harmonicModel.get(i);
                    if (c != null) {
                        Point p = getColorLocation(harmonicModel.get(i));
                        int error = (p.x - x) * (p.x - x) +
                                (p.y - y) * (p.y - y);
                        if (error < closestError) {
                            closestIndex = i;
                            closestError = error;
                        }
                    }
                }
                if (closestIndex != -1) {
                    if (closestError > 20) {
                        closestIndex = -1;
                    }
                }
            }
            selectedIndex = closestIndex;
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
        //update(e);
        }

        private void update(MouseEvent e) {
            if (selectedIndex != -1) {
                float[] hsb = getColorAt(e.getX(), e.getY());
                //if (hsb != null) {
                harmonicModel.set(selectedIndex, new CompositeColor(harmonicModel.getColorSystem(), hsb));
                //}
                repaint();
            }
        }
    }
    private MouseHandler mouseHandler;
    private class ModelHandler implements PropertyChangeListener, ListDataListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == HarmonicColorModel.COLOR_SYSTEM_PROPERTY) {
                colorWheelProducer = createWheelProducer(getWidth(), getHeight());
            }
            repaint();
        }

        public void intervalAdded(ListDataEvent e) {
            repaint();
        }

        public void intervalRemoved(ListDataEvent e) {
            repaint();
        }

        public void contentsChanged(ListDataEvent e) {
            repaint();
        }
        
    }
    private ModelHandler modelHandler;

    /** Creates new form. */
    public HarmonicColorWheel() {
        initComponents();
        getModel().getBoundedRangeModel(2).setValue(100);

        setWheelInsets(new Insets(5, 5, 5, 5));
        
        modelHandler = new ModelHandler();

        DefaultHarmonicColorModel p = new DefaultHarmonicColorModel();
        setHarmonicColorModel(p);
        setToolTipText("");

        emotions = new ArrayList<Segment>();
        emotions.add(new Segment("Powerful", 4));
        emotions.add(new Segment("Rich", 1));
        emotions.add(new Segment("Romantic", 7));
        emotions.add(new Segment("Vital", 12));
        emotions.add(new Segment("Earthy", 10));
        emotions.add(new Segment("Friendly", 20));
        emotions.add(new Segment("Soft", 22));
        emotions.add(new Segment("Welcoming", 28));
        emotions.add(new Segment("Moving", 36));
        emotions.add(new Segment("Elegant", 39));
        emotions.add(new Segment("Fresh", 52));
        emotions.add(new Segment("Traditional", 49));
        emotions.add(new Segment("Refreshing", 60));
        emotions.add(new Segment("Tropical", 62));
        emotions.add(new Segment("Classic", 68));
        emotions.add(new Segment("Dependable", 65));
        emotions.add(new Segment("Calm", 70));
        emotions.add(new Segment("Regal", 76));
        emotions.add(new Segment("Magical", 84));
        emotions.add(new Segment("Energetic", 92));
        emotions.add(new Segment("Subdued", 94));
    }

    public HarmonicColorModel getHarmonicColorModel() {
        return harmonicModel;
    }

    public String getToolTipText(MouseEvent evt) {
        float[] hsb = getColorAt(evt.getX(), evt.getY());
        if (hsb == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        buf.append(Math.round(hsb[0] * 360));
        buf.append(",");
        buf.append(Math.round(hsb[1] * 100f));
        buf.append(",");
        buf.append(Math.round(hsb[2] * 100f));
        for (Segment s : emotions) {
            if (s.contains(hsb)) {
                buf.append("<br>");
                buf.append(s.name);
            }
        }

        if (buf.length() > 0) {
            buf.insert(0, "<html>");

            return buf.toString();
        } else {
            return null;
        }
    }

    @Override
    protected void installMouseListeners() {
        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setHarmonicColorModel(HarmonicColorModel newValue) {
        HarmonicColorModel oldValue = harmonicModel;
        if (oldValue != null) {
            oldValue.removePropertyChangeListener(modelHandler);
            oldValue.removeListDataListener(modelHandler);
        }
        harmonicModel = newValue;
        if (newValue != null) {
           newValue.addPropertyChangeListener(modelHandler);
           newValue.addListDataListener(modelHandler);
           colorWheelProducer = createWheelProducer(getWidth(), getHeight());
        }
    }

    @Override
    protected ColorWheelImageProducer createWheelProducer(int w, int h) {
        return new HSLHarmonicColorWheelImageProducer(harmonicModel == null ? new HSLRYBColorSystem() : harmonicModel.getColorSystem(), w, h);
    }

    @Override
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        super.paintComponent(g);
    }

    @Override
    protected void paintThumb(Graphics2D g) {
        if (harmonicModel != null) {
            Point center = getCenter();
            Ellipse2D.Float oval = new Ellipse2D.Float(0, 0, 0, 0);

            float[] comp = null;
            float wheelBrightness = model.getBoundedRangeModel(2).getValue() / 100f;
            for (int i = 0,  n = harmonicModel.size(); i < n; i++) {
                if (harmonicModel.get(i) != null) {
                    Point p = getColorLocation(harmonicModel.get(i));
                    g.setColor(Color.black);
                    g.drawLine(center.x, center.y, p.x, p.y);
                }
            }
            for (int i = 0,  n = harmonicModel.size(); i < n; i++) {
                if (harmonicModel.get(i) != null) {
                    Point p = getColorLocation(harmonicModel.get(i));
                    CompositeColor mixerColor = harmonicModel.get(i);
                    comp = mixerColor.getComponents();
                    if (i == selectedIndex) {
                        g.setColor(Color.white);
                        oval.x = p.x - baseRadius;
                        oval.y = p.y - baseRadius;
                        oval.width = baseRadius * 2f;
                        oval.height = baseRadius * 2f;
                        g.fill(oval);
                    }
                    g.setColor(mixerColor.getColor());
                    oval.x = p.x - handleRadius;
                    oval.y = p.y - handleRadius;
                    oval.width = handleRadius * 2f;
                    oval.height = handleRadius * 2f;
                    g.fill(oval);
                    g.setColor(Color.black);
                    g.draw(oval);
                    if (i == harmonicModel.getBase()) {
                        oval.x = p.x - baseRadius;
                        oval.y = p.y - baseRadius;
                        oval.width = baseRadius * 2f;
                        oval.height = baseRadius * 2f;
                        g.draw(oval);
                    }
                   // g.drawString(i+"", p.x, p.y);
                }
            }
        }
    }

    protected Point getColorLocation(CompositeColor c) {
        Point p = colorWheelProducer.getColorLocation(c,
                getWidth() - wheelInsets.left - wheelInsets.right,
                getHeight() - wheelInsets.top - wheelInsets.bottom);
        p.x += wheelInsets.left;
        p.y += wheelInsets.top;
        return p;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
