/*
 * @(#)SingleFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import java.util.*;

/**
 * An Enumeration that contains only a single Figures. An instance of this
 * enumeration can be used only once to retrieve the figure as the figure
 * is forgotten after the first retrieval.
 *
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public final class SingleFigureEnumerator implements FigureEnumeration {
	private Figure mySingleFigure;

	public SingleFigureEnumerator(Figure newSingleFigure) {
		mySingleFigure = newSingleFigure;
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasMoreElements() {
		return mySingleFigure != null;
	}

	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception NoSuchElementException If no more elements exist.
	 */
	public Object nextElement() {
		Object returnFigure = mySingleFigure;
		mySingleFigure = null;
		return returnFigure;
	}

	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		Figure returnFigure = (Figure)mySingleFigure;
		mySingleFigure = null;
		return returnFigure;
	}
}
