/*
 * @(#)FigureEnumeration.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.util.*;

/**
 * Interface for Enumerations that access Figures.
 * It provides a method nextFigure, that hides the down casting
 * from client code.
 *
 * @version <$CURRENT_VERSION$>
 */
public interface FigureEnumeration extends Enumeration {
	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure();
}
