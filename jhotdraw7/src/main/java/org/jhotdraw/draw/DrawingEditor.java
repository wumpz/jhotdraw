/*
 * @(#)DrawingEditor.java  4.0  2009-04-19
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */


package org.jhotdraw.draw;

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
 * <p>
 * Design pattern:<br>
 * Name: Mediator.<br>
 * Role: Mediator.<br>
 * Partners: {@link DrawingView} as Colleague, {@link Tool} as
 * Colleague.
 * <p>
 * Design pattern:<br>
 * Name: Proxy.<br>
 * Role: Subject.<br>
 * Partners: {@link org.jhotdraw.draw.action.DrawingEditorProxy} as Proxy, {@link DefaultDrawingEditor} as
 * Real Subject.
 * 
 * @author Werner Randelshofer
 * @version 4.0 2009-04-18 Made set/getDefaultAttribute methods and
 * set/getHandleAttribute methods type safe.
 * <br>3.1 2008-05-23 Added TOOL_PROPERTY.
 * <br>3.0 2008-05-11 Added methods setHandleAttribute, getHandleAttribute. 
 * <br>2.4 2007-12-25 Renamed PROP_CURRENT_VIEW to ACTIVE_VIEW_PROPERTY. 
 * <br>2.3 2007-05-26 Streamlined methods setActiveView, setFocusedView, getActiveView
 * into setActiveView, getActiveView.
 * <br>2.2 2007-04-16 Added method getDefaultAttributes 
 * <br>2.1 2006-03-15 Support for enabled state added.
 * <br>2.0 2006-02-13 Revised to support multiple drawing views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingEditor {
    /**
     * The property name for the active view property.
     */
    public final static String ACTIVE_VIEW_PROPERTY = "activeView";   
    /**
     * The property name for the active tool property.
     */
    public final static String TOOL_PROPERTY = "tool";   
    
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
    DrawingView getActiveView();
    /**
     * Sets the editor's active drawing view.
     * This can be set to null, if the editor has no views.
     */
    void setActiveView(DrawingView newValue);
    
    /**
     * Calls deactivate on the previously active tool of this drawing editor.
     * Calls activate on the provided tool.
     * Forwards all mouse, mouse moation and keyboard events that occur on the
     * DrawingView to the provided tool.
     * <p>
     * This is a bound property.
     */
    void setTool(Tool t);
    /**
     * Gets the current tool.
     * <p>
     * This is a bound property.
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
    public <T> void setDefaultAttribute(AttributeKey<T> key, T value);
    /**
     * Gets a default attribute from the editor.
     * The default attribute will be used by creation tools, to create a new
     * figure.
     */
    public <T> T getDefaultAttribute(AttributeKey<T> key);
    /**
     * Applies the default attributes to the specified figure.
     */
     public void applyDefaultAttributesTo(Figure f);
    /**
     * Returns an immutable Map with the default attributes of this editor.
     */
    public Map<AttributeKey,Object> getDefaultAttributes();
     
    /**
     * Sets a handle attribute of the editor.
     * The default attribute will be used by creation tools, to create a new
     * figure.
     * 
     * @param key AttributeKey. 
     * @param value Attribute value. 
     */
    public <T> void setHandleAttribute(AttributeKey<T> key, T value);
    /**
     * Gets a handle attribute from the editor.
     * The default attribute will be used by creation tools, to create a new
     * figure.
     * 
     * @param key AttributeKey. 
     * 
     * @return If the handle attribute has been set, returns the previously
     * set value. If the handle attribute has not been set, returns key.getDefaultValue().
     */
    public <T> T getHandleAttribute(AttributeKey<T> key);
    
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
