/*
 * @(#)TextHolder.java  1.0  19. November 2003
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
� 
 */



package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import java.awt.*;
import org.jhotdraw.geom.*;
/**
 * TextHolder.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface TextHolder extends Figure {
    public boolean isEditable();
    public Font getFont();
    public Color getTextColor();
    public Color getFillColor();
    public TextHolder getLabelFor();
    /**
     * Gets the number of characters used to expand tabs.
     */
    public int getTabSize();
    public String getText();
    public void setText(String text);
	/**
	 * Gets the number of columns to be overlaid when the figure is edited.
	 */
	public int getTextColumns();
    public void setFontSize(float size);
    public float getFontSize();
    public Insets2DDouble getInsets();
}
