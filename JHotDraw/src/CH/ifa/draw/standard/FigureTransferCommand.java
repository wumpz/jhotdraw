/*
 * @(#)FigureTransferCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Common base clase for commands that transfer figures
 * between a drawing and the clipboard.
 */
public abstract class FigureTransferCommand extends AbstractCommand {

   /**
    * Constructs a drawing command.
    * @param name the command name
    * @param view the target view
    */
    protected FigureTransferCommand(String name, DrawingView view) {
        super(name, view);
    }

   /**
    * Deletes the selection from the drawing.
    */
    protected void deleteSelection() {
       view().drawing().removeAll(view().selection());
       view().clearSelection();
    }

   /**
    * Copies the selection to the clipboard.
    */
    protected void copySelection() {
        FigureSelection selection = view().getFigureSelection();
        Clipboard.getClipboard().setContents(selection);
    }

   /**
    * Inserts a vector of figures and translates them by the
    * given offset.
    */
    protected void insertFigures(Vector figures, int dx, int dy) {
        FigureEnumeration e = new FigureEnumerator(figures);
        while (e.hasMoreElements()) {
            Figure figure = e.nextFigure();
            figure.moveBy(dx, dy);
            figure = view().add(figure);
            view().addToSelection(figure);
        }
    }

}


