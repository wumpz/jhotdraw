/*
 * @(#)BorderTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

/**
 * BorderTool decorates the clicked figure with a BorderDecorator.
 *
 * @see BorderDecorator
 *
 * @version <$CURRENT_VERSION$>
 */
public  class BorderTool extends ActionTool {

	public BorderTool(DrawingEditor editor) {
		super(editor);
	}

	/**
	 * Add the touched figure to the selection of an invoke action.
	 * Overrides ActionTool's mouseDown to allow for peeling the border
	 * if there is one already.
	 * This is done by CTRLing the click
	 * @see #action
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		// if not CTRLed then proceed normally
		if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0) {
			super.mouseDown(e, x, y);
		}
		else {
			Figure target = drawing().findFigure(x, y);
			if (target != null && target instanceof DecoratorFigure) {
				view().addToSelection(target);
				reverseAction(target);
			}
		}
	}

	/**
	* Decorates the clicked figure with a border.
	*/
	public void action(Figure figure) {
//    	Figure replaceFigure = drawing().replace(figure, new BorderDecorator(figure));
		
		setUndoActivity(createUndoActivity());
		Vector v = new Vector();
		v.addElement(figure);
		v.addElement(new BorderDecorator(figure));
		getUndoActivity().setAffectedFigures(new FigureEnumerator(v));
		((BorderTool.UndoActivity)getUndoActivity()).replaceAffectedFigures();
	}

	/**
	* Peels off the border from the clicked figure.
	*/
	public void reverseAction(Figure figure) {
		setUndoActivity(createUndoActivity());
		Vector v = new Vector();
		v.addElement(figure);

		v.addElement(((DecoratorFigure)figure).peelDecoration());
		getUndoActivity().setAffectedFigures(new FigureEnumerator(v));
		((BorderTool.UndoActivity)getUndoActivity()).replaceAffectedFigures();
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new BorderTool.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			getDrawingView().clearSelection();
			return replaceAffectedFigures();
		}

		public boolean redo() {
			if (!isRedoable()) {
				return false;
			}
			getDrawingView().clearSelection();
			return replaceAffectedFigures();
		}
		
		public boolean replaceAffectedFigures() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasMoreElements()) {
				return false;
			}
			Figure oldFigure = fe.nextFigure();

			if (!fe.hasMoreElements()) {
				return false;
			}
			Figure replaceFigure = fe.nextFigure();
			
			replaceFigure = getDrawingView().drawing().replace(oldFigure, replaceFigure);
			Vector v = new Vector();
			v.addElement(replaceFigure);
			v.addElement(oldFigure);
			setAffectedFigures(new FigureEnumerator(v));			
			
			return true;
		}
	}
}
