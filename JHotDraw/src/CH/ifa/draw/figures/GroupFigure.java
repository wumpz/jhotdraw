/*
 * @(#)GroupFigure.java 5.1
 *
 */

package CH.ifa.draw.figures;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A Figure that groups a collection of figures.
 */
public  class GroupFigure extends CompositeFigure {

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = 8311226373023297933L;
    private int groupFigureSerializedDataVersion = 1;

   /**
    * GroupFigures cannot be connected
    */
    public boolean canConnect() {
        return false;
    }

   /**
    * Gets the display box. The display box is defined as the union
    * of the contained figures.
    */
    public Rectangle displayBox() {
        FigureEnumeration k = figures();
        Rectangle r = k.nextFigure().displayBox();

        while (k.hasMoreElements())
            r.add(k.nextFigure().displayBox());
        return r;
    }

    public void basicDisplayBox(Point origin, Point corner) {
        // do nothing
        // we could transform all components proportionally
    }

    public FigureEnumeration decompose() {
        return new FigureEnumerator(fFigures);
    }

   /**
    * Gets the handles for the GroupFigure.
    */
    public Vector handles() {
        Vector handles = new Vector();
        handles.addElement(new GroupHandle(this, RelativeLocator.northWest()));
        handles.addElement(new GroupHandle(this, RelativeLocator.northEast()));
        handles.addElement(new GroupHandle(this, RelativeLocator.southWest()));
        handles.addElement(new GroupHandle(this, RelativeLocator.southEast()));
        return handles;
    }

   /**
    * Sets the attribute of all the contained figures.
    */
    public void setAttribute(String name, Object value) {
        super.setAttribute(name, value);
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().setAttribute(name, value);
    }
}
