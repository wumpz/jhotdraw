/*
 * @(#)DrawingEditorProxy.java  1.0  April 29, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.draw.action;

import java.awt.Container;
import java.awt.Cursor;
import java.beans.*;
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
 *
 * @author Werner Randelshofer
 * @version 1.0 April 29, 2007 Created.
 */
public class DrawingEditorProxy extends AbstractBean implements DrawingEditor {
    private DrawingEditor target;
    private PropertyChangeListener forwarder;
    
    /** Creates a new instance. */
    public DrawingEditorProxy() {
        forwarder = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
              firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
          }  
        };
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
    
    public void setDefaultAttribute(AttributeKey key, Object value) {
        target.setDefaultAttribute(key, value);
    }
    
    public Object getDefaultAttribute(AttributeKey key) {
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
}
