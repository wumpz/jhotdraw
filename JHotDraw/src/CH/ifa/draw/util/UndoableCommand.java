/*
 * @(#)UndoableCommand.java
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

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class UndoableCommand implements Command, FigureSelectionListener {

	private Command myWrappedCommand;
	private boolean hasSelectionChanged;
	
	public UndoableCommand(Command newWrappedCommand) {
		setWrappedCommand(newWrappedCommand);
	}
	
    /**
     * Executes the command.
     */
    public void execute() {
    	hasSelectionChanged = false;
    	// listen for selection change events during executing the wrapped command
    	view().addFigureSelectionListener(this);

    	getWrappedCommand().execute();

    	Undoable undoableCommand = getWrappedCommand().getUndoActivity();
    	if ((undoableCommand != null) && (undoableCommand.isUndoable())) {
		    view().getUndoManager().pushUndo(undoableCommand);
			view().getUndoManager().clearRedos();
    	}
    	
    	// initiate manual update of undo/redo menu states if it has not
    	// been done automatically during executing the wrapped command
    	if (!hasSelectionChanged || (view().getUndoManager().getUndoSize() == 1)) {
    		view().editor().figureSelectionChanged(view());
    	}

    	// remove so not all commands are listeners that have to be notified
    	// all the time
    	view().addFigureSelectionListener(this);
    }

    /**
     * Tests if the command can be executed.
     */
    public boolean isExecutable() {
    	return getWrappedCommand().isExecutable();
    }

	protected void setWrappedCommand(Command newWrappedCommand) {
		myWrappedCommand = newWrappedCommand;
	}
	
	protected Command getWrappedCommand() {
		return myWrappedCommand;
	}
	
    /**
     * Gets the command name.
     */
    public String name() {
    	return getWrappedCommand().name();
    }

    public DrawingView view() {
    	return getWrappedCommand().view();
    }

	public void figureSelectionChanged(DrawingView view) {
		hasSelectionChanged = true;
	}

	public Undoable getUndoActivity() {
		return new UndoableAdapter(view());
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		// do nothing: always return default UndoableAdapter
	}
}
