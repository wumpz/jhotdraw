/*
 * @(#)FigureAttributeEditorHandler.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import javax.annotation.Nullable;
import org.jhotdraw.gui.*;
import java.util.HashSet;
import java.util.Set;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;

/**
 * FigureAttributeEditorHandler mediates between an AttributeEditor and the
 * currently selected Figure's in a DrawingEditor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingAttributeEditorHandler<T> extends AbstractAttributeEditorHandler<T> {

    private Drawing drawing;

    public DrawingAttributeEditorHandler(AttributeKey<T> key, AttributeEditor<T> attributeEditor, @Nullable DrawingEditor drawingEditor) {
        super(key, attributeEditor, drawingEditor, false);
    }

    public void setDrawing(Drawing newValue) {
        drawing = newValue;
        updateAttributeEditor();
    }

    public Drawing getDrawing() {
        return drawing;
    }

    @Override
    protected Set<Figure> getEditedFigures() {
        HashSet<Figure> s = new HashSet<>();
        if (drawing != null) {
            s.add(drawing);
        } else if (activeView != null) {
            s.add(activeView.getDrawing());
        }
        return s;
    }
}
