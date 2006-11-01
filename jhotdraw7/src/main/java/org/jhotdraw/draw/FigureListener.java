/*
 * @(#)FigureListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.draw;

import java.util.EventListener;


/**
 * Listener interested in Figure changes.
 *
 * @version <$CURRENT_VERSION$>
 */
public interface FigureListener extends EventListener {

	/**
	 * Sent when an area is invalid
	 */
	public void figureInvalidated(FigureEvent e);

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureEvent e);

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureEvent e);

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureEvent e);

	/**
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureEvent e);
}
