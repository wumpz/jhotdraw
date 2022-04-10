package org.jhotdraw.action.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.util.*;

public abstract class AbstractMinimizeMaximizeAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;

    protected AbstractMinimizeMaximizeAction(Application app, View view, String ID) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
        labels.configureAction(this, ID);
    }

    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                getActiveView().getComponent()
        );
    }

    public void actionPerformed(ActionEvent evt, int constant) {
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() ^ constant);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
}
