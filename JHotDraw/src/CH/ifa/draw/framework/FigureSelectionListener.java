package CH.ifa.draw.framework;

/*
 * @(#)FigureSelectionListener.java 5.1s7
 *
 */

/**
 * Listener interested in DrawingView selection changes.
 */
 
public interface FigureSelectionListener {
/**
 * Sent when the figure selection has changed.
 * @param view DrawingView
 */
void figureSelectionChanged(DrawingView view);
}
