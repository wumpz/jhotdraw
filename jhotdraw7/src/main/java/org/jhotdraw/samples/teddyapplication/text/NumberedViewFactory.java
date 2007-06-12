/*
 * @(#)NumberedViewFactory.java  1.0  October 10, 2005
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
 *
 * Original code (c) Stanislav Lapitsky
 * http://www.developer.com/java/other/article.php/3318421
 */

package org.jhotdraw.samples.teddyapplication.text;

import javax.swing.text.*;
/**
 * NumberedViewFactory.
 *
 * @author Werner Randelshofer
 * @version 1.0 October 10, 2005 Created.
 */
public class NumberedViewFactory implements ViewFactory {
    private boolean isLineNumbersVisible;
    
    public void setLineNumbersVisible(boolean newValue) {
        boolean oldValue = isLineNumbersVisible;
        isLineNumbersVisible = newValue;
    }
    public boolean isLineNumbersVisible() {
        return isLineNumbersVisible;
    }
    
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null)
            if (kind.equals(AbstractDocument.ContentElementName)) {
            return new LabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
            // if (isLineNumbersVisible()) {
            return new NumberedParagraphView(elem, this);
            // } else {
            // return new ParagraphView(elem);
            //}
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
            return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
            return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
            return new IconView(elem);
            }
        // default to text display
        return new LabelView(elem);
    }
}
