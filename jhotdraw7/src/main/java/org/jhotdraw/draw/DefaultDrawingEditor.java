/*
 * @(#)DefaultDrawingEditor.java  3.0  2006-02-13
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

import org.jhotdraw.beans.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.*;
import java.util.*;
import java.io.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * DefaultDrawingEditor.
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-02-13 Revised to handle multiple drawing views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawingEditor extends AbstractBean implements DrawingEditor, ToolListener {
    private HashMap<AttributeKey, Object> defaultAttributes = new HashMap<AttributeKey,Object>();
    private Tool tool;
    private HashSet<DrawingView> views;
    private DrawingView activeView;
    private boolean isEnabled = true;
    private DrawingView focusedView;
    
    private FocusListener focusHandler = new FocusListener() {
        public void focusGained(FocusEvent e) {
            setFocusedView((DrawingView) findView((Container) e.getSource()));
        }
        
        public void focusLost(FocusEvent e) {
            if (! e.isTemporary()) {
            setFocusedView(null);
            }
        }
    };
    
    /** Creates a new instance. */
    public DefaultDrawingEditor() {
        setDefaultAttribute(FILL_COLOR, Color.white);
        setDefaultAttribute(STROKE_COLOR, Color.black);
        setDefaultAttribute(TEXT_COLOR, Color.black);
        
        views = new HashSet<DrawingView>();
    }
    
    public void setTool(Tool t) {
        if (t == tool) return;
        if (tool != null) {
            for (DrawingView v : views) {
                v.removeMouseListener(tool);
                v.removeMouseMotionListener(tool);
                v.removeKeyListener(tool);
            }
            tool.deactivate(this);
            tool.removeToolListener(this);
        }
        tool = t;
        if (tool != null) {
            tool.activate(this);
            for (DrawingView v : views) {
                v.addMouseListener(tool);
                v.addMouseMotionListener(tool);
                v.addKeyListener(tool);
            }
            tool.addToolListener(this);
        }
    }
    
    public void areaInvalidated(ToolEvent evt) {
        Rectangle r = evt.getInvalidatedArea();
        evt.getView().getContainer().repaint(r.x, r.y, r.width, r.height);
    }
    public void toolStarted(ToolEvent evt) {
        setView(evt.getView());
    }
    public void setView(DrawingView newValue) {
        DrawingView oldValue = activeView;
        activeView = newValue;
        firePropertyChange("view", oldValue, newValue);
        for (DrawingView v : views) {
            v.getContainer().repaint();
        }
    }
    public void toolDone(ToolEvent evt) {
        // FIXME - Maybe we should do this with all views of the editor??
        DrawingView v = getView();
        if (v != null) {
            Container c = v.getContainer();
            c.invalidate();
            if (c.getParent() != null) c.getParent().validate();
        }
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public DrawingView getView() {
        return activeView != null ? activeView : views.iterator().next();
    }
    
    private void updateFocusedView() {
        for (DrawingView v : views) {
            if (v.getContainer().hasFocus()) {
                setFocusedView(v);
                return;
            }
        }
        setFocusedView(null);
    }
    
    private void setFocusedView(DrawingView newValue) {
        DrawingView oldValue = focusedView;
        focusedView = newValue;
        firePropertyChange("focusedView", oldValue, newValue);
    }
    public DrawingView getFocusedView() {
        return focusedView;
    }
    
    public void applyDefaultAttributesTo(Figure f) {
        for (Map.Entry<AttributeKey, Object> entry : defaultAttributes.entrySet()) {
            f.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    
    public Object getDefaultAttribute(AttributeKey key) {
        return defaultAttributes.get(key);
    }
    
    public void setDefaultAttribute(AttributeKey key, Object newValue) {
        Object oldValue = defaultAttributes.put(key, newValue);
        firePropertyChange(key.getKey(), oldValue, newValue);
    }
    
    public void remove(DrawingView view) {
        view.getContainer().removeFocusListener(focusHandler);
        views.remove(view);
        if (tool != null) {
            view.removeMouseListener(tool);
            view.removeMouseMotionListener(tool);
            view.removeKeyListener(tool);
        }
        
        view.removeNotify(this);
        if (activeView == view) {
            view = (views.size() > 0) ? views.iterator().next() : null;
        }
        updateFocusedView();
    }
    
    public void add(DrawingView view) {
        views.add(view);
        view.addNotify(this);
        view.getContainer().addFocusListener(focusHandler);
        if (tool != null) {
            view.addMouseListener(tool);
            view.addMouseMotionListener(tool);
            view.addKeyListener(tool);
        }
        updateFocusedView();
    }
    
    public void setCursor(Cursor c) {
    }
    
    public Collection<DrawingView> getDrawingViews() {
        return Collections.unmodifiableCollection(views);
    }
    
    public DrawingView findView(Container c) {
        for (DrawingView v : views) {
            if (v.getContainer() == c) {
                return v;
            }
        }
        return null;
    }
    
    public void setEnabled(boolean newValue) {
        if (newValue != isEnabled) {
            boolean oldValue = isEnabled;
            isEnabled = newValue;
            firePropertyChange("enabled", oldValue, newValue);
        }
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
}
