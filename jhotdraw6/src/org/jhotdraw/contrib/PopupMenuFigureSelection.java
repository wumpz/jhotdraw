/*
 * @(#)PopupMenuFigureSelection.java 5.2
 *
 */
 
package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.*;

/**
 * An interface which allows a popup menu to interact with its Figure to
 * which it is associated.
 *
 * @author      Wolfram Kaiser
 * @version     JHotDraw 5.2        31.08.1999
 */
public interface PopupMenuFigureSelection {

	/**
	 * Set the figure which was selected when the popup menu was invoked.
	 *
	 * @param   newSelectedFigure   figure which is selected (typically be a SelectionTool)
	 */
	public void setSelectedFigure(Figure newSelectedFigure);

	/**
	 * Get the figure which was selected when the popup menu was invoked.
	 *
	 * @return  figure which is selected (typically be a SelectionTool)
	 */
	public Figure getSelectedFigure();
}
