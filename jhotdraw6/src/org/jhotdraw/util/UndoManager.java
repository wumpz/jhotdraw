/*
 * @(#)UndoManager.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.*;
import java.util.*;

/**
 * This class manages all the undoable commands. It keeps track of all 
 * the modifications done through user interactions.
 *
 * @version <$CURRENT_VERSION$>
 */
public class UndoManager {
	/**
	 * Maximum default buffer size for undo and redo stack
	 */
	public static final int DEFAULT_BUFFER_SIZE = 20;

	/**
	 * Collection of undo activities
	 */
	private Vector redoStack;
		
	/**
	 * Collection of undo activities
	 */
	private Vector undoStack;
	private int maxStackCapacity;
	
	public UndoManager() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public UndoManager(int newUndoStackSize) {
		maxStackCapacity = newUndoStackSize;
		undoStack = new Vector(maxStackCapacity);
		redoStack = new Vector(maxStackCapacity);
	}

	public void pushUndo(Undoable undoActivity) {
		if (undoActivity.isUndoable()) {
			// If buffersize exceeds, remove the oldest command
			if (getUndoSize() >= maxStackCapacity) {
				undoStack.removeElementAt(0);
			}
		
			undoStack.addElement(undoActivity);
		}
		else {
			// a not undoable activity clears the stack because
			// the last activity does not correspond with the
			// last undo activity
			undoStack = new Vector(maxStackCapacity);
		}
	}

	public void pushRedo(Undoable redoActivity) {
		if (redoActivity.isRedoable()) {
			// If buffersize exceeds, remove the oldest command
			if (getRedoSize() >= maxStackCapacity) {
				redoStack.removeElementAt(0);
			}
		
			// add redo activity only if it is not already the last
			// one in the buffer
			if ((getRedoSize() == 0) || (peekRedo() != redoActivity)) {
				redoStack.addElement(redoActivity);
			}
		}
		else {
			// a not undoable activity clears the tack because
			// the last activity does not correspond with the
			// last undo activity
			redoStack = new Vector(maxStackCapacity);
		}
	}

	public boolean isUndoable() {
		if (getUndoSize() > 0) {
			return ((Undoable)undoStack.lastElement()).isUndoable();
		}
		else {
			return false;
		}
	}
	
	public boolean isRedoable() {
		if (getRedoSize() > 0) {
			return ((Undoable)redoStack.lastElement()).isRedoable();
		}
		else {
			return false;
		}
	}

	protected Undoable peekUndo() {
		if (getUndoSize() > 0) {
			return (Undoable) undoStack.lastElement();
		}
		else {
			return null;
		}
	}

	protected Undoable peekRedo() {
		if (getRedoSize() > 0) {
			return (Undoable) redoStack.lastElement();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the current size of undo buffer.
	 */
	public int getUndoSize() {
		return undoStack.size();
	}

	/**
	 * Returns the current size of redo buffer.
	 */
	public int getRedoSize() {
		return redoStack.size();
	}

	/**
	 * Throw NoSuchElementException if there is none
	 */
	public Undoable popUndo() {
		// Get the last element - throw NoSuchElementException if there is none
		Undoable lastUndoable = peekUndo();

		// Remove it from undo collection
		undoStack.removeElementAt(getUndoSize() - 1);
		
		return lastUndoable;
	}

	/**
	 * Throw NoSuchElementException if there is none
	 */
	public Undoable popRedo() {
		// Get the last element - throw NoSuchElementException if there is none
		Undoable lastUndoable = peekRedo();

		// Remove it from undo collection
		redoStack.removeElementAt(getRedoSize() - 1);

		return lastUndoable;
	}

	public void clearUndos() {
		clearStack(undoStack);
	}

	public void clearRedos() {
		clearStack(redoStack);
	}
	
	protected void clearStack(Vector clearStack) {
		clearStack.removeAllElements();
	}
}
