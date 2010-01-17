/*
 * @(#)TeddyApplicationModel.java
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.teddy;

import org.jhotdraw.app.action.file.PrintFileAction;
import javax.swing.*;
import org.jhotdraw.app.*;
import java.util.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.samples.teddy.action.*;
import org.jhotdraw.util.*;

/**
 * TeddyApplicationModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyApplicationModel extends DefaultApplicationModel {
    
    /** Creates a new instance. */
    public TeddyApplicationModel() {
    }
    
    @Override
    public ActionMap createActionMap(Application a, View v) {
        ActionMap m = super.createActionMap(a, v);
        AbstractAction aa;

        m.put(FindAction.ID, new FindAction(a,v));
        m.put(ToggleLineWrapAction.ID, new ToggleLineWrapAction(a,v));
        m.put(ToggleStatusBarAction.ID, new ToggleStatusBarAction(a,v));
        m.put(ToggleLineNumbersAction.ID, new ToggleLineNumbersAction(a,v));
        m.put(PrintFileAction.ID, null);

        return m;
    }
    
    @Override public void initView(Application a, View v) {
    }
    
    @Override public List<JMenu> createMenus(Application a, View v) {
        LinkedList<JMenu> mb = new LinkedList<JMenu>();
        
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.teddy.Labels");
        
        JMenu m;
        JCheckBoxMenuItem cbmi;
        ActionMap am = a.getActionMap(v);

        m = new JMenu();
        labels.configureMenu(m, "view");
        cbmi = new JCheckBoxMenuItem(am.get(ToggleLineWrapAction.ID));
        ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleLineWrapAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(am.get(ToggleLineNumbersAction.ID));
        ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleLineNumbersAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(am.get(ToggleStatusBarAction.ID));
        ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleStatusBarAction.ID));
        m.add(cbmi);
        mb.add(m);
        
        return mb;
    }
    
    /**
     * Creates toolbars for the application.
     * This class returns an empty list - we don't want toolbars in a text editor.
     */
    @Override
    public List<JToolBar> createToolBars(Application app, View p) {
        return Collections.emptyList();
    }

    @Override
    public JFileURIChooser createOpenChooser(Application app, View p) {
        JFileURIChooser chooser = new JFileURIChooser();
        chooser.setAccessory(new CharacterSetAccessory());
        return chooser;
    }
    @Override
    public JFileURIChooser createSaveChooser(Application app, View p) {
        JFileURIChooser chooser = new JFileURIChooser();
        chooser.setAccessory(new CharacterSetAccessory());
        return chooser;
    }
}
