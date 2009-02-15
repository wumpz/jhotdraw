/**
 * @(#)RoundRectangleRadiusUndoableEdit.java  1.0  May 11, 2008
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.geom.*;

/**
 * RoundRectangleRadiusUndoableEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 May 11, 2008 Created.
 */
public class SVGRectRadiusUndoableEdit extends AbstractUndoableEdit {

    private SVGRectFigure owner;
    private Dimension2DDouble oldArc;
    private Dimension2DDouble newArc;

    public SVGRectRadiusUndoableEdit(SVGRectFigure owner, Dimension2DDouble oldArc, Dimension2DDouble newArc) {
        this.owner = owner;
        this.oldArc = oldArc;
        this.newArc = newArc;
    }

    @Override
    public String getPresentationName() {
        ResourceBundleUtil labels =
                ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
        return labels.getString("attribute.roundRectRadius");
    }

    @Override
    public void redo() throws CannotRedoException {
        owner.willChange();
        owner.setArc(newArc.width, newArc.height);
        owner.changed();
        super.redo();
    }

    @Override
    public void undo() throws CannotUndoException {
        owner.willChange();
        owner.setArc(oldArc.width, oldArc.height);
        owner.changed();
        super.undo();
    }

    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof SVGRectRadiusUndoableEdit) {
            SVGRectRadiusUndoableEdit that = (SVGRectRadiusUndoableEdit) anEdit;
            if (that.owner == this.owner) {
                this.newArc = that.newArc;
                that.die();
                return true;
            }
        }
        return false;
    }

    public boolean replaceEdit(UndoableEdit anEdit) {
        if (anEdit instanceof SVGRectRadiusUndoableEdit) {
            SVGRectRadiusUndoableEdit that = (SVGRectRadiusUndoableEdit) anEdit;
            if (that.owner == this.owner) {
                that.oldArc = this.oldArc;
                this.die();
                return true;
            }
        }
        return false;
    }
}
