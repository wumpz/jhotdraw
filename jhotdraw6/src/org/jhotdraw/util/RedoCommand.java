/*
 * @(#)RedoCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.util.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.framework.*;

/**
 * Command to redo the latest undone change in the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */ 
public class RedoCommand extends AbstractCommand {

	/**
	 * Constructs a properties command.
	 * @param name the command name
	 * @param view the target view
	 */
	public RedoCommand(String name, DrawingView inView) {
		super(name, inView);
	}

	public void execute() {
		UndoManager um = view().getUndoManager();
System.out.println("UndoManager.isRedoable(): " + um.isRedoable() + " .. " + um.peekRedo());
		if ((um == null) || !um.isRedoable()) {
			return;
		}
		
		Undoable lastRedoable = um.popRedo();

		// Execute redo
		boolean hasBeenUndone = lastRedoable.redo();
		// Add to undo stack
		if (hasBeenUndone && lastRedoable.isUndoable()) {
System.out.println("hasBeenUndone: " + hasBeenUndone);
			um.pushUndo(lastRedoable);
		}
			
		view().checkDamage();

		view().editor().figureSelectionChanged(view());
	}
  
	/**
	 * Used in enabling the redo menu item.
	 * Redo menu item will be enabled only when there is at least one redoable
	 * activity in the UndoManager.
	 */
	public boolean isExecutable() {
		UndoManager um = view().getUndoManager();
		if ((um != null) && (um.getRedoSize() > 0)) {
			return true;
		}

	    return false;
	}
}
