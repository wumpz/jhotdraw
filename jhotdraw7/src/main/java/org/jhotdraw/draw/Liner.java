/*
/*
 * @(#)Liner.java  1.0  2006-01-20
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

import java.util.*;
import java.io.*;
import org.jhotdraw.geom.*;
/**
 * A Liner encapsulates an algorithm to lineout
 * a ConnectionFigure.
 * <p>
 * Design pattern:<br>
 * Name: Strategy.<br>
 * Role: Strategy.<br>
 * Partners: {@link LineFigure} as Context.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2006-01-20 Created.
 */
public interface Liner extends Serializable, Cloneable {
    
    /**
     * Layouts the Path. This may alter the number and type of points
     * in the Path.
     *
     * @param figure The ConnectionFigure to be lined out.
     */
    public void lineout(ConnectionFigure figure);
    
    /**
     * Creates Handle's for the Liner.
     * The ConnectionFigure can provide these handles to the user, in order
     * to let her control the lineout.
     * 
     * @param path The path for which to create handles.
     */
    public Collection<Handle> createHandles(BezierPath path);
    
    public Liner clone();
}
