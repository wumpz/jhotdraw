/*
 * @(#)NodeFigure.java 5.1
 *
 */

package CH.ifa.draw.samples.net;

import java.awt.*;
import java.util.*;
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;


public class NodeFigure extends TextFigure {
    private static final int BORDER = 6;
    private Vector      fConnectors;
    private boolean     fConnectorsVisible;

    public NodeFigure() {
        initialize();
        fConnectors = null;
    }

    public Rectangle displayBox() {
        Rectangle box = super.displayBox();
        int d = BORDER;
        box.grow(d, d);
        return box;
    }

    public boolean containsPoint(int x, int y) {
        // add slop for connectors
        if (fConnectorsVisible) {
            Rectangle r = displayBox();
            int d = LocatorConnector.SIZE/2;
            r.grow(d, d);
            return r.contains(x, y);
        }
        return super.containsPoint(x, y);
    }

    private void drawBorder(Graphics g) {
        Rectangle r = displayBox();
        g.setColor(getFrameColor());
        g.drawRect(r.x, r.y, r.width-1, r.height-1);
    }

    public void draw(Graphics g) {
        super.draw(g);
        drawBorder(g);
        drawConnectors(g);
    }

    public Vector handles() {
        ConnectionFigure prototype = new LineConnection();
        Vector handles = new Vector();
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(), prototype));
        handles.addElement(new ConnectionHandle(this, RelativeLocator.west(), prototype));
        handles.addElement(new ConnectionHandle(this, RelativeLocator.south(), prototype));
        handles.addElement(new ConnectionHandle(this, RelativeLocator.north(), prototype));

        handles.addElement(new NullHandle(this, RelativeLocator.southEast()));
        handles.addElement(new NullHandle(this, RelativeLocator.southWest()));
        handles.addElement(new NullHandle(this, RelativeLocator.northEast()));
        handles.addElement(new NullHandle(this, RelativeLocator.northWest()));
        return handles;
    }

    private void drawConnectors(Graphics g) {
        if (fConnectorsVisible) {
            Enumeration e = connectors().elements();
            while (e.hasMoreElements())
                ((Connector) e.nextElement()).draw(g);
        }
    }

    /**
     */
    public void connectorVisibility(boolean isVisible) {
        fConnectorsVisible = isVisible;
        invalidate();
    }

    /**
     */
    public Connector connectorAt(int x, int y) {
        return findConnector(x, y);
    }

    /**
     */
    private Vector connectors() {
        if (fConnectors == null)
            createConnectors();
        return fConnectors;
    }

    private void createConnectors() {
        fConnectors = new Vector(4);
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.north()) );
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.south()) );
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.west()) );
        fConnectors.addElement(new LocatorConnector(this, RelativeLocator.east()) );
    }

    private Connector findConnector(int x, int y) {
        // return closest connector
        long min = Long.MAX_VALUE;
        Connector closest = null;
        Enumeration e = connectors().elements();
        while (e.hasMoreElements()) {
            Connector c = (Connector)e.nextElement();
            Point p2 = Geom.center(c.displayBox());
            long d = Geom.length2(x, y, p2.x, p2.y);
            if (d < min) {
                min = d;
                closest = c;
            }
        }
        return closest;
    }

    private void initialize() {
        setText("node");
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        setFont(fb);
        createConnectors();
    }
}
