/*
 * @(#)TextHolderFigure.java  1.1  2007-05-19
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */



package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import java.awt.*;
import org.jhotdraw.geom.*;
/**
 * The interface of a figure that has some editable text contents.
 *
 * @author Werner Randelshofer
 * @version 2.1 2007-05-19 Added method isTextOverflow.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface TextHolderFigure extends Figure {
    // FIXME - Maybe we can remove method isEditable(), because we already have
    // an isInteractive() method in the Figure interface.
    /**
     * Returns true if the text of the TextHolderFigure can be edited.
     */
    public boolean isEditable();
    /**
     * Returns the font to be used by a text editor for editing this Figure.
     */
    public Font getFont();
    /**
     * Returns the text color to be used by a text editor for editing this Figure.
     */
    public Color getTextColor();
    /**
     * Returns the fill color to be used by a text editor for editing this Figure.
     */
    public Color getFillColor();
    
    // FIMXE - Maybe we can remove method getLabelFor().
    /**
     * Sometimes we want to use a TextHolderFigure as a label for another
     * TextHolderFigure. Returns the TextHolderFigure that should be really used.
     */
    public TextHolderFigure getLabelFor();
    /**
     * Gets the number of characters used to expand tabs.
     */
    public int getTabSize();
    
    // FIMXE - Maybe method getText and setText should work with StyledDocument
    //    instead of with Strings.
    /**
     * Returns the text held by the Text Holder.
     */
    public String getText();
    public void setText(String text);
    /**
     * Gets the number of columns to be overlaid when the figure is edited.
     */
    public int getTextColumns();
    
    /**
     * Sets the font size of the text held by the TextHolderFigure.
     */
    public void setFontSize(float size);
    /**
     * Gets the font size of the text held by the TextHolderFigure.
     */
    public float getFontSize();
    
    /**
     * Returns Insets to be used by the text editor relative to the handle bounds
     * of the figure.
     */
    public Insets2D.Double getInsets();
    
    /**
     * Returns true, if the text does not fit into the bounds of the Figure.
     */
    public boolean isTextOverflow();
}
