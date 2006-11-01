/*
 * @(#)TextAreaFigure.java  2.0.1  2006-02-27
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
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import java.io.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A TextAreaFigure contains formatted text.<br>
 * It automatically rearranges the text to fit its allocated display area,
 * breaking the lines at word boundaries whenever possible.<br>
 * The text can contain either LF or CRLF sequences to separate paragraphs,
 * as well as tab characters for table like formatting and alignment.<br>
 * Currently the tabs are distributed at regular intervals as determined by
 * the TabSize property. Tabs align correctly with either fixed
 * or variable fonts.<br>
 * If, when resizing, the vertical size of the display box is not enough to
 * display all the text, TextAreaFigure displays a dashed red line at the
 * bottom of the figure to indicate there is hidden text.<br>
 * TextAreFigure uses all standard attributes for the area Rectangle2D.Double,
 * ie: FillColor, PenColor for the border, FontSize, FontStyle, and FontName,
 * as well as four additional attributes LeftMargin, RightMargin, TopMargin,
 * and TabSize.<br>
 * <p>
 * XXX - TextAreaFigure should not draw a rectangle on its own but rather
 * rely on a decorator. We probably need a DecoratorConnector for this and we
 * need a way to specify the inner bounds of the decorator. We also need a way
 * to center the text of the TextAreaFigure verticaly and horizontaly.
 *
 * @author    Eduardo Francos - InContext (original version),
 *            Werner Randelshofer (this derived version)
 * @version 2.0.1 2006-02-27 Draw UNDERLINE_LOW_ONE_PIXEL instead of UNDERLINE_ON. 
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 5. M�rz 2004  Created.
 */
