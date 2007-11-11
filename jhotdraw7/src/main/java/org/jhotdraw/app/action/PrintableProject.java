/*
 * @(#)PrintableProject.java  1.0  July 31, 2007
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

package org.jhotdraw.app.action;

import java.awt.print.*;
import org.jhotdraw.app.*;

/**
 * Defines the interface for a project which can be printed.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 31, 2007 Created.
 */
public interface PrintableProject extends Project {
public Pageable createPageable();   
}
