/*
 * @(#)AbstractCommand.java
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
import CH.ifa.draw.util.Command;
import CH.ifa.draw.util.CommandListener;
import CH.ifa.draw.util.Undoable;
import java.util.*;

/**
 * @author: Helge Horch, Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public abstract class AbstractCommand implements Command, FigureSelectionListener, ViewChangeListener {

	private String  myName;
	private Undoable myUndoableActivity;
	private boolean myIsViewRequired;
	private AbstractCommand.EventDispatcher myEventDispatcher;
		
	/**
	 * the DrawingEditor this command applies to
	 */
	private DrawingEditor myDrawingEditor;

	/**
	 * Constructs a command with the given name that applies to the given view.
	 * @param name java.lang.String
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public AbstractCommand(String newName, DrawingEditor newDrawingEditor) {
		this(newName, newDrawingEditor, true);
	}

	public AbstractCommand(String newName, DrawingEditor newDrawingEditor, boolean newIsViewRequired) {
		setName(newName);
		setDrawingEditor(newDrawingEditor);
		getDrawingEditor().addViewChangeListener(this);
		myIsViewRequired = newIsViewRequired;
		setEventDispatcher(createEventDispatcher());
	}

	public void viewSelectionChanged(DrawingView oldView, DrawingView newView) {
		if (oldView != null) {
			oldView.removeFigureSelectionListener(this);
		}
		if (newView != null) {
			newView.addFigureSelectionListener(this);
		}
		if (isViewRequired()) {
			boolean isOldViewInteractive = (oldView != null) && oldView.isInteractive();
			boolean isNewViewInteractive = (newView != null) && newView.isInteractive();
			// old view was not interactive aware while new view is now interactive aware
			if (!isOldViewInteractive && isNewViewInteractive) {
				getEventDispatcher().fireCommandExecutableEvent();
			}
			// old view was interactive aware while new view is not
			else if (isOldViewInteractive && !isNewViewInteractive) {
				getEventDispatcher().fireCommandNotExecutableEvent();
			}
		}
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
	 * @param view a DrawingView
	 */
	public void figureSelectionChanged(DrawingView view) {
	}

	/**
	 * @return DrawingEditor associated with this command
	 */	
	public DrawingEditor getDrawingEditor() {
		return myDrawingEditor;
	}
	
	private void setDrawingEditor(DrawingEditor newDrawingEditor) {
		myDrawingEditor = newDrawingEditor;
	}

	/**
	 * Convenience method
	 *
	 * @return DrawingView currently active in the editor
	 */
	public DrawingView view() {
		return getDrawingEditor().view();
	}
	
	/**
	 * Gets the command name.
	 */
	public String name() {
		return myName;
	}
	
	public void setName(String newName) {
		myName = newName;
	}
	
	/**
	 * Releases resources associated with this command
	 */
	public void dispose() {
		if (view() != null) {
			view().removeFigureSelectionListener(this);
		}
	}

	/**
	 * Executes the command.
	 */
	public void execute() {
		if (view() == null) {
			throw new JHotDrawRuntimeException("execute should NOT be getting called when view() == null");
		};
	}

	/**
	 * Tests if the command can be executed. The view must be valid when this is
	 * called. Per default, a command is executable if at
	 * least one figure is selected in the current activated
	 * view.
	 */
	public boolean isExecutable() {
		// test whether there is a view required and whether an existing view 
		// accepts user input
		if (isViewRequired()) {
			if ((view() == null) || !view().isInteractive()) {
				return false;
			}
		}
		return isExecutableWithView();
	}

	protected boolean isViewRequired() {
		return myIsViewRequired;
	}

	protected boolean isExecutableWithView() {
		return true;
	}
	
	public Undoable getUndoActivity() {
		return myUndoableActivity;
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		myUndoableActivity = newUndoableActivity;
	}

	public void addCommandListener(CommandListener newCommandListener) {
		getEventDispatcher().addCommandListener(newCommandListener);
	}
	
	public void removeCommandListener(CommandListener oldCommandListener) {
		getEventDispatcher().removeCommandListener(oldCommandListener);
	}

	private void setEventDispatcher(AbstractCommand.EventDispatcher newEventDispatcher) {
		myEventDispatcher = newEventDispatcher;
	}

	protected AbstractCommand.EventDispatcher getEventDispatcher() {
		return myEventDispatcher;
	}

	public AbstractCommand.EventDispatcher createEventDispatcher() {
		return new AbstractCommand.EventDispatcher(this);
	}

	public static class EventDispatcher {
		private Vector myRegisteredListeners;
		private Command myObservedCommand;
		
		public EventDispatcher(Command newObservedCommand) {
			myRegisteredListeners = new Vector();
			myObservedCommand = newObservedCommand;
		}
		
		public void fireCommandExecutedEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((CommandListener)le.nextElement()).commandExecuted(new EventObject(myObservedCommand));
			}
		}
		
		public void fireCommandExecutableEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((CommandListener)le.nextElement()).commandExecutable(new EventObject(myObservedCommand));
			}
		}

		public void fireCommandNotExecutableEvent() {
			Enumeration le = myRegisteredListeners.elements();
			while (le.hasMoreElements()) {
				((CommandListener)le.nextElement()).commandNotExecutable(new EventObject(myObservedCommand));
			}
		}

		public void addCommandListener(CommandListener newCommandListener) {
			if (!myRegisteredListeners.contains(newCommandListener)) {
				myRegisteredListeners.add(newCommandListener);
			}
		}
		
		public void removeCommandListener(CommandListener oldCommandListener) {
			if (myRegisteredListeners.contains(oldCommandListener)) {
				myRegisteredListeners.remove(oldCommandListener);
			}
		}
	}
}