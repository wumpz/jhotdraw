/*
 * @(#)TextHolder.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;

/**
 * The interface of a figure that has some editable text contents.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */

public interface TextHolder {

	public Rectangle textDisplayBox();

	/**
	 * Gets the text shown by the text figure.
	 */
	public String getText();

	/**
	 * Sets the text shown by the text figure.
	 */
	public void setText(String newText);

	/**
	 * Tests whether the figure accepts typing.
	 */
	public boolean acceptsTyping();

	/**
	 * Gets the number of columns to be overlaid when the figure is edited.
	 */
	public int overlayColumns();

	/**
	 * Connects a text holder to another figure.
	 */
	public void connect(Figure connectedFigure);

	/**
	 * Disconnects a text holder from a connect figure.
	 */
	public void disconnect(Figure disconnectFigure);
	
	/**
	 * Gets the font.
	 */
	public Font getFont();

}
