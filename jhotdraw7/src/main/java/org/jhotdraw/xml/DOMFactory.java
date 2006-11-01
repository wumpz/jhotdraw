/*
 * @(#)DOMFactory.java  1.0  February 17, 2004
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

/**
 * DOMFactory.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 17, 2004 Create..
 */
public interface DOMFactory {
    /**
     * Returns the element name for the specified object.
     * Note: The element names "string", "int", "float", "long", "double", 
     * "boolean", "enum" and "null"  are reserved and must not be returned by
     * this operation.
     */
    public String getName(DOMStorable o);
    /**
     * Creates an object from the specified element name.
     */
    public Object create(String name);
    
    /**
     * Returns the element tag name for the specified Enum class.
     */
    public String getEnumName(Enum o);
    /**
     * Returns the enum tag name for the specified Enum instance.
     */
    public String getEnumValue(Enum o);
    
    /**
     * Creates an enum from the specified element name.
     */
    public Enum createEnum(String name, String value);
}