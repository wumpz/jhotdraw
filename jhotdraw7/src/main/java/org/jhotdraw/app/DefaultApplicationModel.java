/*
 * @(#)DefaultApplicationModel.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
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

import org.jhotdraw.app.action.file.SaveFileAsAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.action.edit.ClearSelectionAction;

/**
 * An {@link ApplicationModel} which creates a default set of {@code Action}s
 * and which does not override any of the default menu bars nor create tool bars.
 * <p>
 * The following actions are created by the {@code createActionMap} method of
 * this model:
 * <ul>
 * <li>{@link NewFileAction}</li>
 * <li>{@link OpenFileAction}</li>
 * <li>{@link SaveFileAction}</li>
 * <li>{@link SaveFileAsAction}</li>
 * <li>{@link CloseFileAction}</li>
 *
 * <li>{@link UndoAction}</li>
 * <li>{@link RedoAction}</li>
 * <li>{@link CutAction}</li>
 * <li>{@link CopyAction}</li>
 * <li>{@link PasteAction}</li>
 * <li>{@link DeleteAction}</li>
 * <li>{@link DuplicateAction}</li>
 * <li>{@link SelectAllAction}</li>
 * <li>{@link ClearSelectionAction}</li>
 * </ul>
 *
 * <p>The {@code createMenu...} methods of this model return null, resulting in
 * a set of default menu bars created by the {@link Application} which holds
 * this model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DefaultApplicationModel
        extends AbstractApplicationModel {

    /**
     * Does nothing.
     */
    @Override
    public void initView(Application a, View v) {
    }

    /**
     * Returns an {@code ActionMap} with a default set of actions (See
     * class comments).
     */
    @Override
    public ActionMap createActionMap(Application a, View v) {
        ActionMap m=new ActionMap();
        m.put(NewFileAction.ID, new NewFileAction(a));
        m.put(OpenFileAction.ID, new OpenFileAction(a));
        m.put(SaveFileAction.ID, new SaveFileAction(a,v));
        m.put(SaveFileAsAction.ID, new SaveFileAsAction(a,v));
        m.put(CloseFileAction.ID, new CloseFileAction(a,v));

        m.put(UndoAction.ID, new UndoAction(a,v));
        m.put(RedoAction.ID, new RedoAction(a,v));
        m.put(CutAction.ID, new CutAction());
        m.put(CopyAction.ID, new CopyAction());
        m.put(PasteAction.ID, new PasteAction());
        m.put(DeleteAction.ID, new DeleteAction());
        m.put(DuplicateAction.ID, new DuplicateAction());
        m.put(SelectAllAction.ID, new SelectAllAction());
        m.put(ClearSelectionAction.ID, new ClearSelectionAction());
        return m;
    }

    /** Returns an empty unmodifiable list. */
    @Override
    public List<JToolBar> createToolBars(Application app, View p) {
        return Collections.emptyList();
    }

    /** Returns an empty modifiable list. */
    @Override
    public List<JMenu> createMenus(Application a, View v) {
        LinkedList<JMenu> menus = new LinkedList<JMenu>();
        JMenu m;
        if ((m = createFileMenu(a, v)) != null) {
            menus.add(m);
        }
        if ((m = createEditMenu(a, v)) != null) {
            menus.add(m);
        }
        if ((m = createViewMenu(a, v)) != null) {
            menus.add(m);
        }
        if ((m = createWindowMenu(a, v)) != null) {
            menus.add(m);
        }
        if ((m = createHelpMenu(a, v)) != null) {
            menus.add(m);
        }
        return menus;
    }

    /** Returns null. */
    protected JMenu createFileMenu(Application app, View view) {
        return null;
    }

    /** Returns null. */
    protected JMenu createEditMenu(Application app, View view) {
        return null;
    }

    /** Returns null. */
    protected JMenu createViewMenu(Application app, View view) {
        return null;
    }

    /** Returns null. */
    protected JMenu createWindowMenu(Application app, View view) {
        return null;
    }

    /** Returns null. */
    protected JMenu createHelpMenu(Application app, View view) {
        return null;
    }
}
