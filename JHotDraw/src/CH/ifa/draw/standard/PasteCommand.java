/*
 * @(#)PasteCommand.java
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
import CH.ifa.draw.util.*;
import java.util.Enumeration;
import java.awt.*;

/**
 * Command to insert the clipboard into the drawing.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class PasteCommand extends FigureTransferCommand {

	/**
	 * Constructs a paste command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public PasteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		Point lastClick = view().lastClick();
		FigureSelection selection = (FigureSelection)Clipboard.getClipboard().getContents();
		if (selection != null) {
			setUndoActivity(createUndoActivity());
			getUndoActivity().setAffectedFigures(
				(FigureEnumerator)selection.getData(StandardFigureSelection.TYPE));
			
			if (!getUndoActivity().getAffectedFigures().hasMoreElements()) {
				setUndoActivity(null);
				return;
			}

			Rectangle r = bounds(getUndoActivity().getAffectedFigures());
			view().clearSelection();

			// get an enumeration of inserted figures
			FigureEnumeration fe = insertFigures(getUndoActivity().getAffectedFigures(), lastClick.x-r.x, lastClick.y-r.y);
			getUndoActivity().setAffectedFigures(fe);
			
			view().checkDamage();
		}
	}

	public boolean isExecutableWithView() {
		return Clipboard.getClipboard().getContents() != null;
	}

	Rectangle bounds(Enumeration k) {
		Rectangle r = ((Figure) k.nextElement()).displayBox();
		while (k.hasMoreElements()) {
			r.add(((Figure) k.nextElement()).displayBox());
		}
		return r;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
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

			FigureEnumeration fe = getAffectedFigures();			
			while (fe.hasMoreElements()) {
				getDrawingView().drawing().orphan(fe.nextFigure());
			}
	
			getDrawingView().clearSelection();

			return true;
		}
	
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			getDrawingView().clearSelection();
			setAffectedFigures(getDrawingView().insertFigures(
				getAffectedFigures(), 0, 0, false));

			return true;
		}
	}
}
