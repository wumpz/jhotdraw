/*
 * @(#)TextHolderFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.*;
import org.jhotdraw.utils.geom.Insets2D;

/**
 * The interface of a {@link Figure} that has some editable text contents.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The text creation tools create new figures by cloning a prototype {@code TextHolderFigure}
 * object.<br>
 * Prototype: {@link TextHolderFigure}; Client: {@link org.jhotdraw.draw.tool.TextCreationTool},
 * {@link org.jhotdraw.draw.tool.TextAreaCreationTool}.
 *
 * <p><em>Prototype</em><br>
 * The text input format creates new text holder figures by cloning a prototype figure object and
 * assigning an image to it, which was read from data input. That's the reason why {@code Figure}
 * extends the {@code Cloneable} interface. <br>
 * Prototype: {@link TextHolderFigure}; Client: {@link org.jhotdraw.draw.io.TextInputFormat}. <hr>
 */
public interface TextHolderFigure extends Figure {

  // FIXME - Maybe we can remove method isEditable(), because we already have
  // an isInteractive() method in the Figure interface.
  /** Returns true if the text of the TextHolderFigure can be edited. */
  public boolean isEditable();

  /** Returns the font to be used by a text editor for editing this Figure. */
  public Font getFont();

  /** Returns the text color to be used by a text editor for editing this Figure. */
  public Color getTextColor();

  /** Returns the fill color to be used by a text editor for editing this Figure. */
  public Color getFillColor();

  // FIMXE - Maybe we can remove method getLabelFor().
  /**
   * Sometimes we want to use a TextHolderFigure as a label for another TextHolderFigure. Returns
   * the TextHolderFigure that should be really used.
   */
  public TextHolderFigure getLabelFor();

  /** Gets the number of characters used to expand tabs. */
  public int getTabSize();

  // FIMXE - Maybe method getText and setText should work with StyledDocument
  //    instead of with Strings.
  /** Returns the text held by the Text Holder. */
  public String getText();

  /**
   * Sets the text of the Text Holder.
   *
   * @param text
   */
  public void setText(String text);

  /** Gets the number of columns to be overlaid when the figure is edited. */
  public int getTextColumns();

  /** Sets the font size of the text held by the TextHolderFigure. */
  public void setFontSize(float size);

  /** Gets the font size of the text held by the TextHolderFigure. */
  public float getFontSize();

  /**
   * Gets the baseline of the first line of text, relative to the upper left corner of the figure
   * bounds.
   */
  public double getBaseline();

  /** Returns Insets to be used by the text editor relative to the handle bounds of the figure. */
  public Insets2D.Double getInsets();

  /** Returns true, if the text does not fit into the bounds of the Figure. */
  public boolean isTextOverflow();
}
