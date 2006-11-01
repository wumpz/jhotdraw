/*
 * @(#)Arrangeable.java  1.0  7. Februar 2006
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

package org.jhotdraw.app.action;

import java.beans.*;
/**
 * Arrangeable.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 7. Februar 2006 Created.
 */
public interface Arrangeable {
    enum Arrangement { VERTICAL, HORIZONTAL, CASCADE };
    
    public void setArrangement(Arrangement newValue);
    public Arrangement getArrangement();
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
}
