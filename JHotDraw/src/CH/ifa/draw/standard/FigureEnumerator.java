/*
 * @(#)FigureEnumerator.java
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
import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * An Enumeration for a Collection of Figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public final class FigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;

	private static FigureEnumerator singletonEmptyEnumerator =
		new FigureEnumerator(CollectionsFactory.current().createList());

	public FigureEnumerator(Collection c) {
		myIterator = c.iterator();
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}

	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}

	public static FigureEnumeration getEmptyEnumeration() {
		return singletonEmptyEnumerator;
	}

/*	public static FigureEnumeration getClonedFigures(FigureEnumeration toDuplicate) {
		List v = CollectionsFactory.current().createList();
		while (toDuplicate.hasMoreElements()) {
			try {
				v.addElement(toDuplicate.nextFigure().clone());
			}
			catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return new FigureEnumerator(v);
	}
*/
}
