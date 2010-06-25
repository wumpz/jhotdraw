/*
 * @(#)Tool.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */


package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.event.ToolListener;
import java.awt.*;
import java.awt.event.*;
import org.jhotdraw.annotations.NotNull;
import org.jhotdraw.annotations.Nullable;
/**
 * A <em>tool</em> defines a mode of a {@link DrawingEditor}. All input events
 * targeted to the active {@link DrawingView} of the drawing editor are 
 * forwarded to its current tool.
 * <p>
 * Tools inform listeners when they are done with an interaction by calling
 * the ToolListener's toolDone() method. The Tools are created once and reused.
 * They are initialized/deinitialized with activate()/deactivate().
 * <p>
 * Tools are used for user interaction. Unlike figures, a tool works with
 * the user interface coordinates of the DrawingView. The user interface 
 * coordinates are expressed in integer pixels.
 * <p>
 * A Tool forwards UndoableEdit events to the Drawing object onto which it
 * is performing changes.
 * <p>
 * If a tool does not handle a specific keyboard event, it looks up the
 * {@code InputMap} of the drawing editor and then invokes the
 * corresponding action in the {@code ActionMap} of the drawing editor.
 * A tool can have an InputMap and ActionMap of its own which take precedence
 * over the ones provided by the drawing editor.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The following interfaces define the contracts of a framework for structured
 * drawing editors:<br>
 * Contract: {@link Drawing}, {@link Figure}, {@link DrawingView},
 * {@link DrawingEditor}, {@link org.jhotdraw.draw.handle.Handle} and {@link Tool}.
 *
 * <p><em>Mediator</em><br>
 * {@code DrawingEditor} acts as a mediator for coordinating drawing tools
 * and drawing views:<br>
 * Mediator: {@link DrawingEditor}; Colleagues: {@link DrawingView}, {@link Tool}.
 *
 * <p><em>Mediator</em><br>
 * {@code DrawingEditor} acts as a mediator for coordinating keyboard input from
 * {@code Tool}s and Swing action objects:<br>
 * Mediator: {@link DrawingEditor}; Colleagues: {@link Tool}, javax.swing.Action.
 *
 * <p><em>Observer</em><br>
 * State changes of tools can be observed by other objects. Specifically
 * {@code DrawingEditor} observes area invalidations of tools and repaints
 * its active drawing view accordingly.<br>
 * Subject: {@link Tool}; Observer: {@link ToolListener}; Event:
 * {@link org.jhotdraw.draw.event.ToolEvent}; Concrete Observer: {@link DrawingEditor}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
@NotNull
public interface Tool extends MouseListener, MouseMotionListener, KeyListener {
    
    /**
     * Activates the tool for the given editor. This method is called
     * whenever the user switches to this tool.
     */
    public void activate(DrawingEditor editor);
    
    /**
     * Deactivates the tool. This method is called whenever the user
     * switches to another tool.
     */
    public void deactivate(DrawingEditor editor);

    /**
     * Adds a listener for this tool.
     */
    void addToolListener(ToolListener l);
    
    /**
     * Removes a listener for this tool.
     */
    void removeToolListener(ToolListener l);
    
    /**
     * Draws the tool.
     */
    void draw(Graphics2D g);
    
    /**
     * Deletes the selection.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editDelete();
    /**
     * Cuts the selection into the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editCut();
    /**
     * Copies the selection into the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editCopy();
    /**
     * Duplicates the selection.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editDuplicate();
    /**
     * Pastes the contents of the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editPaste();
    
    /**
     * Returns the tooltip text for a mouse event on a drawing view.
     * 
     * @param view A drawing view.
     * @param evt A mouse event.
     * @return A tooltip text or null.
     */
    @Nullable public String getToolTipText(DrawingView view, MouseEvent evt);
    
    /**
     * Returns true, if this tool lets the user interact with handles.
     * <p>
     * Handles may draw differently, if interaction is not possible.
     * 
     * @return True, if this tool supports interaction with the handles.
     */
    public boolean supportsHandleInteraction();
}
