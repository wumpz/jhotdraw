/*
 * @(#)NodeFigure.java 5.2
 *
 */

package org.jhotdraw.samples.offsetConnectors;

import java.awt.*;
import java.util.*;
import java.io.IOException;
import org.jhotdraw.framework.*;
import org.jhotdraw.standard.*;
import org.jhotdraw.figures.*;
import org.jhotdraw.util.*;


public class MyEllipseFigure extends EllipseFigure {
    private static final int BORDER = 6;
    public MyEllipseFigure() {
    }


    private void drawBorder(Graphics g) {
        Rectangle r = displayBox();
        g.setColor(getFrameColor());
        g.drawRect(r.x, r.y, r.width-1, r.height-1);
    }

    public void draw(Graphics g) {
        super.draw(g);
        drawBorder(g);
        //drawConnectors(g);
    }


    /**
     */
    public Connector connectorAt(int x, int y) {
        return OffsetConnector.trackConnector(this, x ,y);
    }

}
