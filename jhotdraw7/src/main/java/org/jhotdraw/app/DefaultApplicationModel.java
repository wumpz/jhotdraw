/*
 * @(#)DefaultApplicationModel.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
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
import org.jhotdraw.app.action.file.ClearFileAction;
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
 * DefaultApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DefaultApplicationModel
        extends AbstractApplicationModel {

    @Override
    public void initView(Application a, View p) {
    }

    @Override
    public void initApplication(Application a) {
        putAction(NewFileAction.ID, new NewFileAction(a));
        putAction(OpenFileAction.ID, new OpenFileAction(a));
        putAction(SaveFileAction.ID, new SaveFileAction(a));
        putAction(SaveFileAsAction.ID, new SaveFileAsAction(a));
        putAction(CloseFileAction.ID, new CloseFileAction(a));

        putAction(UndoAction.ID, new UndoAction(a));
        putAction(RedoAction.ID, new RedoAction(a));
        putAction(CutAction.ID, new CutAction());
        putAction(CopyAction.ID, new CopyAction());
        putAction(PasteAction.ID, new PasteAction());
        putAction(DeleteAction.ID, new DeleteAction());
        putAction(DuplicateAction.ID, new DuplicateAction());
        putAction(SelectAllAction.ID, new SelectAllAction());
        putAction(ClearSelectionAction.ID, new ClearSelectionAction());
    }

    @Override
    public List<JToolBar> createToolBars(Application app, View p) {
        return Collections.emptyList();
    }

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

    protected JMenu createFileMenu(Application app, View view) {
        return null;
    }

    protected JMenu createEditMenu(Application app, View view) {
        return null;
    }

    protected JMenu createViewMenu(Application app, View view) {
        return null;
    }

    protected JMenu createWindowMenu(Application app, View view) {
        return null;
    }

    protected JMenu createHelpMenu(Application app, View view) {
        return null;
    }
}
