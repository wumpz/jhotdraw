/*
 * @(#)Layouter.java 5.2
 *
 */
package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.Figure;
import java.io.Serializable;
import java.awt.*;

/**
 * A Layoutable is a target for a Layouter who lays out the Layoutable
 * according to its layout algorithm
 *
 * @author Wolfram Kaiser
 */
public interface Layoutable extends Figure {

	/**
	 * Layout the figure
	 */
	public void layout();

	/**
	 * Set the Layouter for this Layoutable
	 *
	 * @param newLayouter layouter
	 */
	public void setLayouter(Layouter newLayouter);
	
	/**
	 * Return the Layouter for this Layoutable
	 *
	 * @param layouter
	 */
	public Layouter getLayouter();
}