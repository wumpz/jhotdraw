/*
 * @(#)GroupHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.figures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.Locator;
import org.jhotdraw.draw.NullHandle;

/**
 * A Handle for a GroupFigure.
 *
 * @version <$CURRENT_VERSION$>
 */
public final class GroupHandle extends NullHandle {

	public GroupHandle(Figure owner, Locator locator) {
		super(owner, locator);
	}

	/**
	 * Draws the Group handle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
		r.grow(-1, -1);
		g.setColor(Color.white);
		g.drawRect(r.x, r.y, r.width, r.height);
	}
}
