/**
 * @(#)WheelsAndSlidersMain.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.color;

import org.jhotdraw.color.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A demo of color wheels and color sliders using all kinds of color systems.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class WheelsAndSlidersMain extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;

    private Color color;
    private JLabel colorLabel;
    private LinkedList<ColorSliderModel> models;

    private class Handler implements ChangeListener {

        private int adjusting;

        @Override
        public void stateChanged(ChangeEvent e) {
            if (adjusting++ == 0) {
                ColorSliderModel m = (ColorSliderModel) e.getSource();
                color = m.getColor();
                previewLabel.setBackground(color);
                for (ColorSliderModel c : models) {
                    if (c != m) {
                        if (c.getColorSpace().equals(m.getColorSpace())) {
                            // If the color system is the same, directly set the components (=lossless)
                            for (int i = 0; i < m.getComponentCount(); i++) {
                                c.setComponent(i, m.getComponent(i));
                            }
                        } else {
                            // If the color system is different, set the RGB color (=lossy)
                            c.setColor(color);
                        }
                    }
                }
            }
            adjusting--;
        }
    }
    private Handler handler;

    /**
     * Creates new form.
     */
    public WheelsAndSlidersMain() {
        initComponents();

        models = new LinkedList<ColorSliderModel>();
        handler = new Handler();

        previewLabel.setOpaque(true);

        // RGB panels
        chooserPanel.add(createSliderChooser(ColorSpace.getInstance(ColorSpace.CS_sRGB)));
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_sRGB), 0, 1, 2, JColorWheel.Type.SQUARE));
        //chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_LINEAR_RGB), 0, 1, 2, JColorWheel.Type.SQUARE));

        // CMYK
        //chooserPanel.add(createSliderChooser(CMYKGenericColorSpace.getInstance()));
        chooserPanel.add(createSliderChooser(CMYKNominalColorSpace.getInstance()));

        // Empty panel
        chooserPanel.add(new JPanel());

        
        // HSB, HSV, HSL, ... variants
        chooserPanel.add(createColorWheelChooser(HSBColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSVColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLColorSpace.getInstance(), 0, 2, 1));
        chooserPanel.add(createColorWheelChooser(HSVPhysiologicColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLPhysiologicColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLPhysiologicColorSpace.getInstance(), 0, 2, 1));
        chooserPanel.add(new JPanel());

        // CIELAB
        ColorSpace cs;
        cs = new CIELABColorSpace();
        chooserPanel.add(createColorWheelChooser(cs, 1, 2, 0, JColorWheel.Type.SQUARE));
        cs = new CIELCHabColorSpace();
        chooserPanel.add(createColorWheelChooser(cs, 2, 1, 0, JColorWheel.Type.POLAR));


        // CIEXYZ
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_CIEXYZ), 1, 0, 2, JColorWheel.Type.SQUARE));
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_PYCC), 1, 2, 0, JColorWheel.Type.SQUARE));

    }

    private JPanel createColorWheelChooser(ColorSpace sys) {
        return createColorWheelChooser(sys, 0, 1, 2);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex) {
        return createColorWheelChooser(sys, angularIndex, radialIndex, verticalIndex, JColorWheel.Type.POLAR);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex, JColorWheel.Type type) {
        return createColorWheelChooser(sys, angularIndex, radialIndex, verticalIndex, type, false, false);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex, JColorWheel.Type type, boolean flipX, boolean flipY) {
        JPanel p = new JPanel(new BorderLayout());
        final DefaultColorSliderModel m = new DefaultColorSliderModel(sys);
        models.add(m);
        m.addChangeListener(handler);
        JColorWheel w = new JColorWheel();
        w.setType(type);
        w.setAngularComponentIndex(angularIndex);
        w.setRadialComponentIndex(radialIndex);
        w.setVerticalComponentIndex(verticalIndex);
        w.setFlipX(flipX);
        w.setFlipY(flipY);
        w.setModel(m);
        JSlider s = new JSlider(JSlider.VERTICAL);
        m.configureSlider(verticalIndex, s);
        p.add(new JLabel("<html>" + ColorUtil.getName(sys) + "<br>α:" + angularIndex + " r:" + radialIndex + " v:" + verticalIndex), BorderLayout.NORTH);
        p.add(w, BorderLayout.CENTER);
        p.add(s, BorderLayout.EAST);

        JPanel pp = new JPanel();
        p.add(pp, BorderLayout.SOUTH);
        for (int i = 0; i < m.getComponentCount(); i++) {
            final int comp = i;
            final JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setColumns(4);
            ChangeListener cl=new ChangeListener() {
                NumberFormat df = NumberFormat.getNumberInstance();

                @Override
                public void stateChanged(ChangeEvent e) {
                    df.setMaximumFractionDigits(3);
                    tf.setText(df.format(m.getComponent(comp)));
                }
            };
            cl.stateChanged(null);
            m.addChangeListener(cl);
            pp.add(tf);
        }
        return p;
    }

    private JPanel createSliderChooser(ColorSpace sys) {
        return createSliderChooser(sys, false);
    }

    private JPanel createSliderChooser(ColorSpace sys, boolean vertical) {
        JPanel p = new JPanel(new GridBagLayout());
        final DefaultColorSliderModel m = new DefaultColorSliderModel(sys);

        models.add(m);
        GridBagConstraints gbc = new GridBagConstraints();
        if (!vertical) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            p.add(new JLabel(
                    "<html>" + ColorUtil.getName(sys)), gbc);
        }
        m.addChangeListener(handler);


        for (int i = 0; i < m.getComponentCount(); i++) {
            final int comp = i;
            JSlider s = new JSlider(JSlider.HORIZONTAL);
            s.setMajorTickSpacing(50);
            s.setPaintTicks(true);
            s.setOrientation(vertical ? JSlider.VERTICAL : JSlider.HORIZONTAL);
            m.configureSlider(comp, s);
            if (vertical) {
                gbc.gridx = i;
                gbc.gridy = 0;
            } else {
                gbc.gridy = i + 1;
                gbc.gridx = 0;
            }
            p.add(s, gbc);
            final JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setColumns(4);
            ChangeListener cl=new ChangeListener() {
                NumberFormat df = NumberFormat.getNumberInstance();

                @Override
                public void stateChanged(ChangeEvent e) {
                    df.setMaximumFractionDigits(3);
                    tf.setText(df.format(m.getComponent(comp)));
                }
            };
            cl.stateChanged(null);
            m.addChangeListener(cl);
            if (vertical) {
                gbc.gridx = i;
                gbc.gridy = 1;
            } else {
                gbc.gridy = i + 1;
                gbc.gridx = 1;
            }
            p.add(tf, gbc);
        }
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("Color Wheels, Squares and Sliders");
                f.add(new WheelsAndSlidersMain());
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.pack();
                f.setVisible(true);


            }
        });


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooserPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        chooserPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chooserPanel.setLayout(new java.awt.GridLayout(0, 4, 10, 10));
        add(chooserPanel, java.awt.BorderLayout.CENTER);

        previewLabel.setText("Selected Color");
        add(previewLabel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chooserPanel;
    private javax.swing.JLabel previewLabel;
    // End of variables declaration//GEN-END:variables
}
