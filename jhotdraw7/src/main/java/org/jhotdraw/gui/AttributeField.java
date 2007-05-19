/*
 * @(#)AttributeField.java  1.0  15. Mai 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.gui;

import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;

/**
 * Interface for a field which can be used to edit an attribute of the
 * selected {@code Figure}s in a {@code DrawingView}.
 * <p>
 * The {@code AttributeField} can either be global to all
 * {@code DrawingView}s of a {@code DrawingEditor}, or it can be local to a
 * single {@code DrawingView}.
 *
 * @author Werner Randelshofer
 * @version 1.0 15. Mai 2007 Created.
 */
public interface AttributeField {
    /**
     * Returns the JComponent of the attribute field.
     */
    public JComponent getComponent();
    
    /**
     * Updates the field to reflect the attribute value of the currently
     * selected {@code Figure}s. 
     */
    public void updateField(Set<Figure> selectedFigures);
}
