/*
 * @(#)JFontChooser.java  1.0  2008-05-18
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
package org.jhotdraw.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.jhotdraw.gui.fontchooser.*;
import org.jhotdraw.gui.plaf.*;
import org.jhotdraw.gui.plaf.palette.PaletteFontChooserUI;

/**
 * Font chooser dialog.
 * 
 * @author  Werner Randelshofer
 * @version 1.0 2008-05-18 Created.
 */
public class JFontChooser extends JComponent {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "FontChooserUI";
    /**
     * Identifies the "selectedFont" property.
     */
    public final static String SELECTED_FONT_PROPERTY = "selectedFont";
    /**
     * Identifies the "selectionPath" property.
     */
    public final static String SELECTION_PATH_PROPERTY = "selectionPath";
    /** Instruction to cancel the current selection. */
    public static final String CANCEL_SELECTION = "CancelSelection";

    /**
     * Instruction to approve the current selection
     * (same as pressing yes or ok).
     */
    public static final String APPROVE_SELECTION = "ApproveSelection";
    /**
     * Identifies the "model" property.
     */
    public final static String MODEL_PROPERTY = "model";
    /**
     * Holds the selected path of the JFontChooser.
     */
    private TreePath selectionPath;
    /**
     * Holds the selected font of the JFontChooser.
     */
    private Font selectedFont;
    /**
     * Holds the model of the JFontChooser.
     */
    private FontChooserModel model;
    
    // ********************************
    // ***** Dialog Return Values *****
    // ********************************

    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;

    /**
     * Return value if an error occured.
     */
    public static final int ERROR_OPTION = -1;
    
    private int returnValue = ERROR_OPTION;
    
    // DIALOG
    private JDialog dialog = null;

    /** Creates new form JFontChooser */
    public JFontChooser() {
        model = new DefaultFontChooserModel();
        updateUI();
    }

