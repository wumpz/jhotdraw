
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;



/**
 * An ActionMap class that defines cut/copy/paste/delete.
 * 
 * This class only exists to paper over limitations in the standard JTextComponent
 * cut/copy/paste/delete javax.swing.Actions.  The standard cut/copy Actions don't 
 * keep their enabled property in sync with having the focus and (for copy) having
 * a non-empty text selection.  The standard paste Action's enabled property doesn't
 * stay in sync with the current contents of the clipboard.  The paste/copy/delete
 * actions must also track the JTextComponent editable property.
 * 
 * The new cut/copy/paste/delete are installed lazily, when a JTextComponent gets 
 * the focus, and before any other focus-change related work is done.  See
 * updateFocusOwner().
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @author Scott Violet (Scott.Violet@Sun.COM)
 */
class TextActions extends AbstractBean {
    private final ApplicationContext context;
    private final CaretListener textComponentCaretListener;
    private final PropertyChangeListener textComponentPCL;
    private final String markerActionKey = "TextActions.markerAction";
    private final javax.swing.Action markerAction;
    private boolean copyEnabled = false;    // see setCopyEnabled
    private boolean cutEnabled = false;     // see setCutEnabled
    private boolean pasteEnabled = false;   // see setPasteEnabled
    private boolean deleteEnabled = false;  // see setDeleteEnabled

    public TextActions(ApplicationContext context) {
        this.context = context;
	markerAction = new javax.swing.AbstractAction() { 
	    public void actionPerformed(ActionEvent e) { } 
        };
	textComponentCaretListener = new TextComponentCaretListener();
	textComponentPCL = new TextComponentPCL();
	getClipboard().addFlavorListener(new ClipboardListener());
    }

    private ApplicationContext getContext() {
        return context;
    }

    private JComponent getFocusOwner() {
	return 	getContext().getFocusOwner();
    }

    private Clipboard getClipboard() {
	return getContext().getClipboard();
    }

    /* Called by the KeyboardFocus PropertyChangeListener in ApplicationContext,
     * before any other focus-change related work is done.
     */
    void updateFocusOwner(JComponent oldOwner, JComponent newOwner) {
	if (oldOwner instanceof JTextComponent) {
	    JTextComponent text = (JTextComponent)oldOwner;
	    text.removeCaretListener(textComponentCaretListener);
	    text.removePropertyChangeListener(textComponentPCL);
	}
	if (newOwner instanceof JTextComponent) {
	    JTextComponent text = (JTextComponent)newOwner;
	    maybeInstallTextActions(text);
	    updateTextActions(text);
	    text.addCaretListener(textComponentCaretListener);
	    text.addPropertyChangeListener(textComponentPCL);
	}
	else if (newOwner == null) {
	    setCopyEnabled(false);
	    setCutEnabled(false);
	    setPasteEnabled(false);
	    setDeleteEnabled(false);
	}
    }

    private final class ClipboardListener implements FlavorListener {
	public void flavorsChanged(FlavorEvent e) {
	    JComponent c = getFocusOwner();
	    if (c instanceof JTextComponent) {
		updateTextActions((JTextComponent)c);
	    }
	}
    }

    private final class TextComponentCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
	    updateTextActions((JTextComponent)(e.getSource()));
        }
    }

    private final class TextComponentPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ((propertyName == null) || "editable".equals(propertyName)) {
		updateTextActions((JTextComponent)(e.getSource()));
	    }
	}
    }

    private void updateTextActions(JTextComponent text) {
	Caret caret = text.getCaret();
	boolean selection = (caret.getDot() != caret.getMark());
	boolean editable = text.isEditable();
	boolean data = getClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
	setCopyEnabled(selection);
	setCutEnabled(editable && selection);
	setDeleteEnabled(editable && selection);
	setPasteEnabled(editable && data);
    }

    // TBD: what if text.getActionMap is null, or if it's parent isn't the UI-installed actionMap
    private void maybeInstallTextActions(JTextComponent text) {
	ActionMap actionMap = text.getActionMap();
	if (actionMap.get(markerActionKey) == null) {
	    actionMap.put(markerActionKey, markerAction);
	    ActionMap textActions = getContext().getActionMap(getClass(), this);
	    for(Object key : textActions.keys()) {
		actionMap.put(key, textActions.get(key));
	    }
	}
    }


    /* This method lifted from JTextComponent.java 
     */
    private int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent)currentEvent).getModifiers();
        } 
	else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent)currentEvent).getModifiers();
        }
        return modifiers;
    }

    private void invokeTextAction(JTextComponent text, String actionName) {
        ActionMap actionMap = text.getActionMap().getParent();
	long eventTime = EventQueue.getMostRecentEventTime();
	int eventMods = getCurrentEventModifiers();
	ActionEvent actionEvent = 
	    new ActionEvent(text, ActionEvent.ACTION_PERFORMED, actionName, eventTime, eventMods);
	actionMap.get(actionName).actionPerformed(actionEvent);
    }

    @Action(enabledProperty = "cutEnabled")
    public void cut(ActionEvent e) {
	Object src = e.getSource();
	if (src instanceof JTextComponent) {
	    invokeTextAction((JTextComponent)src, "cut");
	}
    }

    public boolean isCutEnabled() { return cutEnabled; }

    public void setCutEnabled(boolean cutEnabled) { 
	boolean oldValue = this.cutEnabled; 
	this.cutEnabled = cutEnabled;
	firePropertyChange("cutEnabled", oldValue, this.cutEnabled);
    }

    @Action(enabledProperty = "copyEnabled")
    public void copy(ActionEvent e) {
	Object src = e.getSource();
	if (src instanceof JTextComponent) {
	    invokeTextAction((JTextComponent)src, "copy");
	}
    }

    public boolean isCopyEnabled() { return copyEnabled; }

    public void setCopyEnabled(boolean copyEnabled) { 
	boolean oldValue = this.copyEnabled; 
	this.copyEnabled = copyEnabled;
	firePropertyChange("copyEnabled", oldValue, this.copyEnabled);
    }

    @Action(enabledProperty = "pasteEnabled")
    public void paste(ActionEvent e) {
	Object src = e.getSource();
	if (src instanceof JTextComponent) {
	    invokeTextAction((JTextComponent)src, "paste");
	}
    }

    public boolean isPasteEnabled() { return pasteEnabled; }

    public void setPasteEnabled(boolean pasteEnabled) { 
	boolean oldValue = this.pasteEnabled; 
	this.pasteEnabled = pasteEnabled;
	firePropertyChange("pasteEnabled", oldValue, this.pasteEnabled);
    }

    @Action(enabledProperty = "deleteEnabled")
    public void delete(ActionEvent e) {
	Object src = e.getSource();
	if (src instanceof JTextComponent) {
	    /* The deleteNextCharAction is bound to the delete key in
	     * text components.  The name appears to be a misnomer,
	     * however it's really a compromise.  Calling the method
	     * by a more accurate name,
	     *   "IfASelectionExistsThenDeleteItOtherwiseDeleteTheNextCharacter"
	     * would be rather unwieldy.
	     */
	    invokeTextAction((JTextComponent)src, DefaultEditorKit.deleteNextCharAction);
	}
    }

    public boolean isDeleteEnabled() { return deleteEnabled; }

    public void setDeleteEnabled(boolean deleteEnabled) { 
	boolean oldValue = this.deleteEnabled; 
	this.deleteEnabled = deleteEnabled;
	firePropertyChange("deleteEnabled", oldValue, this.deleteEnabled);
    }
}
