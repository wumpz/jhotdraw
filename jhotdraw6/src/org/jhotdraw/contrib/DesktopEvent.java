/*
 * @(#)DesktopEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;
import java.util.EventObject;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DesktopEvent extends EventObject {
	DrawingView dv;
	public DesktopEvent(Desktop source, DrawingView dv) {
		super(source);
		this.dv = dv;
	}
	public DrawingView getDrawingView() {
	    return dv;
	}
}