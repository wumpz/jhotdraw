/*
 * @(#)GridProject.java  1.0  July 31, 2007
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

package org.jhotdraw.draw.action;

import org.jhotdraw.app.Project;
import org.jhotdraw.draw.GridConstrainer;

/**
 * Defines the contract for a project which has a grid that can be
 * shown and hidden.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 31, 2007 Created.
 */
public interface GridProject extends Project {
    /**
     * Changes the visibility of the grid. This is a bound property.
     */
    public void setGridVisible(boolean newValue);
    /**
     * Returns true, if the grid is visible.
     */
    public boolean isGridVisible();

    /**
     * Returns the grid constrainer of the project.
     */
    GridConstrainer getGridConstrainer();
    
}
