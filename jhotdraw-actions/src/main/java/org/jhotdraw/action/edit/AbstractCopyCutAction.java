package org.jhotdraw.action.edit;

import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.datatransfer.ClipboardUtil;

public abstract class AbstractCopyCutAction extends AbstractSelectionAction {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance which acts on the currently focused component.
     */
    protected AbstractCopyCutAction(String ID) {
        this(null, ID);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    protected AbstractCopyCutAction(JComponent target, String ID) {
        super(target, ID);
    }

    public void actionPerformed(ActionEvent evt, int status) {
        super.actionPerformed(evt);
        JComponent c = target;
        if (c != null) {
            c.getTransferHandler().exportToClipboard(
                    c,
                    ClipboardUtil.getClipboard(),
                    status);
        }
    }
}
