/*
 * @(#)NumberTextFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.util.*;
import java.awt.*;
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A TextFigure specialized to edit numbers.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class NumberTextFigure extends TextFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -4056859232918336475L;
	private int numberTextFigureSerializedDataVersion = 1;

	/**
	 * Gets the number of columns to be used by the text overlay.
	 * @see CH.ifa.draw.util.FloatingTextField
	 */
	public int overlayColumns() {
		return Math.max(4, getText().length());
	}

	/**
	 * Gets the numerical value of the contained text.
	 * return the value or 0 in the case of an illegal number format.
	 */
	public int getValue() {
		int value = 0;
		try {
			value = Integer.parseInt(getText());
		}
		catch (NumberFormatException e) {
			value = 0;
		}
		return value;
	}

	/**
	 * Sets the numberical value of the contained text.
	 */
	public void setValue(int value) {
		setText(Integer.toString(value));
	}

}
