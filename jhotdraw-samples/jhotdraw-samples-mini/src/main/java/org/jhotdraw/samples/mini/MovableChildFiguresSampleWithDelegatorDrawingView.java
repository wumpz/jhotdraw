/*
 * @(#)MovableChildFiguresSample.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.figure.LabelFigure;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.Figure;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.DragHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.tool.DelegationSelectionTool;

/**
 * Example showing how to create a graphical composite figure which holds
 * component figures that can be moved independently using handles.
 *
 * This version uses the AbstractDrawingView.
 *
 * @author Tobias Warneke
 */
public class MovableChildFiguresSampleWithDelegatorDrawingView {

    private static class LabeledEllipseFigure extends GraphicalCompositeFigure {

        private static final long serialVersionUID = 1L;

        public LabeledEllipseFigure() {
            setPresentationFigure(new EllipseFigure());
            LabelFigure label = new LabelFigure("Label");
            label.transform(new AffineTransform(0, 0, 0, 0, 25, 37));
            add(label);
        }

        /**
         * Return default handles from the presentation figure.
         */
        @Override
        public Collection<Handle> createHandles(int detailLevel) {
            LinkedList<Handle> handles = new LinkedList<Handle>();
            switch (detailLevel) {
                case 0:
                    MoveHandle.addMoveHandles(this, handles);
                    for (Figure child : getChildren()) {
                        MoveHandle.addMoveHandles(child, handles);
                        handles.add(new DragHandle(child));
                    }
                    break;
                case 1:
                    ResizeHandleKit.addResizeHandles(this, handles);
                    break;
                default:
                    break;
            }
            return handles;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create a simple drawing consisting of three
                // text areas and an elbow connection.
                LabeledEllipseFigure ta = new LabeledEllipseFigure();
                ta.setBounds(new Point2D.Double(10, 10), new Point2D.Double(100, 100));
                LabeledEllipseFigure tb = new LabeledEllipseFigure();
                tb.setBounds(new Point2D.Double(220, 120), new Point2D.Double(310, 210));
                LabeledEllipseFigure tc = new LabeledEllipseFigure();
                tc.setBounds(new Point2D.Double(220, 10), new Point2D.Double(310, 100));
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(tc);
                // Create a frame with a drawing view and a drawing editor
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 300);
                final DrawingViewImpl view = new DrawingViewImpl();
                final JPanel drawPanel = new JPanel() {
                    @Override
                    protected void printComponent(Graphics g) {
                        view.printComponent(g);
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        view.paintComponent(g);
                    }

                    @Override
                    public void setBounds(int x, int y, int width, int height) {
                        super.setBounds(x, y, width, height); //To change body of generated methods, choose Tools | Templates.
                        view.fireViewTransformChanged();
                    }
                };
                drawPanel.setSize(500, 500);
                drawPanel.setOpaque(true);
                view.setDrawTo(drawPanel);
                view.setDrawing(drawing);
                f.add(drawPanel);
                f.add(new JLabel("Press space bar to toggle handles."), BorderLayout.SOUTH);
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);
                editor.setTool(new DelegationSelectionTool());
                f.setVisible(true);
            }

            class DrawingViewImpl extends DelegatorDrawingView {

                @Override
                public AffineTransform getDrawingToViewTransform() {
                    AffineTransform transform = new AffineTransform();
                    transform.setToRotation(0.9, getComponent().getWidth() / 2, getComponent().getHeight() / 2);
                    return transform;
                }
            }
        });
    }
}
