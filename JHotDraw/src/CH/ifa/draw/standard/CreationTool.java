/*
 * @(#)CreationTool.java
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
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * A tool to create new figures. The figure to be
 * created is specified by a prototype.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld029.htm>Prototype</a></b><br>
 * CreationTool creates new figures by cloning a prototype.
 * <hr>
 *
 * @see Figure
 * @see Object#clone
 *
 * @version <$CURRENT_VERSION$>
 */


public class CreationTool extends AbstractTool {

	/**
	 * the anchor point of the interaction
	 */
	private Point   fAnchorPoint;

	/**
	 * the currently created figure
	 */
	private Figure  fCreatedFigure;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	/**
	 * the prototypical figure that is used to create new figures.
	 */
	private Figure  fPrototype;


	/**
	 * Initializes a CreationTool with the given prototype.
	 */
	public CreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor);
		fPrototype = prototype;
	}

	/**
	 * Constructs a CreationTool without a prototype.
	 * This is for subclassers overriding createFigure.
	 */
	protected CreationTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
		fPrototype = null;
	}

	/**
	 * Sets the cross hair cursor.
	 */
	public void activate() {
		super.activate();
		if (isUsable()) {
			view().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	/**
	 * Creates a new figure by cloning the prototype.
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		fAnchorPoint = new Point(x,y);
		fCreatedFigure = createFigure();
		setAddedFigure((view().add(getCreatedFigure())));
		getAddedFigure().displayBox(fAnchorPoint, fAnchorPoint);
	}

	/**
	 * Creates a new figure by cloning the prototype.
	 */
	protected Figure createFigure() {
		if (fPrototype == null) {
			throw new JHotDrawRuntimeException("No protoype defined");
		}
		return (Figure) fPrototype.clone();
	}

	/**
	 * Adjusts the extent of the created figure
	 */
	public void mouseDrag(MouseEvent e, int x, int y) {
		getAddedFigure().displayBox(fAnchorPoint, new Point(x,y));
	}

	/**
	 * Checks if the created figure is empty. If it is, the figure
	 * is removed from the drawing.
	 * @see Figure#isEmpty
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
		if (getCreatedFigure().isEmpty()) {
			drawing().remove(getAddedFigure());
			// nothing to undo
			setUndoActivity(null);
		}
		else {
			// use undo activity from paste command...
			setUndoActivity(createUndoActivity());

			// put created figure into a figure enumeration
			getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));
		}
		fCreatedFigure = null;
		setAddedFigure(null);
		editor().toolDone();
	}

	/**
	 * Gets the currently created figure
	 */
	protected Figure getCreatedFigure() {
		return fCreatedFigure;
	}

	private void setCreatedFigure(Figure newCreatedFigure) {
		fCreatedFigure = newCreatedFigure;
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	private void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}

	/**
	 * The anchor point is usually the first mouse click performed with this tool.
	 *
	 * @return the anchor point for the interaction
	 * @see #mouseDown
	 */
	protected Point getAnchorPoint() {
		// SF bug-report id: #490752
		return fAnchorPoint;
	}
}
