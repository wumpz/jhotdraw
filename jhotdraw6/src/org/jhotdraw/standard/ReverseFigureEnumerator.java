/*
 * @(#)ReverseFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.util.ReverseListEnumerator;
import CH.ifa.draw.framework.*;

/**
 * An Enumeration that enumerates a Collection of figures back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public final class ReverseFigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;

	public ReverseFigureEnumerator(List l) {
		myIterator = new ReverseListEnumerator(l);
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}

	/**
	 * Returns the next element casted as a figure of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}
}
