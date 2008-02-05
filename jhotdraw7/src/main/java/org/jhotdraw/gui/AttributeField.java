/*
 * @(#)AttributeField.java  1.0  15. Mai 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
