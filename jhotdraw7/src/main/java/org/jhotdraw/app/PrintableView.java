/*
 * @(#)PrintableView.java  1.0  July 31, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.app;

import java.awt.print.*;
import org.jhotdraw.app.*;

/**
 * Defines the interface of a view which can be printed.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 31, 2007 Created.
 */
public interface PrintableView extends View {
public Pageable createPageable();   
}
