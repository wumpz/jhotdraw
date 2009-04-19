/*
 * @(#)DrawingEditorProxy.java  1.0  April 29, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw.action;

import java.awt.Container;
import java.awt.Cursor;
import java.beans.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.Tool;

/**
 * DrawingEditorProxy.
 * <p>
 * Design pattern:<br>
 * Name: Proxy.<br>
 * Role: Proxy.<br>
 * Partners: {@link org.jhotdraw.draw.DrawingEditor} as Subject, 
 * {@link org.jhotdraw.draw.DefaultDrawingEditor} as Real Subject.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 29, 2007 Created.
 */
public class DrawingEditorProxy extends AbstractBean implements DrawingEditor {
    private DrawingEditor target;
    private class Forwarder implements PropertyChangeListener, Serializable {
          public void propertyChange(PropertyChangeEvent evt) {
              firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
          }
    }
    private Forwarder forwarder;
    
    /** Creates a new instance. */
    public DrawingEditorProxy() {
        forwarder = new Forwarder();
    }
    
    /**
     * Sets the target of the proxy.
     */
    public void setTarget(DrawingEditor newValue) {
        if (target != null) {
            target.removePropertyChangeListener(forwarder);
        }
        this.target = newValue;
        if (target != null) {
            target.addPropertyChangeListener(forwarder);
        }
    }
    /**
     * Gets the target of the proxy.
     */
    public DrawingEditor getTarget() {
        return target;
    }
    
    
    public void add(DrawingView view) {
        target.add(view);
    }
    
    public void remove(DrawingView view) {
        target.remove(view);
    }
    
    public Collection<DrawingView> getDrawingViews() {
        return target.getDrawingViews();
    }
    
    public DrawingView getActiveView() {
        return (target == null) ? null : target.getActiveView();
    }
    
    public void setActiveView(DrawingView newValue) {
        target.setActiveView(newValue);
    }
    
    public DrawingView getFocusedView() {
        return (target == null) ? null : target.getActiveView();
    }
    
    public void setTool(Tool t) {
        target.setTool(t);
    }
    
    public Tool getTool() {
        return target.getTool();
    }
    
    public void setCursor(Cursor c) {
        target.setCursor(c);
    }
    
    public DrawingView findView(Container c) {
        return target.findView(c);
    }
    
    public <T> void setDefaultAttribute(AttributeKey<T> key, T value) {
        target.setDefaultAttribute(key, value);
    }
    
    public <T> T getDefaultAttribute(AttributeKey<T> key) {
        return target.getDefaultAttribute(key);
    }
    
    public void applyDefaultAttributesTo(Figure f) {
        target.applyDefaultAttributesTo(f);
    }
    
    public Map<AttributeKey, Object> getDefaultAttributes() {
        return target.getDefaultAttributes();
    }
    
    public void setEnabled(boolean newValue) {
        target.setEnabled(newValue);
    }
    
    public boolean isEnabled() {
        return target.isEnabled();
    }

    public <T> void setHandleAttribute(AttributeKey<T> key, T value) {
        target.setHandleAttribute(key, value);
    }

    public <T> T getHandleAttribute(AttributeKey<T> key) {
        return target.getHandleAttribute(key);
    }
}
