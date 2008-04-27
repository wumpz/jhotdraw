/*
 * @(#)AbstractColorSystem.java  1.0  May 22, 2005
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.color;

import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.io.*;
import java.util.*;
/**
 * Abstract super class for ColorSystem's.
 *
 * @author  Werner Randelshofer
 * @version 1.0 May 22, 2005 Created.
 */
public abstract class AbstractColorSystem implements ColorSystem {
    public float[] toComponents(int rgb, float[] components) {
        return toComponents((rgb & 0xff0000) >> 16, (rgb & 0xff00) >> 8, rgb & 0xff, components);
    }
    
    public abstract float[] toComponents(int r, int g, int b, float[] components);
}
