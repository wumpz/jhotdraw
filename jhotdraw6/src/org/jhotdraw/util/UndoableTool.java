/*
 * @(#)UndoableTool.java
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
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class UndoableTool implements Tool {

	private Tool myWrappedTool;
	
	public UndoableTool(Tool newWrappedTool) {
		setWrappedTool(newWrappedTool);
	}
	
    /**
     * Activates the tool for the given view. This method is called
     * whenever the user switches to this tool. Use this method to
     * reinitialize a tool.
     */
    public void activate() {
    	getWrappedTool().activate();
    }

    /**
     * Deactivates the tool. This method is called whenever the user
     * switches to another tool. Use this method to do some clean-up
     * when the tool is switched. Subclassers should always call
     * super.deactivate.
     */
    public void deactivate() {
    	getWrappedTool().deactivate();
    	Undoable undoActivity = getWrappedTool().getUndoActivity();
System.out.println("UndoActivity: " + undoActivity);
    	if ((undoActivity != null) && (undoActivity.isUndoable())) {
	    	view().getUndoManager().pushUndo(undoActivity);
			view().getUndoManager().clearRedos();
	    	// update menus
   			view().editor().figureSelectionChanged(view());
    	}
    }

    /**
     * Handles mouse down events in the drawing view.
     */
    public void mouseDown(MouseEvent e, int x, int y) {
		getWrappedTool().mouseDown(e, x, y);
    }

    /**
     * Handles mouse drag events in the drawing view.
     */
    public void mouseDrag(MouseEvent e, int x, int y) {
    	getWrappedTool().mouseDrag(e, x, y);
    }

    /**
     * Handles mouse up in the drawing view. After the mouse button
     * has been released, the associated tool activity can be undone
     * if the associated tool supports the undo operation from the Undoable interface.
     *
     * @see CH.ifa.draw.util.Undoable
     */
    public void mouseUp(MouseEvent e, int x, int y) {
    	getWrappedTool().mouseUp(e, x, y);
    }

    /**
     * Handles mouse moves (if the mouse button is up).
     */
    public void mouseMove(MouseEvent evt, int x, int y) {
    	getWrappedTool().mouseMove(evt, x, y);
    }

    /**
     * Handles key down events in the drawing view.
     */
    public void keyDown(KeyEvent evt, int key) {
    	getWrappedTool().keyDown(evt, key);
    }

	protected void setWrappedTool(Tool newWrappedTool) {
		myWrappedTool = newWrappedTool;
	}
	
	protected Tool getWrappedTool() {
		return myWrappedTool;
	}

    public DrawingView view() {
    	return getWrappedTool().view();
    }
	
	public Undoable getUndoActivity() {
		return new UndoableAdapter(view());
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		// do nothing: always return default UndoableAdapter
	}
}
