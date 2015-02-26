/*
 * @(#)UngroupAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * UngroupAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class UngroupAction extends GroupAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.ungroupSelection";
    /** Creates a new instance. */
    private CompositeFigure prototype;

    /** Creates a new instance. */
    public UngroupAction(DrawingEditor editor) {
        super(editor, new GroupFigure(), false);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor, prototype, false);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }
}
