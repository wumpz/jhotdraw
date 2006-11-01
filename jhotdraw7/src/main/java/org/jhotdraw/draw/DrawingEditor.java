/*
 * @(#)DrawingEditor.java  2.1  2006-03-15
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


package org.jhotdraw.draw;

import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.util.*;
/**
 * DrawingEditor defines the interface for coordinating
 * the different objects that participate in a drawing editor.
 * <p>
 * For applications with a single document interface (SDI) there is typically 
 * one DrawingEditor instance per document window. So that each window
 * can have its own toolbars and drawing palettes.
 * <p>
 * For applications with a Windows-style multiple document interface (MDI) there
 * is typically one DrawingEditor instance per parent window. All document 
 * windows within a parent window share the toolbars and drawing palettes 
 * provided be the parent window.
 * <p>
 * For applications with a Mac OS X-style application document interface (OSX) 
 * there is typically a single DrawingEditor instance for the application. All
 * document windows within the application share a single set of toolbars and 
 * drawing palettes.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-03-15 Support for enabled state added.
 * <br>2.0 2006-02-13 Revised to support multiple drawing views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingEditor {
    /**
     * Gets the editor's current drawing.
     * /
    Drawing getDrawing();
    /**
     * Sets the editor's current drawing.
     * /
    void setDrawing(Drawing drawing);
    */
    /**
     * Adds a drawing view to the editor.
     * The editor invokes addNotify on the view, and it registers its tool
     * as an event listener on the view.
     */
    void add(DrawingView view);

    /**
     * Removes a drawing view from the editor.
     * The editor invokes removeNotify on the view, and it unregisters its tool
     * on the view.
     */
    void remove(DrawingView view);
    
    /**
     * Gets all drawing views associated with this editor.
     */
    Collection<DrawingView> getDrawingViews();
    
    /**
     * Gets the editor's active drawing view.
     * This can be null, if the editor has no views.
     */
    DrawingView getView();
    /**
     * Sets the editor's active drawing view.
     * This can be set to null, if the editor has no views.
     */
    void setView(DrawingView newValue);
    /**
     * Gets the editor's focused view.
     * This can be null, if the editor has no views.
     */
    DrawingView getFocusedView();
    
    /**
     * Calls deactivate on the previously active tool of this drawing editor.
     * Calls activate on the provided tool.
     * Forwards all mouse, mouse moation and keyboard events that occur on the
     * DrawingView to the provided tool.
     */
    void setTool(Tool t);
    /**
     * Gets the current tool.
     */
    Tool getTool();
    /**
     * Sets the cursor on the view(s) of the drawing editor.
     */
    void setCursor(Cursor c);
    /**
     * Finds a handle at the given coordinates.
     * @return A handle, null if no handle is found.
     * /
    public Handle findHandle(Point p);
    */
    
    /**
     * Finds a drawing view.
     * This is used by Tool to identify the view of which it has received
     * an event.
     */
    public DrawingView findView(Container c);
    
    /**
     * Sets a default attribute of the editor.
     * The default attribute will be used by creation tools, to create a new
     * figure.
     */
    public void setDefaultAttribute(AttributeKey key, Object value);
    /**
     * Gets a default attribute from the editor.
     * The default attribute will be used by creation tools, to create a new
     * figure.
     */
    public Object getDefaultAttribute(AttributeKey key);
    /**
     * Applies the default attributes to the specified figure.
     */
     public void applyDefaultAttributesTo(Figure f);
     
     /**
      * Sets the enabled state of the drawing editor.
      * This is a bound property.
      */
     public void setEnabled(boolean newValue);
     /**
      * Gets the enabled state of the drawing editor.
      */
     public boolean isEnabled();
     
     public void addPropertyChangeListener(PropertyChangeListener listener);
     public void removePropertyChangeListener(PropertyChangeListener listener);
}
