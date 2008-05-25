/*
 * @(#)TextTool.java  2.4  2008-05-24
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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
import javax.swing.event.*;
import java.util.*;
/**
 * A tool to create new or edit existing figures that implement the TextHolderFigure
 * interface, such as TextFigure. The figure to be created is specified by a
 * prototype.
 * <p>
 * To create a figure using the TextTool, the user does the following mouse
 * gestures on a DrawingView:
 * <ol>
 * <li>Press the mouse button over an area on the DrawingView on which there
 * isn't a text figure present. This defines the location of the Figure.</li>
 * </ol>
 * When the user has performed this mouse gesture, the TextTool overlays
 * a text field over the drawing where the user can enter the text for the Figure.
 * <p>
 * To edit an existing text figure using the TextTool, the user does the
 * following mouse gesture on a DrawingView:
 * <ol>
 * <li>Press the mouse button over a TextHolderFigure Figure on the DrawingView.</li>
 * </ol>
 * <p>
 * The TextTool then uses Figure.findFigureInside to find a Figure that
 * implements the TextHolderFigure interface and that is editable. Then it overlays
 * a text field over the drawing where the user can enter the text for the Figure.
 * <p>
 * </p>
 * XXX - Maybe this class should be split up into a CreateTextTool and
 * a EditTextTool.
 * </p>
 *
 *
 * @see TextHolderFigure
 * @see FloatingTextField
 *
 * @author Werner Randelshofer
 * @version 2.4 2008-05-24 Moved code from this class into FloatingTextField.
 * <br>2.3 2008-05-17 Honor toolDoneAfterCreation property.
 * <br>2.2 2007-11-30 Added variable isUsedForCreation.  
 * <br>2.1 2007-08-22 Added support for property 'toolDoneAfterCreation'.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class TextTool extends CreationTool implements ActionListener {
    private FloatingTextField   textField;
    private TextHolderFigure  typingTarget;
    /**
     * By default this tool is only used for the creation of new TextHolderFigures.
     * If this variable is set to false, the tool is used to create new
     * TextHolderFigure and edit existing TextHolderFigure.
     */
    private boolean isForCreationOnly = true;
    
    /** Creates a new instance. */
    public TextTool(TextHolderFigure prototype) {
        super(prototype);
    }
    /** Creates a new instance. */
    public TextTool(TextHolderFigure prototype, Map attributes) {
        super(prototype, attributes);
    }
    
    public void deactivate(DrawingEditor editor) {
        endEdit();
        super.deactivate(editor);
    }
    /**
     * By default this tool is used to create a new TextHolderFigure.
     * If this property is set to false, the tool is used to create
     * a new TextHolderFigure or to edit an existing TextHolderFigure.
     */
    public void setForCreationOnly(boolean newValue) {
        isForCreationOnly = newValue;
    }
    /**
     * Returns true, if this tool can be only be used for creation of
     * TextHolderFigures and not for editing existing ones. 
     */
    public boolean isForCreationOnly() {
        return isForCreationOnly;
    }
    
    
    /**
     * If the pressed figure is a TextHolderFigure it can be edited otherwise
     * a new text figure is created.
     */
    public void mousePressed(MouseEvent e) {
        TextHolderFigure textHolder = null;
        Figure pressedFigure = getDrawing().findFigureInside(getView().viewToDrawing(new Point(e.getX(), e.getY())));
        if (pressedFigure instanceof TextHolderFigure) {
            textHolder = ((TextHolderFigure) pressedFigure).getLabelFor();
            if (!textHolder.isEditable() || isForCreationOnly)
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
            // figure is overlaid. (Note, fDamage should be null in StandardDrawingView
            // when the overlay figure is drawn because a JTextField cannot be scrolled)
            //view().checkDamage();
            textHolder = (TextHolderFigure)getCreatedFigure();
            getView().clearSelection();
            getView().addToSelection(textHolder);
            beginEdit(textHolder);
            updateCursor(getView(), e.getPoint());
        }
    }
    
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
    
    
    public void mouseReleased(MouseEvent evt) {
    }
    
    protected void endEdit() {
        if (typingTarget != null) {
            typingTarget.willChange();
            if (textField.getText().length() > 0) {
                typingTarget.setText(textField.getText());
            } else {
                if (createdFigure != null) {
                    getDrawing().remove((Figure)getAddedFigure());
                } else {
                    typingTarget.setText("");
                    typingTarget.changed();
                }
            }
            // XXX - Implement Undo/Redo behavior here
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
