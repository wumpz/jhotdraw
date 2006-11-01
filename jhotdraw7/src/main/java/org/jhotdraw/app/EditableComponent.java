/*
 * @(#)EditableComponent.java  2.0  2001-07-18
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */
package org.jhotdraw.app;

/**
 * This interface must be implemented by components
 * which are editable.
 *
 * @author Werner Randelshofer
 * @version 2.0 2001-07-18
 */

public interface EditableComponent {
	/**
	 * Copies the selected region and place its contents into the system clipboard.
	 */
	public void copy();
	/**
	 * Cuts the selected region and place its contents into the system clipboard.
	 */
	public void cut();
	/**
	 * Deletes the component at (or after) the caret position.
	 */
	public void delete();
	/**
	 * Pastes the contents of the system clipboard at the caret position.
	 */
	public void paste();
	/**
	 * Duplicates the selected region.
	 */
	public void duplicate();
	/**
	 * Selects all.
	 */
	public void selectAll();
}