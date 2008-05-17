/*
 * @(#)EditableComponent.java  3.0  2007-04-13
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app;

/**
 * This interface must be implemented by components
 * which are editable.
 * <p>
 * FIXME - Investigate if we can replace this interface by querying the 
 * TransferHandler of a component and retrieve its cut/copy/paste actions.
 * See http://java.sun.com/docs/books/tutorial/uiswing/dnd/intro.html#cut
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-04-13 We don't need to have Cut/Copy/Paste in this 
 * interface, because this functionality is already provided by
 * javax.swing.TransferHandler.
 * <br>2.0 2001-07-18
 */

public interface EditableComponent {
	/**
	 * Deletes the component at (or after) the caret position.
	 */
	public void delete();
	/**
	 * Duplicates the selected region.
	 */
	public void duplicate();
	/**
	 * Selects all.
	 */
	public void selectAll();
	/**
	 * Selects nothing.
	 */
	public void clearSelection();
}