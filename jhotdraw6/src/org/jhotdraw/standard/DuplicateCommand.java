/*
 * @(#)DuplicateCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Duplicate the selection and select the duplicates.
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
        FigureSelection selection = view().getFigureSelection();

        view().clearSelection();

        Vector figures = (Vector)selection.getData(StandardFigureSelection.TYPE);
        insertFigures(figures, 10, 10);
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}


