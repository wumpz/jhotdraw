/*
 * @(#)JStringAttributeField.java  1.0  April 22, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;

/**
 * A JTextArea that can be used to edit a String attribute of a Figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 22, 2007 Created.
 */
public class JStringAttributeArea extends JTextArea {
    private static final boolean DEBUG = false;
    
    private DrawingEditor editor;
    private AttributeKey<String> attributeKey;
    private boolean isMultipleValues;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private int isUpdatingField = 0;
    
    private PropertyChangeListener viewEventHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    
    private class EditorEventHandler implements PropertyChangeListener, FigureSelectionListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    view.removeFigureSelectionListener(this);
                    view.removePropertyChangeListener(viewEventHandler);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    view.addFigureSelectionListener(this);
                    view.addPropertyChangeListener(viewEventHandler);
                }
                updateEnabledState();
                updateField();
            } else if (attributeKey != null && name.equals(attributeKey.getKey())) {
                updateField();
            }
        }
        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();
            updateField();
        }
    };
    
    private EditorEventHandler eventHandler = new EditorEventHandler();
    
    /** Creates new instance. */
    public JStringAttributeArea() {
        this(null, null);
    }
    public JStringAttributeArea(DrawingEditor editor, AttributeKey<String> attributeKey) {
        initComponents();
        this.attributeKey = attributeKey;
        setEditor(editor);
        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateFigures();
            }
            
            public void removeUpdate(DocumentEvent e) {
                updateFigures();
            }
            
            public void changedUpdate(DocumentEvent e) {
                updateFigures();
            }
        });
        /*
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateFigures();
            }
        });
        */
    }
    
    public void setAttributeKey(AttributeKey<String> attributeKey) {
        this.attributeKey = attributeKey;
    }
    public void setEditor(DrawingEditor editor) {
        if (DEBUG) System.out.println("JFigureAttributeField.setEditor("+editor+")");
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (getView() != null) {
                getView().removeFigureSelectionListener(eventHandler);
            }
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(eventHandler);
            if (getView() != null) {
                getView().addFigureSelectionListener(eventHandler);
            }
            updateEnabledState();
            updateField();
        }
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    protected DrawingView getView() {
        return (editor == null) ? null : editor.getActiveView();
    }
    
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled() &&
                    getView().getSelectionCount() > 0
                    );
        } else {
            setEnabled(false);
        }
    }
    protected void updateField() {
        if (DEBUG) System.out.println("JFigureAttributeField.updateText");
        //if (! isFocusOwner()) {
        if (isUpdatingField++ == 0) {
            if (getView() == null || attributeKey == null) {
                setText("");
            } else {
                String fieldValue = null;
                boolean isFirst = true;
                isMultipleValues = false;
                for (Figure f : getView().getSelectedFigures()) {
                    if (isFirst) {
                        isFirst = false;
                        fieldValue = attributeKey.get(f);
                    } else {
                        String figureValue = attributeKey.get(f);
                        if (figureValue == fieldValue ||
                                figureValue != null && fieldValue != null &&
                                figureValue.equals(fieldValue)) {
                        } else {
                            fieldValue = null;
                            isMultipleValues = true;
                        }
                    }
                }
                setText(fieldValue);
            }
            repaint();
        }
        isUpdatingField--;
    }
    
    private void updateFigures() {
        if (isUpdatingField++ == 0) {
            String fieldValue = getText().trim();
            if (fieldValue.length() == 0) {
                fieldValue = null;
            }
            if (getView() != null && attributeKey != null) {
                for (Figure f : getView().getSelectedFigures()) {
                    attributeKey.set(f, fieldValue);
                }
            }
            // Don't set editor default attribute
            //editor.setDefaultAttribute(attributeKey, fieldValue);
        }
        isUpdatingField--;
    }
    
    public void dispose() {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (this.editor.getActiveView() != null) {
                this.editor.getActiveView().removeFigureSelectionListener(eventHandler);
            }
        }
        this.editor = null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (! isFocusOwner() && isMultipleValues) {
            Insets insets = getInsets();
            Insets margin = getMargin();
            int height = getHeight();
            FontMetrics fm = g.getFontMetrics(getFont());
            //g.setColor(Color.DARK_GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            g.drawString(labels.getString("multipleValues"),
                    insets.left + margin.left,
                    insets.top + margin.top + fm.getAscent()
                    );
        }
    }
    
}
