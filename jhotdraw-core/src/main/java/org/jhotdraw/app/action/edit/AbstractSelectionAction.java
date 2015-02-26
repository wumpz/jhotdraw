/*
 * @(#)AbstractSelectionAction.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * {@code AbstractSelectionAction} acts on the selection of a target component.
 * <p>
 * By default, the action is disabled when the target component is disabled or has
 * no selection. If the target component is null, updateEnabled does nothing.
 * You can change this behavior by overriding method {@code updateEnabled()}.
 * <p>
 * This action registers a {@link WeakPropertyChangeListener} on the component.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below work together:
 * <br>
 * Contract: {@link org.jhotdraw.gui.EditableComponent}, {@code JTextComponent}.<br>
 * Client: {@link org.jhotdraw.app.action.edit.AbstractSelectionAction},
 * {@link org.jhotdraw.app.action.edit.DeleteAction},
 * {@link org.jhotdraw.app.action.edit.DuplicateAction},
 * {@link org.jhotdraw.app.action.edit.SelectAllAction},
 * {@link org.jhotdraw.app.action.edit.ClearSelectionAction}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSelectionAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    /** The target of the action or null if the action acts on the currently
     * focused component.
     */
    @Nullable protected JComponent target;
    /** This variable keeps a strong reference on the property change listener. */
    private PropertyChangeListener propertyHandler;

    /** Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public AbstractSelectionAction(@Nullable JComponent target) {
        this.target = target;
        if (target != null) {
            // Register with a weak reference on the JComponent.
            propertyHandler = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String n = evt.getPropertyName();

                    if ("enabled".equals(n)) {
                        updateEnabled();
                    } else if (n.equals(EditableComponent.SELECTION_EMPTY_PROPERTY)) {
                        updateEnabled();
                    }
                }
            };
            target.addPropertyChangeListener(new WeakPropertyChangeListener(propertyHandler));
        }
    }

    protected void updateEnabled() {
        if (target instanceof EditableComponent) {
            setEnabled(target.isEnabled() && !((EditableComponent) target).isSelectionEmpty());
        } else if (target != null) {
            setEnabled(target.isEnabled());
        }
    }
}
