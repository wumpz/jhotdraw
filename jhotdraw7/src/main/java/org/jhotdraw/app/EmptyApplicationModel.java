/*
 * @(#)EmptyApplicationModel.java
 *
 * Copyright (c) 2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app;

import java.util.*;
import javax.swing.*;

/**
 * EmptyApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class EmptyApplicationModel
        extends AbstractApplicationModel {

    @Override
    public List<JToolBar> createToolBars(Application app, View p) {
        return Collections.emptyList();
    }

    @Override
    public List<JMenu> createMenus(Application a, View p) {
        return Collections.emptyList();
    }

    @Override
    public void initApplication(Application a) {
    }
}
