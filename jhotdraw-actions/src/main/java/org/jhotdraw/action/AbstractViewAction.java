/*
 * @(#)AbstractViewAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action;

import java.beans.*;
import javax.swing.*;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of a {@link View}.
 * <p>
 * If the current View object is disabled or is null, the
 * AbstractViewAction is disabled as well.
 * <p>
 * A property name can be specified. When the specified property
 * changes or when the current view changes, method updateView
 * is invoked.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractViewAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private Application app;
    private View view;
    private String propertyName;
    /**
     * Set this to true if the action may create a new view if none exists.
     */
    private boolean mayCreateView;
    public static final String VIEW_PROPERTY = "view";
    public static final String ENABLED_PROPERTY = "enabled";
    /**
     * Combined enabled value consisting of the enabled state of this action and
     * the enabled state of the view and the application.
     */
    private boolean combinedEnabled = true;
    private PropertyChangeListener applicationListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ((evt.getPropertyName() == null && Application.ACTIVE_VIEW_PROPERTY == null) || (evt.getPropertyName() != null && evt.getPropertyName().equals(Application.ACTIVE_VIEW_PROPERTY))) { // Strings get interned
                updateView((View) evt.getOldValue(), (View) evt.getNewValue());
            }
        }
    };
    private PropertyChangeListener viewListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (ENABLED_PROPERTY.equals(name)) {
                updateEnabled();
            } else if ((name == null && propertyName == null) || (name != null && name.equals(propertyName))) {
                updateView();
            }
        }
    };

    /**
     * Creates a new instance which acts on the specified view of the application.
     */
    public AbstractViewAction(Application app, View view) {
        this.app = app;
        this.view = view;
        this.enabled = true;
        app.addPropertyChangeListener(new WeakPropertyChangeListener(applicationListener));
        if (view != null) {
            view.addPropertyChangeListener(viewListener);
        }
    }

    /**
     * Updates the listeners of this action depending on the current view
     * of the application.
     */
    protected void updateView(View oldValue, View newValue) {
        // We only need to do this, if the view has not been explicitly set
        if (view == null) {
            if (oldValue != null) {
                uninstallViewListeners(oldValue);
            }
            if (newValue != null) {
                installViewListeners(newValue);
            }
            firePropertyChange(VIEW_PROPERTY, oldValue, newValue);
            updateEnabled();
            updateView();
        }
    }

    /**
     * Sets the property name.
     */
    protected void setPropertyName(String name) {
        this.propertyName = name;
        if (name != null) {
            updateView();
        }
    }

    /**
     * Gets the property name.
     */
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * This method is invoked, when the property changed and when
     * the view changed.
     */
    protected void updateView() {
    }

    /**
     * Installs listeners on the view object.
     */
    protected void installViewListeners(View p) {
        p.addPropertyChangeListener(viewListener);
    }

    /**
     * Uninstalls listeners on the view object.
     */
    protected void uninstallViewListeners(View p) {
        p.removePropertyChangeListener(viewListener);
    }

    /**
     * Updates the enabled state of this action depending on the new enabled
     * state of the view.
     */
    protected void updateEnabled() {
        setEnabled(this.enabled);
    }

    public Application getApplication() {
        return app;
    }

    public View getActiveView() {
        return (view == null) ? app.getActiveView() : view;
    }

    /**
     * Returns true if the action <i>and</i> the application and <i>the</i> view
     * is enabled.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    @Override
    public boolean isEnabled() {
        return combinedEnabled;
    }

    /**
     * Enables or disables the action. The enabled state of the action
     * depends on the value that is set here and on the enabled state of
     * the application.
     *
     * @param newValue true to enable the action, false to
     * disable it
     * @see Action#setEnabled
     */
    @Override
    public void setEnabled(boolean newValue) {
        boolean oldValue = combinedEnabled;
        this.enabled = newValue;
        combinedEnabled = getApplication().isEnabled()
                && (isMayCreateView() || getActiveView() != null
                && getActiveView().isEnabled())
                && this.enabled;
        firePropertyChange(ENABLED_PROPERTY,
                oldValue,
                combinedEnabled);
    }

    /**
     * Set this to true if the action may create a new view if none exists.
     * If this is false, the action will be disabled, if no view is available.
     */
    protected void setMayCreateView(boolean b) {
        mayCreateView = b;
    }

    /**
     * Returns to true if the action may create a new view if none exists.
     */
    protected boolean isMayCreateView() {
        return mayCreateView;
    }
}
