/*
 * @(#)StyleManager.java  1.1  2007-05-13
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
 *
 * Original code taken from article "Swing and CSS" by Joshua Marinacci 10/14/2003
 * http://today.java.net/pub/a/today/2003/10/14/swingcss.html
 */

package org.jhotdraw.xml.css;

import java.awt.*;
import java.util.*;
import net.n3.nanoxml.*;
import org.jhotdraw.util.ReversedList;
import org.w3c.dom.Element;
/**
 * StyleManager applies styling Rules to an XML DOM.
 * This class supports net.n3.nanoxml as well as org.w3c.dom.
 * 
 * @author Werner Randelshofer
 * @version 1.1 2007-05-13 Process styles in reverse sequence.
 * <br>1.0 6. Juni 2006 Created.
 */
public class StyleManager {
    private java.util.List<CSSRule> rules;
    
    public StyleManager() {
        rules = new ArrayList<CSSRule>();
    }
    
    public void add(CSSRule rule) {
        rules.add(rule);
    }
    
    public void applyStylesTo(Element elem) {
        for (CSSRule rule : rules) {
            if(rule.matches(elem)) {
                rule.apply(elem);
            }
        }
    }
    public void applyStylesTo(IXMLElement elem) {
        for (CSSRule rule : new ReversedList<CSSRule>(rules)) {
            if(rule.matches(elem)) {
                //System.out.println("StyleManager applying "+rule+" to "+elem);
                rule.apply(elem);
            }
        }
    }
}
