/*
 * @(#)SendToBackCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.framework.*;

/**
 * A command to send the selection to the back of the drawing.
 */
public class SendToBackCommand extends AbstractCommand {

   /**
    * Constructs a send to back command.
    * @param name the command name
    * @param view the target view
    */
    public SendToBackCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
       FigureEnumeration k = new ReverseFigureEnumerator(view().selectionZOrdered());
       while (k.hasMoreElements()) {
            view().drawing().sendToBack(k.nextFigure());
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}


