/*
 * @(#)LabelFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import javax.annotation.Nullable;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.tool.TextEditingTool;
import java.awt.geom.*;
import java.util.*;

/**
 * A LabelFigure can be used to provide more double clickable area for a
 * TextHolderFigure.
 *
 * FIXME - Move FigureListener into inner class.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class LabelFigure extends TextFigure implements FigureListener {
    private static final long serialVersionUID = 1L;
    @Nullable private TextHolderFigure target;
    
    /** Creates a new instance. */
    public LabelFigure() {
        this("Label");
    }
    public LabelFigure(String text) {
        setText(text);
        setEditable(false);
    }
    
    public void setLabelFor(TextHolderFigure target) {
        if (this.target != null) {
            this.target.removeFigureListener(this);
        }
        this.target = target;
        if (this.target != null) {
            this.target.addFigureListener(this);
        }
    }
    @Override
    public TextHolderFigure getLabelFor() {
        return (target == null) ? this : target;
    }
    
    /**
     * Returns a specialized tool for the given coordinate.
     * <p>Returns null, if no specialized tool is available.
     */
    @Override
    public Tool getTool(Point2D.Double p) {
        return (target != null && contains(p)) ? new TextEditingTool(target) : null;
    }
    
    
    @Override
    public void areaInvalidated(FigureEvent e) {
    }
    
    @Override
    public void attributeChanged(FigureEvent e) {
    }
    
    @Override
    public void figureAdded(FigureEvent e) {
    }
    
    @Override
    public void figureChanged(FigureEvent e) {
    }
    
    @Override
    public void figureRemoved(FigureEvent e) {
        if (e.getFigure() == target) {
            target.removeFigureListener(this);
            target = null;
        }
    }
    
    @Override
    public void figureRequestRemove(FigureEvent e) {
    }
    
    @Override
    public void remap(Map<Figure,Figure> oldToNew, boolean disconnectIfNotInMap) {
        super.remap(oldToNew, disconnectIfNotInMap);
        if (target != null) {
            Figure newTarget = oldToNew.get(target);
            if (newTarget != null) {
                target.removeFigureListener(this);
                target = (TextHolderFigure) newTarget;
                newTarget.addFigureListener(this);
            }
        }
    }

    @Override
    public void figureHandlesChanged(FigureEvent e) {
    }
}
