/*
 * @(#)SplitPathsAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.svg.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * SplitPathsAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class SplitAction extends CombineAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.splitPath";
    private ResourceBundleUtil labels =
            ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
    
    /** Creates a new instance. */
    public SplitAction(DrawingEditor editor) {
        super(editor, new SVGPathFigure(), false);
        labels.configureAction(this, ID);
    }
    public SplitAction(DrawingEditor editor, SVGPathFigure prototype) {
        super(editor, prototype, false);
        labels.configureAction(this, ID);
    }
}
