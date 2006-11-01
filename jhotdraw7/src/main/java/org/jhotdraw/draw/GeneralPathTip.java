/*
 * @(#)GeneralPathLineDecoration.java  1.0  4. Februar 2004
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

package org.jhotdraw.draw;

import java.awt.geom.*;
import java.awt.*;
/**
 * GeneralPathLineDecoration.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 4. Februar 2004  Created.
 */
public class GeneralPathTip extends AbstractLineDecoration {
    private GeneralPath path;
    double decorationRadius;
    
    /** Creates a new instance. */
    public GeneralPathTip(GeneralPath path, double decorationRadius) {
        this(path, decorationRadius, false, true, false);
    }
    public GeneralPathTip(GeneralPath path, double decorationRadius, boolean isFilled, boolean isStroked, boolean isSolid) {
        super(isFilled, isStroked, isSolid);
        this.path = path;
        this.decorationRadius = decorationRadius;
    }
    
    protected GeneralPath getDecoratorPath(org.jhotdraw.draw.Figure f) {
        return (GeneralPath) path.clone();
    }
    
    protected double getDecoratorPathRadius(org.jhotdraw.draw.Figure f) {
        return decorationRadius;
    }
}