public class TextAreaFigure extends AttributedFigure implements TextHolder {
    private Rectangle2D.Double bounds = new Rectangle2D.Double();
    private boolean editable = true;
    private final static BasicStroke dashes = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {4f, 4f}, 0f);
    
    // cache of the TextFigure's layout
    transient private TextLayout textLayout;
    
    /** Creates a new instance. */
    public TextAreaFigure() {
        this("Text");
    }
    public TextAreaFigure(String text) {
        setText(text);
    }
    
    /**
     * Gets the text shown by the text figure.
     */
    public String getText() {
        return (String) getAttribute(TEXT);
    }
    
    /**
     * Sets the text shown by the text figure.
     */
    public void setText(String newText) {
        setAttribute(TEXT, newText);
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        bounds.x = Math.min(anchor.x, lead.x);
        bounds.y = Math.min(anchor.y, lead.y);
        bounds.width = Math.max(1, Math.abs(lead.x - anchor.x));
        bounds.height = Math.max(1, Math.abs(lead.y - anchor.y));
        textLayout = null;
    }
    public void basicTransform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        basicSetBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
                );
    }
    
    
    public boolean contains(Point2D.Double p) {
        return bounds.contains(p);
    }
    
    /**
     * Returns the insets used to draw text.
     */
    public Insets2DDouble getInsets() {
        double sw = Math.ceil(STROKE_WIDTH.get(this) / 2);
        Insets2DDouble insets = new Insets2DDouble(4,4,4,4);
        return new Insets2DDouble(insets.top+sw,insets.left+sw,insets.bottom+sw,insets.right+sw);
    }
    
    public int getTabSize() {
        return 8;
    }
    
    protected void drawText(Graphics2D g) {
        if (getText() != null || isEditable()) {
            
            Font font = getFont();
boolean isUnderlined = FONT_UNDERLINED.get(this);
            Insets2DDouble insets = getInsets();
            Rectangle2D.Double textRect = new Rectangle2D.Double(
            bounds.x + insets.left,
            bounds.y + insets.top,
            bounds.width - insets.left - insets.right,
            bounds.height - insets.top - insets.bottom
            );
            float leftMargin = (float) textRect.x;
            float rightMargin = (float) Math.max(leftMargin + 1, textRect.x + textRect.width);
            float verticalPos = (float) textRect.y;
            if (leftMargin < rightMargin) {
                float tabWidth = (float) (getTabSize() * g.getFontMetrics(font).charWidth('m'));
                float[] tabStops = new float[(int) (textRect.width / tabWidth)];
                for (int i=0; i < tabStops.length; i++) {
                    tabStops[i] = (float) (textRect.x + (int) (tabWidth * (i + 1)));
                }
                
                if (getText() != null) {
                    Shape savedClipArea = g.getClip();
                    g.clip(textRect);
                    
                    String[] paragraphs = getText().split("\n");//Strings.split(getText(), '\n');
                    for (int i = 0; i < paragraphs.length; i++) {
                        if (paragraphs[i].length() == 0) paragraphs[i] = " ";
                        AttributedString as = new AttributedString(paragraphs[i]);
                        as.addAttribute(TextAttribute.FONT, font);
                        if (isUnderlined) {
                        as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                        }
                        int tabCount = new StringTokenizer(paragraphs[i], "\t").countTokens() - 1;
                        verticalPos = drawParagraph(g, as.getIterator(), verticalPos, leftMargin, rightMargin, tabStops, tabCount);
                        if (verticalPos > textRect.y + textRect.height) {
                            break;
                        }
                    }
                    g.setClip(savedClipArea);
                }
            }
            
            if (leftMargin >= rightMargin || verticalPos > textRect.y + textRect.height) {
                g.setColor(Color.red);
                g.setStroke(dashes);
                g.draw(new Line2D.Double(textRect.x, textRect.y + textRect.height - 1, textRect.x + textRect.width - 1, textRect.y + textRect.height - 1));
            }
        }
    }
    
    /**
     * Draws a paragraph of text at the specified y location and returns
     * the y position for the next paragraph.
     */
    private float drawParagraph(Graphics2D g, AttributedCharacterIterator styledText, float verticalPos, float leftMargin, float rightMargin, float[] tabStops, int tabCount) {
        
        // assume styledText is an AttributedCharacterIterator, and the number
        // of tabs in styledText is tabCount
        
        int[] tabLocations = new int[tabCount+1];
        
        int i = 0;
        for (char c = styledText.first(); c != styledText.DONE; c = styledText.next()) {
            if (c == '\t') {
                tabLocations[i++] = styledText.getIndex();
            }
        }
        tabLocations[tabCount] = styledText.getEndIndex() - 1;
        
        // Now tabLocations has an entry for every tab's offset in
        // the text.  For convenience, the last entry is tabLocations
        // is the offset of the last character in the text.
        
        LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, getFontRenderContext());
        int currentTab = 0;
        
        while (measurer.getPosition() < styledText.getEndIndex()) {
            
            // Lay out and draw each line.  All segments on a line
            // must be computed before any drawing can occur, since
            // we must know the largest ascent on the line.
            // TextLayouts are computed and stored in a List;
            // their horizontal positions are stored in a parallel
            // List.
            
            // lineContainsText is true after first segment is drawn
            boolean lineContainsText = false;
            boolean lineComplete = false;
            float maxAscent = 0, maxDescent = 0;
            float horizontalPos = leftMargin;
            LinkedList<TextLayout> layouts = new LinkedList<TextLayout>();
            LinkedList<Float> penPositions = new LinkedList<Float>();
            
            while (!lineComplete) {
                float wrappingWidth = rightMargin - horizontalPos;
                TextLayout layout = null;
                layout =
                measurer.nextLayout(wrappingWidth,
                tabLocations[currentTab]+1,
                lineContainsText);
                
                // layout can be null if lineContainsText is true
                if (layout != null) {
                    layouts.add(layout);
                    penPositions.add(horizontalPos);
                    horizontalPos += layout.getAdvance();
                    maxAscent = Math.max(maxAscent, layout.getAscent());
                    maxDescent = Math.max(maxDescent,
                    layout.getDescent() + layout.getLeading());
                } else {
                    lineComplete = true;
                }
                
                lineContainsText = true;
                
                if (measurer.getPosition() == tabLocations[currentTab]+1) {
                    currentTab++;
                }
                
                if (measurer.getPosition() == styledText.getEndIndex())
                    lineComplete = true;
                else if (tabStops.length == 0 || horizontalPos >= tabStops[tabStops.length-1])
                    lineComplete = true;
                
                if (!lineComplete) {
                    // move to next tab stop
                    int j;
                    for (j=0; horizontalPos >= tabStops[j]; j++) {}
                    horizontalPos = tabStops[j];
                }
            }
            
            verticalPos += maxAscent;
            
            Iterator<TextLayout> layoutEnum = layouts.iterator();
            Iterator<Float> positionEnum = penPositions.iterator();
            
            // now iterate through layouts and draw them
            while (layoutEnum.hasNext()) {
                TextLayout nextLayout = layoutEnum.next();
                float nextPosition = positionEnum.next();
                nextLayout.draw(g, nextPosition, verticalPos);
            }
            
            verticalPos += maxDescent;
        }
        
        return verticalPos;
    }
    
    
    protected void drawFill(Graphics2D g) {
        g.fill(bounds);
    }
    
    protected void drawStroke(Graphics2D g) {
        g.draw(bounds);
    }
    
    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) bounds.getBounds2D();
    }
    
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
        if (detailLevel == 0) {
        handles.add(new FontSizeHandle(this));
        }
        return handles;
    }
    
    protected void validate() {
        super.validate();
        textLayout = null;
    }
    
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean b) {
        this.editable = b;
    }
    
    public int getTextColumns() {
        return (getText() == null) ? 4 : Math.max(getText().length(), 4);
    }
    
    /**
     * Returns a specialized tool for the given coordinate.
     * <p>Returns null, if no specialized tool is available.
     */
    public Tool getTool(Point2D.Double p) {
        return (isEditable() && contains(p)) ? new TextAreaTool(this) : null;
    }
    
    
    
    protected void readBounds(DOMInput in) throws IOException {
        bounds.x = in.getAttribute("x",0d);
        bounds.y = in.getAttribute("y",0d);
        bounds.width = in.getAttribute("w",0d);
        bounds.height = in.getAttribute("h",0d);
    }
    protected void writeBounds(DOMOutput out) throws IOException {
        out.addAttribute("x",bounds.x);
        out.addAttribute("y",bounds.y);
        out.addAttribute("w",bounds.width);
        out.addAttribute("h",bounds.height);
    }
    public void read(DOMInput in) throws IOException {
        readBounds(in);
        readAttributes(in);
        textLayout = null;
    }
    
    public void write(DOMOutput out) throws IOException {
        writeBounds(out);
        writeAttributes(out);
    }
    
    public TextAreaFigure clone() {
        TextAreaFigure that = (TextAreaFigure) super.clone();
        that.bounds = (Rectangle2D.Double) this.bounds.clone();
        return that;
    }
    
    public TextHolder getLabelFor() {
        return this;
    }
    
    public void restoreTo(Object geometry) {
        Rectangle2D.Double r = (Rectangle2D.Double) geometry;
        bounds.x = r.x;
        bounds.y = r.y;
        bounds.width = r.width;
        bounds.height = r.height;
    }

    public Object getRestoreData() {
        return bounds.clone();
    }
    public Font getFont() {
        return AttributeKeys.getFont(this);
    }

    public Color getTextColor() {
        return TEXT_COLOR.get(this);
    }

    public Color getFillColor() {
        return FILL_COLOR.get(this);
    }

    public void setFontSize(float size) {
        FONT_SIZE.set(this, new Double(size));
    }

    public float getFontSize() {
       return FONT_SIZE.get(this).floatValue();
    }
}
