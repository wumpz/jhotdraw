/*
 * @(#)NotNull.java
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

package org.jhotdraw.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An element annotated with {@code NotNull} claims {@code null} value is
 * <em>forbidden</em> to return (for methods), pass to (parameters) and hold
 * (local variables and fields).
 * <p>
 * If annotated on a type (class or interface), {@code NotNull} is established
 * as the default for all contained elements.
 * <p>
 * Note: This annotation is similar to {@code org.jetbrains.annotations.NotNull},
 * but allows declaration on types.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NotNull {

}
