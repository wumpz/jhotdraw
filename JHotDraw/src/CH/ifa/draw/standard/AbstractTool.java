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
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class AbstractTool implements Tool {

    protected DrawingView  fView;

    /**
     * The position of the initial mouse down.
     */
    protected int     fAnchorX, fAnchorY;

	private Undoable myUndoActivity;
	
    /**
     * Constructs a tool for the given view.
     */
    public AbstractTool(DrawingView itsView) {
        fView = itsView;
    }

    /**
     * Activates the tool for the given view. This method is called
     * whenever the user switches to this tool. Use this method to
     * reinitialize a tool.
     */
    public void activate() {
        view().clearSelection();
    }

    /**
     * Deactivates the tool. This method is called whenever the user
     * switches to another tool. Use this method to do some clean-up
     * when the tool is switched. Subclassers should always call
     * super.deactivate.
     */
    public void deactivate() {
        view().setCursor(Cursor.getDefaultCursor());
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
     * Gets the tool's editor (convenience method).
     */
    public DrawingEditor editor() {
        return view().editor();
    }

    /**
     * Gets the tool's view.
     */
    public DrawingView view() {
        return fView;
    }

	public Undoable getUndoActivity() {
		return myUndoActivity;
	}

	public void setUndoActivity(Undoable newUndoActivity) {
		myUndoActivity = newUndoActivity;
	}
}
