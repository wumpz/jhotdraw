/*
 * @(#)GroupCommand.java 5.2
 *
 */

package CH.ifa.draw.figures;

import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * Command to group the selection into a GroupFigure.
 *
 * @see GroupFigure
 */
public  class GroupCommand extends AbstractCommand {

   /**
    * Constructs a group command.
    * @param name the command name
    * @param view the target view
    */
    public GroupCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
        Vector selected = view().selectionZOrdered();
        Drawing drawing = view().drawing();
        if (selected.size() > 0) {
            view().clearSelection();
            drawing.orphanAll(selected);

            GroupFigure group = new GroupFigure();
            group.addAll(selected);
            view().addToSelection(drawing.add(group));
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}


