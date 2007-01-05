/*
 * @(#)DOMOutput.java  1.2  2006-08-26
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


package org.jhotdraw.xml;

import java.io.IOException;

/**
 * DOMOutput.
 *
 * @author  Werner Randelshofer
 * @version 1.2 2006-08-26 Added method setDoctype.
 * <br>1.1. 2006-06-18 Renamed addElement to openElement. 
 * <br>1.0 10. Maerz 2004  Created.
 */
public interface DOMOutput {
    /**
     * Sets the doctype for the XML document.
     */
    public void setDoctype(String doctype);
    
    /**
     * Adds a new element to the DOM Document and opens it.
     * The new element is added as a child to the current element in the DOM
     * document. Then it becomes the current element.
     * The element must be closed using closeElement.
     */
    public void openElement(String tagName);
    /**
     * Closes the current element of the DOM Document.
     * The parent of the current element becomes the current element.
     * @exception IllegalArgumentException if the provided tagName does
     * not match the tag name of the element.
     */
    public void closeElement();
    /**
     * Adds a comment to the current element of the DOM Document.
     */
    public void addComment(String comment);
    /**
     * Adds a text to current element of the DOM Document.
     * Note: Multiple consecutives texts will be merged.
     */
    public void addText(String text);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void addAttribute(String name, String value);
    /**
     * Adds an attribute to current element of the DOM Document if it is
     * different from the default value.
     */
    public void addAttribute(String name, String value, String defaultValue);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void addAttribute(String name, int value);
    /**
     * Adds an attribute to current element of the DOM Document if it is
     * different from the default value.
     */
    public void addAttribute(String name, int value, int defaultValue);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void addAttribute(String name, boolean value);
    /**
     * Adds an attribute to current element of the DOM Document if it is
     * different from the default value.
     */
    public void addAttribute(String name, boolean value, boolean defaultValue);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void addAttribute(String name, float value);
    /**
     * Adds an attribute to current element of the DOM Document if it is
     * different from the default value.
     */
    public void addAttribute(String name, float value, float defaultValue);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void addAttribute(String name, double value);
    /**
     * Adds an attribute to current element of the DOM Document if it is
     * different from the default value.
     */
    public void addAttribute(String name, double value, double defaultValue);
    /**
     * Writes an object.
     */
    public void writeObject(Object o) throws IOException;
    
    /**
     * Returns a prototype for the object currently being written.
     * This can be used, to reduce the amount of data written to DOMOutput.
     * For example, by not writing object attributes, which have the same values
     * as the prototype.
     */
    public Object getPrototype();
}
