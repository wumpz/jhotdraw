/*
 * @(#)MyFigure.java
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

public class MyFigure extends RectangleFigure {

	private static final int BORDER = 6;

	public MyFigure() {
	}

	private void drawBorder(Graphics g) {
		Rectangle r = displayBox();
		g.setColor(getFrameColor());
		g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
	}

	public void draw(Graphics g) {
		super.draw(g);
		drawBorder(g);
	}

	/**
	 */
	public Connector connectorAt(int x, int y) {
		return OffsetConnector.trackConnector(this, x, y);
	}

}
