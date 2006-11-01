/*
 * @(#)DependencyFigure.java  1.0  18. Juni 2006
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

package org.jhotdraw.samples.pert.figures;

import java.awt.*;
import org.jhotdraw.samples.*;
import java.beans.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;

/**
 * DependencyFigure.
 *
 * @author Werner Randelshofer.
 * @version 1.0 18. Juni 2006 Created.
 */
public class DependencyFigure extends LineConnectionFigure {
    /** Creates a new instance. */
    public DependencyFigure() {
        STROKE_COLOR.set(this, new Color(0x000099));
        STROKE_WIDTH.set(this, 1d);
        END_DECORATION.set(this, new ArrowTip());
        
        setAttributeEnabled(END_DECORATION, false);
        setAttributeEnabled(START_DECORATION, false);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINED, false);
    }
    
    /**
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     */
    public boolean canConnect(Figure start, Figure end) {
        if ((start instanceof TaskFigure)
        && (end instanceof TaskFigure)) {
            
            TaskFigure sf = (TaskFigure) start;
            TaskFigure ef = (TaskFigure) end;
            
            // Disallow multiple connections to same dependent
            if (ef.getPredecessors().contains(sf)) {
                return false;
            }
            
            // Disallow cyclic connections
            return ! sf.isDependentOf(ef);
        }
        
        return false;
    }
    public boolean canConnect(Figure start) {
        return (start instanceof TaskFigure);
    }
    
    
    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    protected void handleDisconnect(Figure start, Figure end) {
        TaskFigure sf = (TaskFigure) start;
        TaskFigure ef = (TaskFigure) end;
        
        sf.removeDependency(this);
        ef.removeDependency(this);
    }
    
    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    protected void handleConnect(Figure start, Figure end) {
        TaskFigure sf = (TaskFigure) start;
        TaskFigure ef = (TaskFigure) end;
        
        sf.addDependency(this);
        ef.addDependency(this);
    }
    
    public DependencyFigure clone() {
        DependencyFigure that = (DependencyFigure) super.clone();
        
        return that;
    }
    
    public int getLayer() {
        return 1;
    }
    
    @Override public void removeNotify(Drawing d) {
        if (getStartFigure() != null) {
            ((TaskFigure) getStartFigure()).removeDependency(this);
        }
        if (getEndFigure() != null) {
            ((TaskFigure) getEndFigure()).removeDependency(this);
        }
        super.removeNotify(d);
    }
}
