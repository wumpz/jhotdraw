/*
 * @(#)AttributeFigure.java 5.1
 *
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * A figure that can keep track of an open ended set of attributes.
 * The attributes are stored in a dictionary implemented by
 * FigureAttributes.
 *
 * @see Figure
 * @see Handle
 * @see FigureAttributes
 */
public abstract class AttributeFigure extends AbstractFigure {

    /**
     * The attributes of a figure. Each figure can have
     * an open ended set of attributes. Attributes are
     * identified by name.
     * @see #getAttribute
     * @see #setAttribute
     */
    private FigureAttributes        fAttributes;

    /**
     * The default attributes associated with a figure.
     * If a figure doesn't have an attribute set, a default
     * value from this shared attribute set is returned.
     * @see #getAttribute
     * @see #setAttribute
     */
    private static FigureAttributes fgDefaultAttributes = null;

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -10857585979273442L;
    private int attributeFigureSerializedDataVersion = 1;

    protected AttributeFigure() { }

    /**
     * Draws the figure in the given graphics. Draw is a template
     * method calling drawBackground followed by drawFrame.
     */
    public void draw(Graphics g) {
        Color fill = getFillColor();
        if (!ColorMap.isTransparent(fill)) {
            g.setColor(fill);
            drawBackground(g);
        }
        Color frame = getFrameColor();
        if (!ColorMap.isTransparent(frame)) {
            g.setColor(frame);
            drawFrame(g);
        }
    }

    /**
     * Draws the background of the figure.
     * @see #draw
     */
    protected void drawBackground(Graphics g) {
    }

    /**
     * Draws the frame of the figure.
     * @see #draw
     */
    protected void drawFrame(Graphics g) {
    }

    /**
     * Gets the fill color of a figure. This is a convenience
     * method.
     @see getAttribute
     */
    public Color getFillColor() {
        return (Color) getAttribute("FillColor");
    }

    /**
     * Gets the frame color of a figure. This is a convenience
     * method.
     @see getAttribute
     */
    public Color getFrameColor() {
        return (Color) getAttribute("FrameColor");
    }

    //---- figure attributes ----------------------------------

    private static void initializeAttributes() {
        fgDefaultAttributes = new FigureAttributes();
        fgDefaultAttributes.set("FrameColor", Color.black);
        fgDefaultAttributes.set("FillColor",  new Color(0x70DB93));
        fgDefaultAttributes.set("TextColor",  Color.black);
        fgDefaultAttributes.set("ArrowMode",  new Integer(0));
        fgDefaultAttributes.set("FontName",  "Helvetica");
        fgDefaultAttributes.set("FontSize",   new Integer(12));
        fgDefaultAttributes.set("FontStyle",  new Integer(Font.PLAIN));
    }

    /**
     * Gets a the default value for a named attribute
     * @see getAttribute
     */
    public static Object getDefaultAttribute(String name) {
        if (fgDefaultAttributes == null)
            initializeAttributes();
        return fgDefaultAttributes.get(name);
    }

    /**
     * Returns the named attribute or null if a
     * a figure doesn't have an attribute.
     * All figures support the attribute names
     * FillColor and FrameColor
     */
    public Object getAttribute(String name) {
        if (fAttributes != null) {
            if (fAttributes.hasDefined(name))
                return fAttributes.get(name);
        }
        return getDefaultAttribute(name);
    }

    /**
     * Sets the named attribute to the new value
     */
    public void setAttribute(String name, Object value) {
        if (fAttributes == null)
            fAttributes = new FigureAttributes();
        fAttributes.set(name, value);
        changed();
    }

    /**
     * Stores the Figure to a StorableOutput.
     */
    public void write(StorableOutput dw) {
        super.write(dw);
        if (fAttributes == null)
            dw.writeString("no_attributes");
        else {
            dw.writeString("attributes");
            fAttributes.write(dw);
        }
    }

    /**
     * Reads the Figure from a StorableInput.
     */
    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        String s = dr.readString();
        if (s.toLowerCase().equals("attributes")) {
            fAttributes = new FigureAttributes();
            fAttributes.read(dr);
        }
    }
}
