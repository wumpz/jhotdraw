/*
 * @(#)BringToFrontCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.framework.*;

/**
 * BringToFrontCommand brings the selected figures in the front of
 * the other figures.
 *
 * @see SendToBackCommand
 */
public class BringToFrontCommand extends AbstractCommand {

   /**
    * Constructs a bring to front command.
    * @param name the command name
    * @param view the target view
    */
    public BringToFrontCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
       FigureEnumeration k = new FigureEnumerator(view().selectionZOrdered());
       while (k.hasMoreElements()) {
            view().drawing().bringToFront(k.nextFigure());
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }
}


