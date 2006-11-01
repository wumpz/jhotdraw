/*
 * @(#)Tool.java  1.0  11. November 2003
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
�
 */


package org.jhotdraw.draw;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
 * A tool defines a mode of the drawing view. All input events targeted to the
 * drawing view are forwarded to its current tool.
 * <p>
 * Tools inform listeners when they are done with an interaction by calling
 * the ToolListener's toolDone() method. The Tools are created once and reused.
 * They are initialized/deinitialized with activate()/deactivate().
 * <p>
 * Tools are used for user interaction. Unlike figures, a tool works with
 * the user interface coordinates of the DrawingView. The user interface 
 * coordinates are expressed in integer pixels.

 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Tool extends MouseListener, MouseMotionListener, KeyListener {
    
    /**
     * Activates the tool for the given view. This method is called
     * whenever the user switches to this tool. Use this method to
     * reinitialize a tool.
     * Note, a valid view must be present in order for the tool to accept activation
     */
    public void activate(DrawingEditor editor);
    
    /**
     * Deactivates the tool. This method is called whenever the user
     * switches to another tool. Use this method to do some clean-up
     * when the tool is switched. Subclassers should always call
     * super.deactivate.
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
}
