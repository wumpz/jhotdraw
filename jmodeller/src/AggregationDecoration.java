/**
 * JModeller
 *
 * @version 1.0     15.01.2001
 * @author Wolfram Kaiser (©2001)
 */

import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;

import org.jhotdraw.figures.AbstractLineDecoration;
import org.jhotdraw.util.StorableInput;
import org.jhotdraw.util.StorableOutput;

/**
 * An AggregationDecoration draws a diamond at the starting ClassFigure
 * of a AssociationLineConnection to indicate that the starting ClassFigure
 * is an aggregration of the end ClassFigure
 */
public class AggregationDecoration extends AbstractLineDecoration {

    /**
     * Size of the diamond (measured as a diagonal line)
     */
    private int     mySize;

    static final long serialVersionUID = 5949131894547260707L;

    /**
     * Create a new instance of AggregationDecoration with a default
     * diamond size of 20
     */
    public AggregationDecoration() {
        this(20);
    }

    /**
     * Create a new instance of AggregationDecoration with a given
     * diamond size
     *
     * @param newSize size of the diamond
     */
    public AggregationDecoration(int newSize) {
        setSize(newSize);
        setFillColor(Color.white);
        setBorderColor(Color.black);
    }
    
   /**
    * Calculates the outline of an arrow tip.
    */
    public Polygon outline(int x1, int y1, int x2, int y2) {
        Polygon shape = new Polygon();

        // calculate direction vector
        double xDir = (double)(x2 - x1);
        double yDir = (double)(y2 - y1);
        
        // calculate direction vector length
        double vLength = Math.sqrt(xDir*xDir + yDir*yDir);
        if (vLength == 0.0) {
            return shape;
        }

        if (xDir == 0.0) {
            xDir = 1.0;
        }

        // normalize direction vector
        xDir = xDir / vLength;
        yDir = yDir / vLength;

        double endX = x1 + xDir * getSize();
        double endY = y1 + yDir * getSize();

        // calculate vector length
        double h = Math.sqrt(getSize());
        
        // calculate orthogonal vector
        double v1y = 1.0;
        double v1x = -(yDir * v1y) / xDir;
        // calculate orthogonal vector length
        double v1Length = Math.sqrt(v1x*v1x + v1y*v1y);
        // normalize orthogonal vector
        v1y = v1y / v1Length;
        v1x = v1x / v1Length;
        double p1y = (endY + y1)/2 + v1y * h;
        double p1x = (endX + x1)/2 + v1x * h;
        double p2y = (endY + y1)/2 - v1y * h;
        double p2x = (endX + x1)/2 - v1x * h;

        shape.addPoint(x1, y1);

        shape.addPoint((int)p1x, (int)p1y);
        shape.addPoint((int)endX, (int)endY);
        shape.addPoint((int)p2x, (int)p2y);
                
        shape.addPoint(x1, y1);
        
        return shape;

    }

    /**
     * Stores the arrow tip to a StorableOutput.
     */
    public void write(StorableOutput dw) {
        super.write(dw);
        dw.writeInt(getSize());
    }
    
    /**
     * Reads the arrow tip from a StorableInput.
     */
    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        setSize(dr.readInt());
    }
    
    /**
     * Set the size of the diamond. The size is measured as diagonal line.
     *
     * @param newSize size of the diamond
     */
    public void setSize(int newSize) {
        mySize = newSize;
    }
    
    /**
     * Return the size of the diamond. The size is measured as diagonal line.
     *
     * @return diamond size
     */
    public int getSize() {
        return mySize;
    }
}