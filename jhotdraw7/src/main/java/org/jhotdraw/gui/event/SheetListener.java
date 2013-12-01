/*
 * @(#)SheetListener.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui.event;

import java.util.*;

/**
 * SheetListener.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface SheetListener extends EventListener {
    /**
     * This method is invoked, when the user selected an option on the
     * JOptionPane or the JFileChooser pane on the JSheet.
     */
    public void optionSelected(SheetEvent evt);
}
