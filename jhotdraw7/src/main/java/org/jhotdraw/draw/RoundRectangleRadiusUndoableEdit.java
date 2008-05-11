/**
 * @(#)RoundRectangleRadiusUndoableEdit.java  1.0  May 11, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package org.jhotdraw.draw;

import ch.randelshofer.quaqua.util.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;

/**
 * RoundRectangleRadiusUndoableEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 May 11, 2008 Created.
 */
public class RoundRectangleRadiusUndoableEdit extends AbstractUndoableEdit {

    private RoundRectangleFigure owner;
    private Point2D.Double oldArc;
    private Point2D.Double newArc;

    public RoundRectangleRadiusUndoableEdit(RoundRectangleFigure owner, Point2D.Double oldArc, Point2D.Double newArc) {
        this.owner = owner;
        this.oldArc = oldArc;
        this.newArc = newArc;
    }

    @Override
    public String getPresentationName() {
        ResourceBundleUtil labels =
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
        return labels.getString("attribute.roundRectRadius");
    }

    @Override
    public void redo() throws CannotRedoException {
        owner.willChange();
        owner.setArc(newArc.x, newArc.y);
        owner.changed();
        super.redo();
    }

    @Override
    public void undo() throws CannotUndoException {
        owner.willChange();
        owner.setArc(oldArc.x, oldArc.y);
        owner.changed();
        super.undo();
    }

    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof RoundRectangleRadiusUndoableEdit) {
            RoundRectangleRadiusUndoableEdit that = (RoundRectangleRadiusUndoableEdit) anEdit;
            if (that.owner == this.owner) {
                this.newArc = that.newArc;
                that.die();
                return true;
            }
        }
        return false;
    }

    public boolean replaceEdit(UndoableEdit anEdit) {
        if (anEdit instanceof RoundRectangleRadiusUndoableEdit) {
            RoundRectangleRadiusUndoableEdit that = (RoundRectangleRadiusUndoableEdit) anEdit;
            if (that.owner == this.owner) {
                that.oldArc = this.oldArc;
                this.die();
                return true;
            }
        }
        return false;
    }
}
