/*
 * @(#)AWTCursor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.standard;

import java.awt.Cursor;

/**
 * Default implementation of the {@link CH.ifa.draw.framework.Cursor} interface
 * for AWT/Swing.
 * 
 * <p>Created on: 08/05/2003.</p>
 * 
 * @version $Revision$
 * @author <a href="mailto:ricardo_padilha@users.sourceforge.net">Ricardo 
 * Sangoi Padilha</a>
 * @see CH.ifa.draw.framework.Cursor
 */
public class AWTCursor extends Cursor implements CH.ifa.draw.framework.Cursor {

	/**
	 * Constructor for <code>AWTCursor</code>.
	 * @param type
	 * @see Cursor#Cursor(int)
	 */
	public AWTCursor(int type) {
		super(type);
	}

	/**
	 * Constructor for <code>AWTCursor</code>.
	 * @param name
	 * @see Cursor#Cursor(java.lang.String)
	 */
	public AWTCursor(String newName) {
		super(newName);
	}

}