    /**
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        // Try to get a browser UI from the UIManager.
        // Fall back to BasicBrowserUI, if none is available.
        if (UIManager.get(getUIClassID()) != null) {
            setUI((FontChooserUI) UIManager.getUI(this));
        } else {
            setUI(PaletteFontChooserUI.createUI(this));
        }
    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the PanelUI object that renders this component
     * @since 1.4
     */
    public FontChooserUI getUI() {
        return (FontChooserUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui  the PanelUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel. 
     */
    public void setUI(FontChooserUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "FontChooserUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Called by the UI when the user hits the Approve button
     * (labeled "Open" or "Save", by default). This can also be
     * called by the programmer.
     * This method causes an action event to fire
     * with the command string equal to
     * <code>APPROVE_SELECTION</code>.
     *
     * @see #APPROVE_SELECTION
     */
    public void approveSelection() {
	returnValue = APPROVE_OPTION;
	if(dialog != null) {
	    dialog.setVisible(false);
	}
	fireActionPerformed(APPROVE_SELECTION);
    }

    /**
     * Called by the UI when the user chooses the Cancel button.
     * This can also be called by the programmer.
     * This method causes an action event to fire
     * with the command string equal to
     * <code>CANCEL_SELECTION</code>.
     *
     * @see #CANCEL_SELECTION
     */
    public void cancelSelection() {
	returnValue = CANCEL_OPTION;
	if(dialog != null) {
	    dialog.setVisible(false);
	}
	fireActionPerformed(CANCEL_SELECTION);
    }
   /**
     * Adds an <code>ActionListener</code> to the font chooser.
     *
     * @param l  the listener to be added
     * 
     * @see #approveSelection
     * @see #cancelSelection
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
 
    /**
     * Removes an <code>ActionListener</code> from the font chooser.
     *
     * @param l  the listener to be removed
     *
     * @see #addActionListener
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created using the <code>command</code> parameter.
     *
     * @see EventListenerList
     */
    protected void fireActionPerformed(String command) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        long mostRecentEventTime = EventQueue.getMostRecentEventTime();
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent)currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent)currentEvent).getModifiers();
        }
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                        command, mostRecentEventTime,
                                        modifiers);
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }
    /**
     * Gets the selected Font.
     * This is a bound property.
     * 
     * @return The selected font, or null, if no font is selected.
     */
    public TreePath getSelectionPath() {
        return selectionPath;
    }

    /**
     * Sets the selected Font.
     * This is a bound property.
     * <p>
     * Changing the selection path, causes a change of the
     * selected font, if the selected font is not the last
     * path segment of the selection path.
     * 
     * @param newValue The new selected font, or null if no font is to be
     * selected..
     */
    public void setSelectionPath(TreePath newValue) {
        TreePath oldValue = selectionPath;
        this.selectionPath = newValue;
        firePropertyChange(SELECTION_PATH_PROPERTY, oldValue, newValue);
        if (selectionPath.getPathCount() == 4) {
            setSelectedFont(((FontFaceNode) selectionPath.getLastPathComponent()).getFont());
        }
    }

    /**
     * Gets the selected Font.
     * This is a bound property.
     * 
     * @return The selected font, or null, if no font is selected.
     */
    public Font getSelectedFont() {
        return selectedFont;
    }

    /**
     * Sets the selected Font.
     * <p>
     * Changing the selected font, causes a change of the
     * selection path, if the selected font is not the last
     * path segment of the selection path.
     * 
     * This is a bound property.
     * 
     * @param newValue The new selected font, or null if no font is to be
     * selected.
     */
    public void setSelectedFont(Font newValue) {
        Font oldValue = selectedFont;
        this.selectedFont = newValue;
        firePropertyChange(SELECTED_FONT_PROPERTY, oldValue, newValue);

        if (newValue == null || selectionPath == null || selectionPath.getPathCount() != 4 ||
                !((FontFaceNode) selectionPath.getLastPathComponent()).getFont().getFontName().equals(newValue.getFontName())) {
            if (newValue == null) {
                setSelectionPath(null);
            } else {
                TreePath path = selectionPath;
                FontCollectionNode oldCollection = (path != null && path.getPathCount() > 1) ? (FontCollectionNode) path.getPathComponent(1) : null;
                FontFamilyNode oldFamily = (path != null && path.getPathCount() > 2) ? (FontFamilyNode) path.getPathComponent(2) : null;
                FontFaceNode oldFace = (path != null && path.getPathCount() > 3) ? (FontFaceNode) path.getPathComponent(3) : null;

                FontCollectionNode newCollection = oldCollection;
                FontFamilyNode newFamily = oldFamily;
                FontFaceNode newFace = null;

                // search in the current family
                if (newFace == null && newFamily != null) {
                    for (FontFaceNode face : newFamily.faces()) {
                        if (face.getFont().getFontName().equals(newValue.getFontName())) {
                            newFace = face;
                            break;
                        }
                    }
                }
                // search in the current collection
                if (newFace == null && newCollection != null) {
                    for (FontFamilyNode family : newCollection.families()) {
                        for (FontFaceNode face : family.faces()) {
                            if (face.getFont().getFontName().equals(newValue.getFontName())) {
                                newFamily = family;
                                newFace = face;
                                break;
                            }
                        }
                    }
                }
                // search in all collections
                if (newFace == null) {
                    TreeNode root = (TreeNode) getModel().getRoot();
                    for (int i = 0, n = root.getChildCount(); i < n; i++) {
                        FontCollectionNode collection = (FontCollectionNode) root.getChildAt(i);
                        for (FontFamilyNode family : collection.families()) {
                            for (FontFaceNode face : family.faces()) {
                                if (face.getFont().getFontName().equals(newValue.getFontName())) {
                                    newCollection = collection;
                                    newFamily = family;
                                    newFace = face;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                if (newFace != null) {
                    setSelectionPath(new TreePath(new Object[] {
                        getModel().getRoot(), newCollection, newFamily, newFace }));
                } else {
                    setSelectionPath(null);
                }
            }
        }
        }

    /**
     * Gets the selected Font.
     * This is a bound property.
     * 
     * @return The selected font, or null, if no font is selected.
     */
    

    public FontChooserModel getModel() {
        return model;
    }

    /**
     * Sets the selected Font.
     * This is a bound property.
     * 
     * @param newValue The new selected font, or null if no font is to be
     * selected..
     */
    public void setModel(FontChooserModel newValue) {
        FontChooserModel oldValue = model;
        this.model = newValue;
        firePropertyChange(MODEL_PROPERTY, oldValue, newValue);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * /
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
