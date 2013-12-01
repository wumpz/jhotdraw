/*
 * @(#)DragTracker.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.*;

/**
 * A <em>drag tracker</em> provides the behavior for dragging selected
 * figures to the {@link SelectionTool}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * The different behavior states of the selection tool are implemented by
 * trackers.<br>
 * Context: {@link SelectionTool}; State: {@link DragTracker},
 * {@link HandleTracker}, {@link SelectAreaTracker}.
 *
 * <p><em>Chain of responsibility</em><br>
 * Mouse and keyboard events of the user occur on a drawing view, and are
 * preprocessed by the {@code DragTracker} of a {@code SelectionTool}. {@code
 * DragTracker} invokes "track" methods on a {@code Handle} which in turn
 * changes an aspect of a figure.
 * Client: {@link SelectionTool}; Handler: {@link DragTracker}, 
 * {@link org.jhotdraw.draw.handle.Handle}.
 * <hr>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DragTracker extends Tool {

    public void setDraggedFigure(Figure f);

}
