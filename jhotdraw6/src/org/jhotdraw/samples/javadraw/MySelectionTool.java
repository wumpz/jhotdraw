/*
 * @(#)MySelectionTool.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import java.awt.event.MouseEvent;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A SelectionTool that interprets double clicks to inspect the clicked figure
 */

public  class MySelectionTool extends SelectionTool {

    public MySelectionTool(DrawingView view) {
        super(view);
    }

    /**
     * Handles mouse down events and starts the corresponding tracker.
     */
    public void mouseDown(MouseEvent e, int x, int y) {
        if (e.getClickCount() == 2) {
            Figure figure = drawing().findFigure(e.getX(), e.getY());
            if (figure != null) {
                inspectFigure(figure);
                return;
            }
        }
        super.mouseDown(e, x, y);
    }

    protected void inspectFigure(Figure f) {
        System.out.println("inspect figure"+f);
    }

}
