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
import CH.ifa.draw.util.Undoable;
import java.util.*;

/**
 * @author: Helge Horch, Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public abstract class AbstractCommand implements Command, FigureSelectionListener {

    private String  myName;
	private Undoable myUndoableActivity;
	
	/**
	 * the DrawingView this command applies to
	 */
	private DrawingView fView;

	/**
	 * Constructs a command with the given name that applies to the given view.
	 * @param name java.lang.String
	 */
	public AbstractCommand(String newName, DrawingView newView) {
		setName(newName);
		setView(newView);
		view().addFigureSelectionListener(this);
	}

	/**
	 * @param view a DrawingView
	 */
	public void figureSelectionChanged(DrawingView view) {
	}

	/**
	 * @return view associated with this command
	 */	
	public DrawingView view() {
		return fView;
	}
	
	private void setView(DrawingView newView) {
		fView = newView;
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
		view().removeFigureSelectionListener(this);
	}

    /**
     * Executes the command.
     */
    public abstract void execute();

    /**
     * Tests if the command can be executed.
     */
    public boolean isExecutable() {
        return true;
    }

	public Undoable getUndoActivity() {
		return myUndoableActivity;
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		myUndoableActivity = newUndoableActivity;
	}
}