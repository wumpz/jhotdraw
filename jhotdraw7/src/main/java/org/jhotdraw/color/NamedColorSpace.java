/*
 * @(#)NamedColorSpace.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw
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

/**
 * Interface for {@code ColorSpace} classes which have a name.
 * <p>
 * This interface is used by {@link ColorSpaceUtil} to retrieve a name from
 * a {@code ColorSpace}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-01-22 Created.
 */
public interface NamedColorSpace {

    public String getName();
}
