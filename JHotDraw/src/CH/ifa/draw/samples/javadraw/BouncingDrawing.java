/*
 * @(#)BouncingDrawing.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Animatable;


public class BouncingDrawing extends StandardDrawing implements Animatable {
    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -8566272817418441758L;
    private int bouncingDrawingSerializedDataVersion = 1;

    public synchronized Figure add(Figure figure) {
        if (!(figure instanceof AnimationDecorator))
            figure = new AnimationDecorator(figure);
        return super.add(figure);
    }

    public synchronized Figure remove(Figure figure) {
        Figure f = super.remove(figure);
        if (f instanceof AnimationDecorator)
            return ((AnimationDecorator) f).peelDecoration();
        return f;
    }

    public synchronized void replace(Figure figure, Figure replacement) {
        if (!(replacement instanceof AnimationDecorator))
            replacement = new AnimationDecorator(replacement);
        super.replace(figure, replacement);
    }

    public void animationStep() {
        Enumeration k = figures();
        while (k.hasMoreElements())
            ((AnimationDecorator) k.nextElement()).animationStep();
    }
}
