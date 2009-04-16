/*
 * @(#)RestoreDataEdit.java  2.0  2006-01-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */


package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import javax.swing.undo.*;
import java.util.*;
/**
 * RestoreDataEdit.
 *
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class RestoreDataEdit extends AbstractUndoableEdit {
    private Figure figure;
    private Object oldRestoreData;
    private Object newRestoreData;
    
    /** Creates a new instance. */
    public RestoreDataEdit(Figure figure, Object oldRestoreData) {
        this.figure = figure;
        this.oldRestoreData = oldRestoreData;
        this.newRestoreData = figure.getTransformRestoreData();
    }
    @Override
    public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
        return labels.getString("edit.transform.text");
    }
    
    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }
    
    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }
    
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
         figure.willChange();
        figure.restoreTransformTo(newRestoreData);
         figure.changed();
    }
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
         figure.willChange();
        figure.restoreTransformTo(oldRestoreData);
         figure.changed();
    }
}
