/*
 * @(#)GridPainter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import org.jhotdraw.framework.DrawingView;
import org.jhotdraw.framework.Painter;

/**
* <p>
* This painter draws a grid as background for the drawing view
* </p>
*
* @author Jorge Manrubia Díez
* @version 1.0
*/
public class GridPainter implements Painter {
	private int myHorizontalSeparation;
	private int myVerticalSeparation;
	private Color myColor;

	/**
	 * It constructs the painter using the desired horizontal and vertical
	 * separation
	 *
	 * @param horizontalSeparation
	 * @param verticalSeparation
	 */
	public GridPainter(int horizontalSeparation, int verticalSeparation) {
		super();
		setHorizontalSeparation(horizontalSeparation);
		setVerticalSeparation(verticalSeparation);
		setColor(Color.black);
	}

	/**
	 * It constructs the painter using the desired separation (both horizontal
	 * and vertical)
	 *
	 * @param newSeparation number of pixel which should separate the grid
	 */
	public GridPainter(int newSeparation) {
		this(newSeparation, newSeparation);
	}

	public int getHorizontalSeparation() {
		return myHorizontalSeparation;
	}

	public void setHorizontalSeparation(int newHorizontalSeparation) {
		myHorizontalSeparation = newHorizontalSeparation;
	}

	public int getVerticalSeparation() {
		return myVerticalSeparation;
	}

	public void setVerticalSeparation(int newVerticalSeparation) {
		myVerticalSeparation = newVerticalSeparation;
	}

	/**
	 * @see org.jhotdraw.framework.Painter#draw(java.awt.Graphics, org.jhotdraw.framework.DrawingView)
	 */
	public void draw(Graphics g, DrawingView view) {
		g.setColor(getColor());
		Dimension size = view.getSize();
		int width = size.width;
		int height = size.height;
		for (int x = 0; x < width; x += getHorizontalSeparation()) {
			for (int y = 0; y < height; y += getVerticalSeparation()) {
				g.drawRect(x, y, 0, 0); //Es lo más eficiente para colorea pixeles
			}
		}
	}

	public Color getColor() {
		return myColor;
	}

	public void setColor(Color newColor) {
		myColor = newColor;
	}
}