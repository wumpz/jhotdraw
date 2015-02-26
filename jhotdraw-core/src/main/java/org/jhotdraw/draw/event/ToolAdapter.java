/*
 * @(#)ToolAdapter.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.event;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract adapter class for receiving {@link ToolEvent}s. This class
 * exists as a convenience for creating {@link ToolListener} objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolAdapter implements ToolListener {

    @Override
    public void toolStarted(ToolEvent event) {
    }

    @Override
    public void toolDone(ToolEvent event) {
    }

    @Override
    public void areaInvalidated(ToolEvent e) {
    }

    @Override
    public void boundsInvalidated(ToolEvent e) {
    }

}
