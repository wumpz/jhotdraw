/*
 * @(#)Arrangeable.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui;

import java.beans.*;

/**
 * Arrangeable.
 * 
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Arrangeable {
    enum Arrangement { VERTICAL, HORIZONTAL, CASCADE };
    
    public void setArrangement(Arrangement newValue);
    public Arrangement getArrangement();
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
}
