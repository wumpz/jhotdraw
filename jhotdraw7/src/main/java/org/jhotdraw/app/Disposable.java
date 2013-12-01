/*
 * @(#)Disposable.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Interface for objects which explicitly must be disposed to free resources.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Disposable {
    /** Disposes of all resources held by this object so that they can be
     * garbage collected.
     */
    public void dispose();
}
