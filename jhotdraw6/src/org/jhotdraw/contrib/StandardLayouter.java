/*
 * @(#)StandardLayouter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.contrib.*;
import CH.ifa.draw.util.*;
import java.awt.*;
import java.io.*;

/**
 * A StandardLayouter contains standard algorithm for 
 * layouting a Layoutable. As a standard behaviour
 * all child components of a Layoutable are laid out
 * underneath each other starting from top to bottom while the
 * x position of all child components stays the same and the width
 * is forced to the width of the maximum width. At the end
 * the presentation figure of the Layoutable is
 * set to the maximum x and y size to encompass all contained
 * child components graphically as well.
 *
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class StandardLayouter implements Layouter {

	/**
	 * The Layoutable which should be laid out.
	 */
	private Layoutable myLayoutable;
	
	/**
	 * Insets to calculate a border
	 */
	private Insets myInsets;
	
	static final long serialVersionUID = 2928651014089117493L;

	/**
	 * Default constructor which is needed for the Storable mechanism.
	 * Usually, the constructor which takes a Layoutable
	 * should be used as each StandardLayouter is associated
	 * with exactly one Layoutable.
	 */
	public StandardLayouter() {
	}

	/**
	 * Constructor which associates a StandardLayouter with
	 * a certain Layoutable.
	 *
	 * @param	newLayoutable	Layoutable to be laid out
	 */	
	public StandardLayouter(Layoutable newLayoutable) {
		setInsets(new Insets(0, 0, 0, 0));
		setLayoutable(newLayoutable);
	}
	
	/**
	 * Get the figure upon which the layout strategy operates.
	 *
	 * @return associated figure which should be laid out
	 */ 
	public Layoutable getLayoutable() {
		return myLayoutable;
	}

	/**
	 * Set the figure upon which the layout strategy operates.
	 *
	 * @param	newLayoutable	Layoutable to be laid out
	 */
	public void setLayoutable(Layoutable newLayoutable) {
		myLayoutable = newLayoutable;
	}

	/*
	 * Calculate the layout for the figure and all its subelements. The
	 * layout is not actually performed but just its dimensions are calculated.
	 * Insets are added for all non-top-level figures.
	 *
	 * @param origin start point for the layout
	 * @param corner minimum corner point for the layout
	 */	
	public Rectangle calculateLayout(Point origin, Point corner) {
		int maxWidth = Math.abs(corner.x - origin.x);
		int maxHeight = 0;
		
		// layout enclosed Layoutable and find maximum width
		FigureEnumeration enum = getLayoutable().figures();
		while (enum.hasMoreElements()) {
			Figure currentFigure = enum.nextFigure();
			Rectangle r = null;
			if (currentFigure instanceof Layoutable) {
				Layouter layoutStrategy = ((Layoutable)currentFigure).getLayouter();
				r = layoutStrategy.calculateLayout(
					new Point(0, 0), new Point(0, 0));
				// add insets to calculated rectangle
				r.grow(layoutStrategy.getInsets().left + layoutStrategy.getInsets().right,
						layoutStrategy.getInsets().top + layoutStrategy.getInsets().bottom);
			}
			else {
				r = new Rectangle(currentFigure.displayBox().getBounds());
			}
			maxWidth = Math.max(maxWidth, r.width);
			maxHeight += r.height;
		}

		return new Rectangle(origin.x, origin.y, maxWidth, maxHeight);
	}

	/**
	 * Method which lays out a figure. It is called by the figure
	 * if a layout task is to be performed. First, the layout dimension for
	 * the figure is calculated and then the figure is arranged newly.
	 * All child component are place beneath another. The figure and all
	 * its children are forced to the minimium width
	 *
	 * @param origin start point for the layout
	 * @param corner minimum corner point for the layout
	 */	
	public Rectangle layout(Point origin, Point corner) {
		// calculate the layout of the figure and its sub-figures first
		Rectangle r = calculateLayout(origin, corner);

		int maxHeight = getInsets().top;
		FigureEnumeration enum = getLayoutable().figures();
		while (enum.hasMoreElements()) {
			Figure currentFigure = enum.nextFigure();

			Point partOrigin = new Point(r.x + getInsets().left, r.y + maxHeight);
			Point partCorner = new Point(r.x + getInsets().left + r.width, r.y + currentFigure.displayBox().height);
			currentFigure.displayBox(partOrigin, partCorner);

			maxHeight += currentFigure.displayBox().height;
		}
		
		// the maximum width has been already calculated
		return new Rectangle(r.x, r.y, r.x + r.width, r.y + maxHeight + getInsets().bottom);
	}

	/**
	 * Reads the contained figures from StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		setLayoutable((Layoutable)dr.readStorable());
	}
	
	/**
	 * Writes the contained figures to the StorableOutput.
	 */
	public void write(StorableOutput dw) {
		dw.writeStorable(getLayoutable());
	}

	/**
	 * Set the insets for spacing between the figure and its subfigures
	 *
	 * @param newInsets new spacing dimensions
	 */
	public void setInsets(Insets newInsets) {
		myInsets = newInsets;
	}

	/**
	 * Get the insets for spacing between the figure and its subfigures
	 *
	 * @return spacing dimensions
	 */	
	public Insets getInsets() {
		return myInsets;
	}
}