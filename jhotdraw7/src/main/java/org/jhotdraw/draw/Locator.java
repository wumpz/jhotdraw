/*
 * @(#)Locator.java  2.0  2006-01-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */


package org.jhotdraw.draw;

import java.awt.geom.*;
/**
 * Locators can be used to locate a position on a figure.<p>
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Locator {

	/**
	 * Locates a position on the provided figure.
	 * @return a point on the figure.
	 */
	public Point2D.Double locate(Figure owner);
	/**
	 * Locates a position on the provided figure relative to the dependent
         * figure.
	 * @return a point on the figure.
	 */
	public Point2D.Double locate(Figure owner, Figure dependent);
}