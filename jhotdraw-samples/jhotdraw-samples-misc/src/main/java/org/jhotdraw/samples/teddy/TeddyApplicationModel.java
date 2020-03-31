/*
 * @(#)TeddyApplicationModel.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.teddy;

import java.util.*;
import javax.swing.*;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.ApplicationModel;
import org.jhotdraw.api.app.MenuBuilder;
import org.jhotdraw.api.app.View;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.samples.teddy.action.FindAction;
import org.jhotdraw.samples.teddy.action.ToggleLineNumbersAction;
import org.jhotdraw.samples.teddy.action.ToggleLineWrapAction;
import org.jhotdraw.samples.teddy.action.ToggleStatusBarAction;
import org.jhotdraw.util.ActionUtil;

/**
 * Provides meta-data and factory methods for an application.
 * <p>
 * See {@link ApplicationModel} on how this class interacts with an application.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyApplicationModel extends DefaultApplicationModel {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public TeddyApplicationModel() {
    }

    @Override
    public ActionMap createActionMap(Application a, View v) {
        ActionMap m = super.createActionMap(a, v);
        AbstractAction aa;
        m.put(FindAction.ID, new FindAction(a, v));
        m.put(ToggleLineWrapAction.ID, new ToggleLineWrapAction(a, v));
        m.put(ToggleStatusBarAction.ID, new ToggleStatusBarAction(a, v));
        m.put(ToggleLineNumbersAction.ID, new ToggleLineNumbersAction(a, v));
        m.put(PrintFileAction.ID, null);
        return m;
    }

    @Override
    public void initView(Application a, View v) {
    }

    /**
     * Creates the MenuBuilder.
     */
    @Override
    protected MenuBuilder createMenuBuilder() {
        return new DefaultMenuBuilder() {
            @Override
            public void addOtherViewItems(JMenu m, Application app, View v) {
                ActionMap am = app.getActionMap(v);
                JCheckBoxMenuItem cbmi;
                cbmi = new JCheckBoxMenuItem(am.get(ToggleLineWrapAction.ID));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleLineWrapAction.ID));
                m.add(cbmi);
                cbmi = new JCheckBoxMenuItem(am.get(ToggleLineNumbersAction.ID));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleLineNumbersAction.ID));
                m.add(cbmi);
                cbmi = new JCheckBoxMenuItem(am.get(ToggleStatusBarAction.ID));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(ToggleStatusBarAction.ID));
                m.add(cbmi);
            }
        };
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
