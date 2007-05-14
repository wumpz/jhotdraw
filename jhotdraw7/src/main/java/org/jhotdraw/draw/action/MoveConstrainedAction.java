/*
 * @(#)MoveConstrainedAction.java  1.0  2007-04-29
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.undo.CompositeEdit;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * Moves the selected figures by one constrained unit.
 *
 * @author  Werner Randelshofer
 * @version 1.0 17. March 2004  Created.
 */
public abstract class MoveConstrainedAction extends AbstractSelectedAction {
    
    private Direction dir;
    
    /** Creates a new instance. */
    public MoveConstrainedAction(DrawingEditor editor, Direction dir) {
        super(editor);
        this.dir = dir;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Point2D.Double p1 = null;
        Point2D.Double fp;
        
        switch (dir) {
            case EAST :
                for (Figure f : getView().getSelectedFigures()) {
                    Rectangle2D.Double b = f.getBounds();
                    fp = new Point2D.Double(b.x + b.width, b.y + b.height / 2d);
                    if (TRANSFORM.get(f) != null) {
                        TRANSFORM.get(f).transform(fp, fp);
                    }
                    if (p1 == null) {
                        p1 = fp;
                    } else {
                        p1.x = Math.max(fp.x, p1.x);
                    }
                }
                break;
            case WEST :
                for (Figure f : getView().getSelectedFigures()) {
                    Rectangle2D.Double b = f.getBounds();
                    fp = new Point2D.Double(b.x, b.y + b.height / 2d);
                    if (TRANSFORM.get(f) != null) {
                        TRANSFORM.get(f).transform(fp, fp);
                    }
                    if (p1 == null) {
                        p1 = fp;
                    } else {
                        p1.x = Math.min(fp.x, p1.x);
                    }
                }
                break;
            case NORTH :
                for (Figure f : getView().getSelectedFigures()) {
                    Rectangle2D.Double b = f.getBounds();
                    fp = new Point2D.Double(b.x + b.width / 2d, b.y);
                    if (TRANSFORM.get(f) != null) {
                        TRANSFORM.get(f).transform(fp, fp);
                    }
                    if (p1 == null) {
                        p1 = fp;
                    } else {
                        p1.x = Math.min(fp.x, p1.x);
                    }
                }
                break;
            case SOUTH :
                for (Figure f : getView().getSelectedFigures()) {
                    Rectangle2D.Double b = f.getBounds();
                    fp = new Point2D.Double(b.x + b.width / 2d, b.y + b.height);
                    if (TRANSFORM.get(f) != null) {
                        TRANSFORM.get(f).transform(fp, fp);
                    }
                    if (p1 == null) {
                        p1 = fp;
                    } else {
                        p1.x = Math.min(fp.x, p1.x);
                    }
                }
                break;
        }
        
        if (p1 != null) {
            Point2D.Double p2 = (Point2D.Double) p1.clone();
            
            if (getView().getConstrainer() != null) {
                getView().getConstrainer().constrainPoint(p2, dir);
            }
            
            AffineTransform tx = new AffineTransform();
            tx.translate(p2.x - p1.x, p2.y - p1.y);
            for (Figure f : getView().getSelectedFigures()) {
                f.willChange();
                f.transform(tx);
                f.changed();
            }
            CompositeEdit edit;
            fireUndoableEditHappened(new TransformEdit(getView().getSelectedFigures(), tx));
        }
    }
    
    public static class East extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedEast";
        
        public East(DrawingEditor editor) {
            super(editor, Direction.EAST);
            labels.configureAction(this, ID);
        }
    }
    public static class West extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedWest";
        public West(DrawingEditor editor) {
            super(editor, Direction.WEST);
            labels.configureAction(this, ID);
        }
    }
    public static class North extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedNorth";
        public North(DrawingEditor editor) {
            super(editor, Direction.NORTH);
            labels.configureAction(this, ID);
        }
    }
    public static class South extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedSouth";
        public South(DrawingEditor editor) {
            super(editor, Direction.SOUTH);
            labels.configureAction(this, ID);
        }
    }
}
