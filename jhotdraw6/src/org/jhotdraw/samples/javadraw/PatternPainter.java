/*
 * @(#)PatternPainter.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import CH.ifa.draw.framework.*;

/**
 * PatternDrawer a background that can be added to a
 * drawing.
 * @see DrawingView
 * @see Painter
 */

public  class PatternPainter
        implements Painter {

    private Image   fImage;

    public PatternPainter(Image image) {
        fImage = image;
    }

    public void draw(Graphics g, DrawingView view) {
        drawPattern(g, fImage, view);
    }

    /**
     * Draws a pattern background pattern by replicating an image.
     */
    private void drawPattern(Graphics g, Image image, DrawingView view) {
        int iwidth = image.getWidth(view);
        int iheight = image.getHeight(view);
        Dimension d = view.getSize();
        int x = 0; int y = 0;

        while (y < d.height) {
            while (x < d.width) {
                g.drawImage(image, x, y, view);
                x += iwidth;
            }
            y += iheight;
            x = 0;
        }
    }
}
