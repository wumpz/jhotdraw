/*
 * @(#)NumberedParagraphView.java  1.0  October 10, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.teddy.text;

import java.awt.*;
import javax.swing.text.*;
/**
 * NumberedParagraphView.
 *
 * @author Werner Randelshofer
 * @version 1.0 October 10, 2005 Created.
 */
public class NumberedParagraphView extends ParagraphView {
    public static short NUMBERS_WIDTH=30;
    private static Font numberFont = new Font("Dialog",Font.PLAIN,10);
    private NumberedViewFactory viewFactory;
    public NumberedParagraphView(Element e, NumberedViewFactory viewFactory) {
        super(e);
        this.viewFactory = viewFactory;
    }
    
    
    
    /**
     * Gets the left inset.
     *
     * @return the inset >= 0
     */
    protected short getLeftInset() {
        short left = super.getLeftInset();
        return (viewFactory.isLineNumbersVisible()) ? (short) (left + NUMBERS_WIDTH) : left;
    }
    
    
    public void paintChild(Graphics g, Rectangle r, int n) {
        super.paintChild(g, r, n);
        if (viewFactory.isLineNumbersVisible()) {
            if (n == 0) {
                g.setColor(Color.gray);
                int lineAscent = g.getFontMetrics().getAscent();
                g.setFont(numberFont);
                int numberAscent = g.getFontMetrics().getAscent();
                int lineNumber = getDocument().
                        getDefaultRootElement().
                        getElementIndex(getStartOffset());
                
                int numberX = r.x - getLeftInset();
                //int numberY = r.y + g.getFontMetrics().getAscent();
                int numberY = r.y + lineAscent;
                g.drawString(Integer.toString(lineNumber + 1),
                        numberX, numberY);
            }
        }
    }
}
