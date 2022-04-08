package org.jhotdraw.action.edit;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.util.*;

public abstract class AbstractRedoUndoAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    private ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
    private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if ((name == null && Action.NAME == null) || (name != null && name.equals(Action.NAME))) {
                putValue(Action.NAME, evt.getNewValue());
            } else if ("enabled".equals(name)) {
                updateEnabledState();
            }
        }
    };

    /**
     * Creates a new instance.
     */
    protected AbstractRedoUndoAction(Application app, View view, String ID) {
        super(app, view);
        labels.configureAction(this, ID);
    }

    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realAction = getRealRedoUndoAction();
        if (realAction != null && realAction != this) {
            isEnabled = realAction.isEnabled();
        }
        setEnabled(isEnabled);
    }

    @Override
    protected void updateView(View oldValue, View newValue) {
        super.updateView(oldValue, newValue);
        if (newValue != null
                && newValue.getActionMap().get(ID) != null
                && newValue.getActionMap().get(ID) != this) {
            putValue(Action.NAME, newValue.getActionMap().get(ID).
                    getValue(Action.NAME));
            updateEnabledState();
        }
    }

    /**
     * Installs listeners on the view object.
     */
    @Override
    protected void installViewListeners(View p) {
        super.installViewListeners(p);
        Action redoUndoActionInView = p.getActionMap().get(ID);
        if (redoUndoActionInView != null && redoUndoActionInView != this) {
            redoUndoActionInView.addPropertyChangeListener(redoActionPropertyListener);
        }
    }

    /**
     * Installs listeners on the view object.
     */
    @Override
    protected void uninstallViewListeners(View p) {
        super.uninstallViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.removePropertyChangeListener(redoActionPropertyListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action realAction = getRealRedoUndoAction();
        if (realAction != null && realAction != this) {
            realAction.actionPerformed(e);
        }
    }

    protected Action getRealRedoUndoAction() {
        return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
    }
}
