/*
 * @(#)UngroupCommand.java 5.2
 *
 */

package CH.ifa.draw.figures;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.AbstractCommand;

/**
 * Command to ungroup the selected figures.
 * @see GroupCommand
 */
public  class UngroupCommand extends AbstractCommand {

   /**
    * Constructs a group command.
    * @param name the command name
    * @param view the target view
    */
    public UngroupCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
        FigureEnumeration selection = view().selectionElements();
        view().clearSelection();

        Vector parts = new Vector();
        while (selection.hasMoreElements()) {
            Figure selected = selection.nextFigure();
            Figure group = view().drawing().orphan(selected);
            FigureEnumeration k = group.decompose();
            while (k.hasMoreElements())
                view().addToSelection(view().add(k.nextFigure()));
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}
