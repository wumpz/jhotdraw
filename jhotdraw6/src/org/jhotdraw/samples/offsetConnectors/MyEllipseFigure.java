/*
 * @(#)NodeFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.samples.offsetConnectors;

import java.awt.*;
import org.jhotdraw.framework.*;
import org.jhotdraw.standard.*;
import org.jhotdraw.figures.*;

public class MyEllipseFigure extends EllipseFigure {

	private static final int BORDER = 6;

	public MyEllipseFigure() {
	}

	private void drawBorder(Graphics g) {
		Rectangle r = displayBox();
		g.setColor(getFrameColor());
		g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
	}

	public void draw(Graphics g) {
		super.draw(g);
		drawBorder(g);
		//drawConnectors(g);
	}

	/**
	 */
	public Connector connectorAt(int x, int y) {
		return OffsetConnector.trackConnector(this, x, y);
	}

}
