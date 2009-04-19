/*
 * @(#)TextCreationTool.java  1.0  2009-04-16
 *
 * Copyright (c) 2009 by the original authors of JHotDraw
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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * A tool to create figures which implement the {@code TextHolderFigure}
 * interface, such as {@code TextFigure}. The figure to be created is specified
 * by a prototype.
 * <p>
 * To create a figure using this tool, the user does the following mouse
 * gestures on a DrawingView:
 * <ol>
 * <li>Press the mouse button over an area on the DrawingView on which there
 * isn't a text figure present. This defines the location of the figure.</li>
 * </ol>
 * When the user has performed this mouse gesture, the TextCreationTool overlays
 * a text field over the drawing where the user can enter the text for the Figure.
 *
 * @see TextHolderFigure
 * @see FloatingTextField
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-04-16 Refactored from TextTool.
 */
public class TextCreationTool extends CreationTool implements ActionListener {
    private FloatingTextField   textField;
    private TextHolderFigure  typingTarget;
    
    /** Creates a new instance. */
    public TextCreationTool(TextHolderFigure prototype) {
        super(prototype);
    }
    /** Creates a new instance. */
    public TextCreationTool(TextHolderFigure prototype, Map<AttributeKey,Object> attributes) {
        super(prototype, attributes);
    }
    
    @Override
    public void deactivate(DrawingEditor editor) {
        endEdit();
        super.deactivate(editor);
    }
    /**
     * Creates a new figure at the location where the mouse was pressed.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        TextHolderFigure textHolder = null;
        // Note: The search sequence used here, must be
        // consistent with the search sequence used by the
        // HandleTracker, SelectAreaTracker, DelegationSelectionTool, SelectionTool.

        // If possible, continue to work with the current selection
        DrawingView v = getView();
        Point2D.Double p = v.viewToDrawing(e.getPoint());
        Figure pressedFigure = null;
        if (true /*isSelectBehindEnabled()*/) {
            for (Figure f : v.getSelectedFigures()) {
                if (f.contains(p)) {
                    pressedFigure = f;
                    break;
                }
            }
        }

        // If the point is not contained in the current selection,
        // search for a figure in the drawing.
        if (pressedFigure == null) {
            pressedFigure = getDrawing().findFigureInside(p);
        }

        if (pressedFigure instanceof TextHolderFigure) {
            textHolder = ((TextHolderFigure) pressedFigure).getLabelFor();
                textHolder = null;
        }
        if (textHolder != null) {
            beginEdit(textHolder);
            updateCursor(getView(), e.getPoint());
                    return;
        }
        if (typingTarget != null) {
            endEdit();
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        } else {
            super.mousePressed(e);
            // update view so the created figure is drawn before the floating text
            // figure is overlaid. 
            textHolder = (TextHolderFigure)getCreatedFigure();
            getView().clearSelection();
            getView().addToSelection(textHolder);
            beginEdit(textHolder);
            updateCursor(getView(), e.getPoint());
        }
    }
    
    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
    }
    
    protected void beginEdit(TextHolderFigure textHolder) {
        if (textField == null) {
            textField = new FloatingTextField();
            textField.addActionListener(this);
        }
        
        if (textHolder != typingTarget && typingTarget != null) {
            endEdit();
        }
        
        textField.createOverlay(getView(), textHolder);
        textField.requestFocus();
        typingTarget = textHolder;
    }
    
    
    @Override
    public void mouseReleased(MouseEvent evt) {
    }
    
    protected void endEdit() {
        if (typingTarget != null) {
            typingTarget.willChange();

            final TextHolderFigure editedFigure = typingTarget;
            final String oldText = typingTarget.getText();
            final String newText = textField.getText();

            if (newText.length() > 0) {
                typingTarget.setText(newText);
            } else {
                if (createdFigure != null) {
                    getDrawing().remove((Figure)getAddedFigure());
                // XXX - Fire undoable edit here!!
                } else {
                    typingTarget.setText("");
                    typingTarget.changed();
                }
            }
            UndoableEdit edit = new AbstractUndoableEdit() {

                @Override
                public String getPresentationName() {
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                    return labels.getString("attribute.text.text");
                }

                @Override
                public void undo() {
                    super.undo();
                    editedFigure.willChange();
                    editedFigure.setText(oldText);
                    editedFigure.changed();
                }

                @Override
                public void redo() {
                    super.redo();
                    editedFigure.willChange();
                    editedFigure.setText(newText);
                    editedFigure.changed();
                }
            };
            getDrawing().fireUndoableEditHappened(edit);

            typingTarget.changed();
            typingTarget = null;
            
            textField.endOverlay();
        }
        //	        view().checkDamage();
    }
    
    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE || isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }
    public void actionPerformed(ActionEvent event) {
        endEdit();
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }
    @Override
    protected void creationFinished(Figure createdFigure) {
        beginEdit((TextHolderFigure) createdFigure);
        updateCursor(getView(), new Point(0,0));
    }
    
    public boolean isEditing() {
        return typingTarget != null;
    }
    
    @Override
    public void updateCursor(DrawingView view, Point p) {
        if (view.isEnabled()) {
            view.setCursor(Cursor.getPredefinedCursor(isEditing() ? Cursor.DEFAULT_CURSOR : Cursor.CROSSHAIR_CURSOR));
        } else {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }
}
