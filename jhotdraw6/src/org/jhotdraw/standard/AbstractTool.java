/*
 * @(#)AbstractTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Undoable;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class AbstractTool implements Tool, ViewChangeListener {

	private DrawingEditor     myDrawingEditor;

	/**
	 * The position of the initial mouse down.
	 */
	protected int     fAnchorX, fAnchorY;

	private Undoable myUndoActivity;
	private AbstractTool.EventDispatcher myEventDispatcher;

	private boolean myIsUsable;

	/**
	 * Flag to indicate whether to perform usable checks or not
	 */
	private boolean myIsEnabled;

	/**
	 * Constructs a tool for the given view.
	 */
	public AbstractTool(DrawingEditor newDrawingEditor) {
		myDrawingEditor = newDrawingEditor;
		setEventDispatcher(createEventDispatcher());
		setEnabled(true);
		editor().addViewChangeListener(this);
	}

	/**
	 * Activates the tool for use on the given view. This method is called
	 * whenever the user switches to this tool. Use this method to
	 * reinitialize a tool.
	 * Since tools will be disabled unless it is useable, there will always
	 * be an active view when this is called. based on isUsable()
	 */
	public void activate() {
		if (view() != null) {
			view().clearSelection();
			getEventDispatcher().fireToolActivatedEvent();
		}
	}

	/**
	 * Deactivates the tool. This method is called whenever the user
	 * switches to another tool. Use this method to do some clean-up
	 * when the tool is switched. Subclassers should always call
	 * super.deactivate.
	 */
	public void deactivate() {
		if (isActive()) {
			if (view() != null) {
				view().setCursor(Cursor.getDefaultCursor());
			}
			getEventDispatcher().fireToolDeactivatedEvent();
		}
	}

	/**
	 * Fired when the selected view changes.
	 * Subclasses should always call super.  ViewSelectionChanged() this allows
	 * the tools state to be updated and referenced to the new view.
	 */
	public void viewSelectionChanged(DrawingView oldView, DrawingView newView) {
		if (isActive()) {
			deactivate();
			activate();
		}

		checkUsable();
	}

	/**
	 * Sent when a new view is created
	 */
	public void viewCreated(DrawingView view) {
	}

	/**
	 * Send when an existing view is about to be destroyed.
	 */
	public void viewDestroying(DrawingView view) {
	}

	/**
	 * Handles mouse down events in the drawing view.
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		fAnchorX = x;
		fAnchorY = y;
	}

	/**
	 * Handles mouse drag events in the drawing view.
	 */
	public void mouseDrag(MouseEvent e, int x, int y) {
	}

	/**
	 * Handles mouse up in the drawing view.
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
	}

	/**
	 * Handles mouse moves (if the mouse button is up).
	 */
	public void mouseMove(MouseEvent evt, int x, int y) {
	}

	/**
	 * Handles key down events in the drawing view.
	 */
	public void keyDown(KeyEvent evt, int key) {
	}

	/**
	 * Gets the tool's drawing.
	 */
	public Drawing drawing() {
		return view().drawing();
	}

	/**
	 * Gets the tool's editor.
	 */
	public DrawingEditor editor() {
		return myDrawingEditor;
	}

	/**
	 * Gets the tool's view (convienence method).
	 */
	public DrawingView view() {
		return editor().view();
	}

	/**
	 * Tests if the tool can be used or "executed."
	 */
	public boolean isUsable() {
		return isEnabled() && myIsUsable;
	}

	public void setUsable(boolean newIsUsable) {
		// perform notification only if the usable state of the tool has changed
		if (isUsable() != newIsUsable) {
			myIsUsable = newIsUsable;
			if (isUsable()) {
				getEventDispatcher().fireToolUsableEvent();
			}
			else {
				getEventDispatcher().fireToolUnusableEvent();
			}
		}
	}

	public void setEnabled(boolean newIsEnabled) {
		// perform notification only if the usable state of the tool has changed
		if (isEnabled() != newIsEnabled) {
			myIsEnabled = newIsEnabled;
			if (isEnabled()) {
				getEventDispatcher().fireToolEnabledEvent();
			}
			else {
				getEventDispatcher().fireToolDisabledEvent();
				setUsable(false);
				deactivate();
			}
		}
	}

	public boolean isEnabled() {
		return myIsEnabled;
	}

	public Undoable getUndoActivity() {
		return myUndoActivity;
	}

	public void setUndoActivity(Undoable newUndoActivity) {
		myUndoActivity = newUndoActivity;
	}

	public boolean isActive() {
		return (editor().tool() == this) && isUsable();
	}

	public void addToolListener(ToolListener newToolListener) {
		getEventDispatcher().addToolListener(newToolListener);
	}

	public void removeToolListener(ToolListener oldToolListener) {
		getEventDispatcher().removeToolListener(oldToolListener);
	}

	private void setEventDispatcher(AbstractTool.EventDispatcher newEventDispatcher) {
		myEventDispatcher = newEventDispatcher;
	}

	protected AbstractTool.EventDispatcher getEventDispatcher() {
		return myEventDispatcher;
	}

	public AbstractTool.EventDispatcher createEventDispatcher() {
		return new AbstractTool.EventDispatcher(this);
	}

	protected void checkUsable() {
		if (isEnabled()) {
			setUsable((view() != null) && view().isInteractive());
		}
	}

	public static class EventDispatcher {
		private Vector myRegisteredListeners;
		private Tool myObservedTool;

		public EventDispatcher(Tool newObservedTool) {
			myRegisteredListeners = new Vector();
			myObservedTool = newObservedTool;
		}

		public void fireToolUsableEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolUsable(new EventObject(myObservedTool));
			}
		}

		public void fireToolUnusableEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolUnusable(new EventObject(myObservedTool));
			}
		}

		public void fireToolActivatedEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolActivated(new EventObject(myObservedTool));
			}
		}

		public void fireToolDeactivatedEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolDeactivated(new EventObject(myObservedTool));
			}
		}

		public void fireToolEnabledEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolEnabled(new EventObject(myObservedTool));
			}
		}

		public void fireToolDisabledEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((ToolListener)le.nextElement()).toolDisabled(new EventObject(myObservedTool));
			}
		}

		public void addToolListener(ToolListener newToolListener) {
			if (!myRegisteredListeners.contains(newToolListener)) {
				myRegisteredListeners.add(newToolListener);
			}
		}

		public void removeToolListener(ToolListener oldToolListener) {
			if (myRegisteredListeners.contains(oldToolListener)) {
				myRegisteredListeners.remove(oldToolListener);
			}
		}
	}
}
