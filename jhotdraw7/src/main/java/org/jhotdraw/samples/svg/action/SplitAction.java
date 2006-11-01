/*
 * @(#)SplitPathsAction.java  1.0  2006-07-12
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
 */

package org.jhotdraw.samples.svg.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 * SplitPathsAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2006-07-12 Created.
 */
public class SplitAction extends UngroupAction {
    public final static String ID = "selectionSplit";
    
    /** Creates a new instance. */
    public SplitAction(DrawingEditor editor) {
        super(editor, new SVGPath());
        
        labels = ResourceBundleUtil.getLAFBundle(
                "org.jhotdraw.samples.svg.Labels",
                Locale.getDefault()
                );
        labels.configureAction(this, ID);
    }
    protected boolean canUngroup() {
        if (super.canUngroup()) {
           return ((CompositeFigure) getView().getSelectedFigures().iterator().next()).getChildCount() > 1;
        }
        return false;
    }
   @Override public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
        LinkedList<Figure> figures = new LinkedList<Figure>(group.getChildren());
        view.clearSelection();
        group.basicRemoveAllChildren();
        LinkedList<Figure> paths = new LinkedList<Figure>();
        for (Figure f : figures) {
            SVGPath path = new SVGPath();
            path.removeAllChildren();
            for (Map.Entry<AttributeKey,Object> entry : group.getAttributes().entrySet()) {
                path.basicSetAttribute(entry.getKey(), entry.getValue());
            }
            path.add(f);
            view.getDrawing().basicAdd(path);
            paths.add(path);
        }
        view.getDrawing().remove(group);
        view.addToSelection(paths);
        return figures;
    }
    @Override public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
        Collection<Figure> sorted = view.getDrawing().sort(figures);
        view.getDrawing().basicRemoveAll(figures);
        view.clearSelection();
        view.getDrawing().add(group);
        group.willChange();
      ((SVGPath) group).removeAllChildren();
        for (Map.Entry<AttributeKey,Object> entry : figures.iterator().next().getAttributes().entrySet()) {
            group.basicSetAttribute(entry.getKey(), entry.getValue());
        }
        for (Figure f : sorted) {
            SVGPath path = (SVGPath) f;
            for (Figure child : path.getChildren()) {
                group.basicAdd(child);
            }
        }
        group.changed();
        view.addToSelection(group);
    }
}
