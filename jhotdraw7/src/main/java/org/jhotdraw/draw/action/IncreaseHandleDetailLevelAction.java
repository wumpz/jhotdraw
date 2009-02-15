/*
 * @(#)SelectSameAction.java  1.1  2006-06-05
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import java.util.*;
import javax.swing.*;
/**
 * SelectSameAction.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-06-05 Optimized performance.
 * <br>1.0 25. November 2003  Created.
 */
public class IncreaseHandleDetailLevelAction extends AbstractSelectedAction {
    public final static String ID = "view.increaseHandleDetailLevel";
    /** Creates a new instance. */
    public IncreaseHandleDetailLevelAction(DrawingEditor editor) {
        super(editor);
        labels.configureAction(this, ID);
        //putValue(AbstractAction.NAME, labels.getString("editSelectSame"));
        //  putValue(AbstractAction.MNEMONIC_KEY, labels.getString("editSelectSameMnem"));
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        increaseHandleDetaiLevel();
    }
    
    public void increaseHandleDetaiLevel() {
      DrawingView view =  getView();
        if (view != null) {
            view.setHandleDetailLevel(view.getHandleDetailLevel() + 1);
        }
    }
}
