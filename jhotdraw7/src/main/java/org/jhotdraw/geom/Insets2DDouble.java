/*
 * @(#)Insets2DDouble.java  1.1  2006-07-08
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

package org.jhotdraw.geom;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.xml.*;

/**
 * Insets2DDouble.
 *
 * @author  Werner Randelshofer
 * @version 2006-07-08 DOMStorable interface implemented. 
 * <br>1.0 January 14, 2006 Created.
 */
public class Insets2DDouble 
        implements Cloneable, java.io.Serializable, DOMStorable {

    /**
     * The inset from the top.
     * This value is added to the Top of the rectangle
     * to yield a new location for the Top.
     *
     * @serial
     * @see #clone()
     */
    public double top;

    /**
     * The inset from the left.
     * This value is added to the Left of the rectangle
     * to yield a new location for the Left edge.
     *
     * @serial
     * @see #clone()
     */
    public double left;

    /**
     * The inset from the bottom.
     * This value is subtracted from the Bottom of the rectangle
     * to yield a new location for the Bottom.
     *
     * @serial
     * @see #clone()
     */
    public double bottom;

    /**
     * The inset from the right.
     * This value is subtracted from the Right of the rectangle
     * to yield a new location for the Right edge.
     *
     * @serial
     * @see #clone()
     */
    public double right;

    /**
     * Creates and initializes a new <code>Insets2DDouble</code> object with the 
     * 0 insets. 
     * <p>
     * This constructor is required by the DOMStorable interface.
     */
    public Insets2DDouble() {
    }
    /**
     * Creates and initializes a new <code>Insets2DDouble</code> object with the 
     * specified top, left, bottom, and right insets. 
     * @param       top   the inset from the top.
     * @param       left   the inset from the left.
     * @param       bottom   the inset from the bottom.
     * @param       right   the inset from the right.
     */
    public Insets2DDouble(double top, double left, double bottom, double right) {
	this.top = top;
	this.left = left;
	this.bottom = bottom;
	this.right = right;
    }

    /**
     * Checks whether two insets objects are equal. Two instances 
     * of <code>Insets2DDouble</code> are equal if the four integer values
     * of the fields <code>top</code>, <code>left</code>, 
     * <code>bottom</code>, and <code>right</code> are all equal.
     * @return      <code>true</code> if the two insets are equal;
     *                          otherwise <code>false</code>.
     * @since       JDK1.1
     */
    public boolean equals(Object obj) {
	if (obj instanceof Insets2DDouble) {
	    Insets2DDouble insets = (Insets2DDouble)obj;
	    return ((top == insets.top) && (left == insets.left) &&
		    (bottom == insets.bottom) && (right == insets.right));
	}
	return false;
    }

    /**
     * Returns the hash code for this Insets2DDouble.
     *
     * @return    a hash code for this Insets2DDouble.
     */
    public int hashCode() {
        double sum1 = left + bottom;
        double sum2 = right + top;
        double val1 = sum1 * (sum1 + 1)/2 + left;
        double val2 = sum2 * (sum2 + 1)/2 + top;
        double sum3 = val1 + val2;
        return Float.floatToIntBits((float) (sum3 * (sum3 + 1)/2 + val2));
    }

    /**
     * Returns a string representation of this <code>Insets2DDouble</code> object. 
     * This method is intended to be used only for debugging purposes, and 
     * the content and format of the returned string may vary between 
     * implementations. The returned string may be empty but may not be 
     * <code>null</code>.
     * 
     * @return  a string representation of this <code>Insets2DDouble</code> object.
     */
    public String toString() {
	return getClass().getName() + "[top="  + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
    }

    /**
     * Create a copy of this object.
     * @return     a copy of this <code>Insets2DDouble</code> object.
     */
    public Object clone() { 
	try { 
	    return super.clone();
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();

    public void write(DOMOutput out) {
        out.addAttribute("top", top, 0d);
        out.addAttribute("left", left, 0d);
        out.addAttribute("bottom", bottom, 0d);
        out.addAttribute("right", right, 0d);
    }

    public void read(DOMInput in) {
        top = in.getAttribute("top", 0d);
        left = in.getAttribute("left", 0d);
        bottom = in.getAttribute("bottom", 0d);
        right = in.getAttribute("right", 0d);
    }

}
