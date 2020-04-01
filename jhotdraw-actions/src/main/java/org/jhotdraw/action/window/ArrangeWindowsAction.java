/*
 * @(#)ArrangeWindowsAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.window;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jhotdraw.api.gui.Arrangeable;
import static org.jhotdraw.api.gui.Arrangeable.Arrangement.CASCADE;
import static org.jhotdraw.api.gui.Arrangeable.Arrangement.HORIZONTAL;
import static org.jhotdraw.api.gui.Arrangeable.Arrangement.VERTICAL;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Changes the arrangement of an {@link Arrangeable} object.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * FIXME - Register as PropertyChangeListener on Arrangeable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ArrangeWindowsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final String VERTICAL_ID = "window.arrangeVertical";
    public static final String HORIZONTAL_ID = "window.arrangeHorizontal";
    public static final String CASCADE_ID = "window.arrangeCascade";
    private Arrangeable arrangeable;
    private Arrangeable.Arrangement arrangement;

    /**
     * Creates a new instance.
     */
    public ArrangeWindowsAction(Arrangeable arrangeable, Arrangeable.Arrangement arrangement) {
        this.arrangeable = arrangeable;
        this.arrangement = arrangement;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
        String labelID;
        switch (arrangement) {
            case VERTICAL:
                labelID = VERTICAL_ID;
                break;
            case HORIZONTAL:
                labelID = HORIZONTAL_ID;
                break;
            case CASCADE:
            default:
                labelID = CASCADE_ID;
                break;
        }
        labels.configureAction(this, labelID);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        arrangeable.setArrangement(arrangement);
    }
}
