/*
 * @(#)FigureChangeAdapter.java 5.2
 *
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Empty implementation of FigureChangeListener.
 *
 */
public class FigureChangeAdapter implements FigureChangeListener {

    /**
     *  Sent when an area is invalid
     */
    public void figureInvalidated(FigureChangeEvent e) {}

    /**
     * Sent when a figure changed
     */
    public void figureChanged(FigureChangeEvent e) {}

    /**
     * Sent when a figure was removed
     */
    public void figureRemoved(FigureChangeEvent e) {}

    /**
     * Sent when requesting to remove a figure.
     */
    public void figureRequestRemove(FigureChangeEvent e) {}

    /**
     * Sent when an update should happen.
     *
     */
    public void figureRequestUpdate(FigureChangeEvent e) {}

}
