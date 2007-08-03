/*
 * @(#)PrintableProject.java  1.0  July 31, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
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
