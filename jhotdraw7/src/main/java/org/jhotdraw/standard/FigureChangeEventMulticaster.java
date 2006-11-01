/*
 * @(#)FigureChangeEventMulticaster.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.standard;

import org.jhotdraw.draw.FigureEvent;
import org.jhotdraw.draw.FigureListener;
import org.jhotdraw.framework.*;
import java.awt.*;
import java.util.*;

/**
 * Manages a list of FigureChangeListeners to be notified of
 * specific FigureChangeEvents.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeEventMulticaster extends
	AWTEventMulticaster implements FigureListener {

	public FigureChangeEventMulticaster(EventListener newListenerA, EventListener newListenerB) {
		super(newListenerA, newListenerB);
	}

	public void figureInvalidated(FigureEvent e) {
		((FigureListener)a).figureInvalidated(e);
		((FigureListener)b).figureInvalidated(e);
	}

	public void figureRequestRemove(FigureEvent e) {
		((FigureListener)a).figureRequestRemove(e);
		((FigureListener)b).figureRequestRemove(e);
	}

	public void figureRequestUpdate(FigureEvent e) {
		((FigureListener)a).figureRequestUpdate(e);
		((FigureListener)b).figureRequestUpdate(e);
	}

	public void figureChanged(FigureEvent e) {
		((FigureListener)a).figureChanged(e);
		((FigureListener)b).figureChanged(e);
	}

	public void figureRemoved(FigureEvent e) {
		((FigureListener)a).figureRemoved(e);
		((FigureListener)b).figureRemoved(e);
	}

	public static FigureListener add(FigureListener a, FigureListener b) {
		return (FigureListener)addInternal(a, b);
	}


	public static FigureListener remove(FigureListener l, FigureListener oldl) {
		return (FigureListener) removeInternal(l, oldl);
	}

	protected EventListener remove(EventListener oldl)
	{
		if (oldl == a) {
			return b;
		}
		if (oldl == b) {
			return a;
		}
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) {
			return this;
		}
		else {
			return addInternal((FigureListener)a2, (FigureListener)b2);
		}
	}

	protected static EventListener addInternal(FigureListener a, FigureListener b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		return new FigureChangeEventMulticaster(a, b);
	}

	protected static EventListener removeInternal(EventListener l, EventListener oldl) {
		if (l == oldl || l == null) {
			return null;
		}
		else if (l instanceof FigureChangeEventMulticaster) {
			return ((FigureChangeEventMulticaster)l).remove(oldl);
		}
		else {
			return l;		// it's not here
		}
	}

}
