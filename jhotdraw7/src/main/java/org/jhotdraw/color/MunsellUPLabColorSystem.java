/*
 * @(#)MunsellUPLabColorSystem.java
 *
 * Copyright (c) 20108 by the original authors of JHotDraw
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

import java.awt.color.*;
import java.io.*;

/**
 * A Munsell Lab color system with a uniform perceptual distribution of the
 * colors.
 * <p>
 * The three coordinates of CIELAB represent the lightness of the color
 * (L* = 0 yields black and L* = 100 indicates diffuse white; specular white
 * may be higher),
 * its position between red/magenta and green (a*, negative values indicate
 * green while positive values indicate magenta) and its position between
 * yellow and blue (b*, negative values indicate blue and positive values
 * indicate yellow).
 * <p>
 * In this color model all LAB values are normalized to lie between 0 and 1.
 * <p>
 * The ICC profile used by this color system has been taken from
 * <a href="http://www.brucelindbloom.com/index.html?MunsellCalcHelp.html">
 * http://www.brucelindbloom.com/index.html?MunsellCalcHelp.html
 * </a>
 * <p>
 * CIE Lab to Uniform Perceptual Lab profile is
 * copyright © 2003 Bruce Justin Lindbloom.<br>
 * All rights reserved.<br>
 * <a href="http://www.brucelindbloom.com">http://www.brucelindbloom.com</a>
 * 
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class MunsellUPLabColorSystem extends ColorSpaceColorSystem {

    private ICC_ColorSpace colorSpace;

    /**
     * Creates a new instance.
     */
    public MunsellUPLabColorSystem() {
        super("Munsell CIELab_to_UPLab2.icc","Munsell UP LAB");
    }
}
