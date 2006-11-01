/*
 * @(#)DOMStorable.java  1.0  February 17, 2004
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

import java.io.*;
/**
 * Interface for objects that can be made persistent using 
 * <code>DOMOutput.writeObject</code> and <code>DOMInput.readObject</code>.
 * <p>
 * By convention every object implementing the DOMStorable interface MUST
 * provide a public parameterless constructor.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 17, 2004 Create.
 */
public interface DOMStorable {
    public void write(DOMOutput out) throws IOException;
    public void read(DOMInput in) throws IOException;
}
