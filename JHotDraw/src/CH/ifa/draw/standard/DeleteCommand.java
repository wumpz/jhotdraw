/*
 * @(#)DeleteCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.util.List;

import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.util.CollectionsFactory;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;

/**
 * Command to delete the selection.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DeleteCommand extends FigureTransferCommand {

	/**
	 * Constructs a delete command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public DeleteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	/**
	 * @see CH.ifa.draw.util.Command#execute()
	 */
	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		/* ricardo_padilha: bugfix for correct delete/undelete behavior
		 * When enumerating the affected figures we must not forget the dependent
		 * figures, since they are deleted as well! 
		 */
		FigureEnumeration fe = view().selection();
		List affected = CollectionsFactory.current().createList();
		Figure f;
		FigureEnumeration dfe;
		while (fe.hasNextFigure()) {
			f = fe.nextFigure();
			affected.add(0, f);
			dfe = f.getDependendFigures();
			if (dfe != null) {
				while (dfe.hasNextFigure()) {
					affected.add(0, dfe.nextFigure());
				}
			}
		}
		fe = new FigureEnumerator(affected);
		getUndoActivity().setAffectedFigures(fe);
		/* ricardo_padilha: end of bugfix */
		deleteFigures(getUndoActivity().getAffectedFigures());
		view().checkDamage();
	}

	/**
	 * @see CH.ifa.draw.standard.AbstractCommand#isExecutableWithView()
	 */
	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 * @return Undoable
	 */
	protected Undoable createUndoActivity() {
		return new DeleteCommand.UndoActivity(this);
	}

	public static class UndoActivity extends UndoableAdapter {

		private FigureTransferCommand myCommand;

		/**
		 * Constructor for <code>UndoActivity</code>.
		 * @param newCommand parent command
		 */
		public UndoActivity(FigureTransferCommand newCommand) {
			super(newCommand.view());
			myCommand = newCommand;
			setUndoable(true);
			setRedoable(true);
		}

		/**
		 * @see CH.ifa.draw.util.Undoable#undo()
		 */
		public boolean undo() {
			if (super.undo() && getAffectedFigures().hasNextFigure()) {
				getDrawingView().clearSelection();
				setAffectedFigures(
					myCommand.insertFigures(getAffectedFiguresReversed(), 0, 0));
				return true;
			}
			return false;
		}

		/**
		 * @see CH.ifa.draw.util.Undoable#redo()
		 */
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				myCommand.deleteFigures(getAffectedFigures());
				getDrawingView().clearSelection();
				return true;
			}
			return false;
		}
	}
}
