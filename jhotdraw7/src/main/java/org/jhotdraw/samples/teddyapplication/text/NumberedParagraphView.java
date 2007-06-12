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

package org.jhotdraw.samples.teddyapplication.text;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.text.*;
/**
 * NumberedParagraphView.
 *
 * @author Werner Randelshofer
 * @version 1.0 October 10, 2005 Created.
 */
public class NumberedParagraphView extends ParagraphView {
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
        int digits = (int) Math.log10(getDocument().
                getDefaultRootElement().getElementCount()) + 2;
        FontMetrics fm = getGraphics().getFontMetrics();
        short numbersWidth = (short) (fm.getStringBounds("0", getGraphics()).getWidth() * digits);
        return (viewFactory.isLineNumbersVisible()) ? (short) (left + numbersWidth) : left;
    }
    
    
    public void paintChild(Graphics g, Rectangle r, int n) {
        super.paintChild(g, r, n);
        if (viewFactory.isLineNumbersVisible()) {
            if (n == 0) {
                g.setColor(Color.gray);
                int lineAscent = g.getFontMetrics().getAscent();
                int lineNumber = getDocument().
                        getDefaultRootElement().
                        getElementIndex(getStartOffset());
                
                int numberX = r.x - getLeftInset();
                int numberY = r.y + lineAscent;
                g.drawString(Integer.toString(lineNumber + 1),
                        numberX, numberY);
            }
        }
    }
}
