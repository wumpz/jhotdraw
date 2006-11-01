/*
 * @(#)TransformEdit.java  2.0  2006-01-14
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
�
 */


package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import javax.swing.undo.*;
import java.awt.geom.*;
import java.util.*;
/**
 * TransformEdit.
 * <p>
 * FIXME - When we do group moves or moves of a composite figure we fail to
 * coallesce the TransformEdit events. This may exhaust memory!
 *
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class TransformEdit extends AbstractUndoableEdit {
    /**
     * Implementation note: Owner has package access, because it is accessed
     * by CompositeMoveEdit.
     */
    private Collection<Figure> figures;
    private AffineTransform tx;
    
    /** Creates a new instance. */
    public TransformEdit(Figure figure, AffineTransform tx) {
        figures = new LinkedList<Figure>();
        ((LinkedList<Figure>) figures).add(figure);
        this.tx = (AffineTransform) tx.clone();
    }
    public TransformEdit(Collection<Figure> figures, AffineTransform tx) {
        this.figures = figures;
        this.tx = (AffineTransform) tx.clone();
    }
    public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
        return labels.getString("transformFigure");
    }
    
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof TransformEdit) {
            TransformEdit that = (TransformEdit) anEdit;
            if (that.figures == this.figures) {
                this.tx.concatenate(that.tx);
                that.die();
                return true;
            }
        }
        return false;
    }
    public boolean replaceEdit(UndoableEdit anEdit) {
        if (anEdit instanceof TransformEdit) {
            TransformEdit that = (TransformEdit) anEdit;
            if (that.figures == this.figures) {
                this.tx.preConcatenate(that.tx);
                that.die();
                return true;
            }
        }
        return false;
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        for (Figure f : figures) {
            f.willChange();
            f.basicTransform(tx);
                f.changed();
            
        }
    }
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            AffineTransform inverse = tx.createInverse();
            for (Figure f : figures) {
                f.willChange();
                f.basicTransform(inverse);
                f.changed();
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }
    public String toString() {
        return getClass().getName()+'@'+hashCode()+" tx:"+tx;
    }
}
