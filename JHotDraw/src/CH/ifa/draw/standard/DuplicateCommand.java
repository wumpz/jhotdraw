/*
 * @(#)DuplicateCommand.java
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
import java.util.*;

/**
 * Duplicate the selection and select the duplicates.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DuplicateCommand extends FigureTransferCommand {

   /**
    * Constructs a duplicate command.
    * @param name the command name
    * @param view the target view
    */
    public DuplicateCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
    	setUndoActivity(createUndoActivity());
        FigureSelection selection = view().getFigureSelection();

		// create duplicate figure(s)
		FigureEnumeration figures = (FigureEnumeration)selection.getData(StandardFigureSelection.TYPE);
		getUndoActivity().setAffectedFigures(figures);

        view().clearSelection();
        getUndoActivity().setAffectedFigures(
        	insertFigures(getUndoActivity().getAffectedFigures(), 10, 10));
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
