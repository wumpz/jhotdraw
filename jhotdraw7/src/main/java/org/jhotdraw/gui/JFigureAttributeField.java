/*
 * @(#)JFigureAttributeField.java  1.0  April 22, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
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
 * A JTextField that can be used to edit a String attribute of a Figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 22, 2007 Created.
 */
public class JFigureAttributeField extends JTextField {
    private static final boolean DEBUG = false;
    
    private DrawingEditor editor;
    private AttributeKey<String> attributeKey;
    private boolean isMultipleValues;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private int isUpdatingText = 0;
    
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    
    private class EventHandler implements PropertyChangeListener, FigureSelectionListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("focusedView")) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    view.removeFigureSelectionListener(this);
                    view.removePropertyChangeListener(propertyChangeHandler);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    view.addFigureSelectionListener(this);
                    view.addPropertyChangeListener(propertyChangeHandler);
                }
                updateEnabledState();
            }
        }
        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();
            updateText();
        }
    };
    
    private EventHandler eventHandler = new EventHandler();
    
    /** Creates new instance. */
    public JFigureAttributeField() {
        this(null, null);
    }
    public JFigureAttributeField(DrawingEditor editor, AttributeKey<String> attributeKey) {
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
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateFigures();
            }
        });
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
        }
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    protected DrawingView getView() {
        return (editor == null) ? null : editor.getFocusedView();
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
    protected void updateText() {
        if (DEBUG) System.out.println("JFigureAttributeField.updateText");
        //if (! isFocusOwner()) {
            isUpdatingText++;
            if (getView() == null || attributeKey == null) {
                setText("");
            } else {
                String href = null;
                boolean isFirst = true;
                isMultipleValues = false;
                for (Figure f : getView().getSelectedFigures()) {
                    if (isFirst) {
                        isFirst = false;
                        href = attributeKey.get(f);
                    } else {
                        String figureHref = attributeKey.get(f);
                        if (figureHref == href ||
                                figureHref != null && href != null &&
                                figureHref.equals(href)) {
                        } else {
                            href = null;
                            isMultipleValues = true;
                        }
                    }
                }
                setText(href);
            }
            repaint();
            isUpdatingText--;
       // }
    }
    
    private void updateFigures() {
        if (isUpdatingText == 0) {
            String text = getText().trim();
            if (text.length() == 0) {
                text = null;
            }
            if (getView() != null && attributeKey != null) {
                for (Figure f : getView().getSelectedFigures()) {
                    attributeKey.set(f, text);
                }
            }
        }
    }
    
    public void dispose() {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (this.editor.getView() != null) {
                this.editor.getView().removeFigureSelectionListener(eventHandler);
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
