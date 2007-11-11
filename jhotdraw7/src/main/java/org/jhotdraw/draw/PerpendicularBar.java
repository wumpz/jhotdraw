/*
 * @(#)PerpendicularBar.java  1.0  2007-11-11
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
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

import java.awt.geom.GeneralPath;

import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

/**
 * A perpendicular line decoration.
 *
 * @author Huw Jones
 */
public class PerpendicularBar extends AbstractLineDecoration implements DOMStorable {
	private double height;
	
    /**
     * Constructs a perpendicular line with a height of 10.
     */
	public PerpendicularBar() {
		this(10);
	}

    /**
     * Constructs a perpendicular line with the given height.
     */
	public PerpendicularBar(double height) {
		super(false, true, false);
		
		this.height = height;
	}
	
    /**
     * Calculates the path of the decorator...a simple line
     * perpendicular to the figure.
     */
    protected GeneralPath getDecoratorPath(Figure f) {
        GeneralPath path = new GeneralPath();
        double halfHeight = height / 2;
        
        path.moveTo((float) +halfHeight, 0);
        path.lineTo((float) -halfHeight, 0);
        
        return path;
    }
    
    /**
     * Calculates the radius of the decorator path.
     */
    protected double getDecoratorPathRadius(Figure f) {
    	return 0.5;
    }
    
    public void read(DOMInput in) {
        height = in.getAttribute("height", 10);
    }
    
    public void write(DOMOutput out) {
        out.addAttribute("height", height);
    }
}