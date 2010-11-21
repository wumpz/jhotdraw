/*
 * @(#)DefaultMenuBuilder.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.app;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import org.jhotdraw.app.action.app.AboutAction;
import org.jhotdraw.app.action.app.AbstractPreferencesAction;
import org.jhotdraw.app.action.app.ExitAction;
import org.jhotdraw.app.action.edit.AbstractFindAction;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.app.action.file.ClearFileAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.NewWindowAction;
import org.jhotdraw.app.action.file.OpenDirectoryAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;

/**
 * {@code DefaultMenuBuilder}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-11-14 Created.
 */
public class DefaultMenuBuilder implements MenuBuilder {

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link AbstractPreferencesAction}</li>
     * </ul>
     */
    @Override
    public void addPreferencesItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(AbstractPreferencesAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link ExitAction}</li>
     * </ul>
     */
    @Override
    public void addExitItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(ExitAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link ClearFileAction}</li>
     * </ul>
     */
    @Override
    public void addClearFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(ClearFileAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link NewWindowAction}</li>
     * </ul>
     */
    @Override
    public void addNewWindowItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(NewWindowAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link NewFileAction}</li>
     * </ul>
     */
    @Override
    public void addNewFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(NewFileAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link LoadFileAction}</li>
     * <li>{@link LoadDirectoryAction}</li>
     * </ul>
     */
    @Override
    public void addLoadFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(LoadFileAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(LoadDirectoryAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link OpenFileAction}</li>
     * <li>{@link OpenDirectoryAction}</li>
     * </ul>
     */
    @Override
    public void addOpenFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(OpenFileAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(OpenDirectoryAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link CloseFileAction}</li>
     * </ul>
     */
    @Override
    public void addCloseFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(CloseFileAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link SaveFileAction}</li>
     * <li>{@link SaveFileAsAction}</li>
     * </ul>
     */
    @Override
    public void addSaveFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(SaveFileAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(SaveFileAsAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link ExportFileAction}</li>
     * </ul>
     */
    @Override
    public void addExportFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(ExportFileAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link PrintFileAction}</li>
     * </ul>
     */
    @Override
    public void addPrintFileItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(PrintFileAction.ID))) {
            m.add(a);
        }
    }

    /** Does nothing. */
    @Override
    public void addOtherFileItems(JMenu m, Application app, @Nullable View v) {
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link UndoAction}</li>
     * <li>{@link RedoAction}</li>
     * </ul>
     */
    @Override
    public void addUndoItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(UndoAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(RedoAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link CutAction}</li>
     * <li>{@link CopyAction}</li>
     * <li>{@link PasteAction}</li>
     * <li>{@link DuplicateAction}</li>
     * <li>{@link DeleteAction}</li>
     * </ul>
     */
    @Override
    public void addClipboardItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(CutAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(CopyAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(PasteAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(DuplicateAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(DeleteAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link SelectAllAction}</li>
     * <li>{@link ClearSelectionAction}</li>
     * </ul>
     */
    @Override
    public void addSelectionItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(SelectAllAction.ID))) {
            m.add(a);
        }
        if (null != (a = am.get(ClearSelectionAction.ID))) {
            m.add(a);
        }
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link AbstractFindAction}</li>
     * </ul>
     */
    @Override
    public void addFindItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(AbstractFindAction.ID))) {
            m.add(a);
        }
    }

    /** Does nothing. */
    @Override
    public void addOtherEditItems(JMenu m, Application app, @Nullable View v) {
    }

    /** Does nothing. */
    @Override
    public void addOtherViewItems(JMenu m, Application app, @Nullable View v) {
    }

    /** Does nothing. */
    @Override
    public void addOtherMenus(List<JMenu> m, Application app, @Nullable View v) {
    }

    /** Does nothing. */
    @Override
    public void addOtherWindowItems(JMenu m, Application app, @Nullable View v) {
    }

    /** Does nothing. */
    @Override
    public void addHelpItems(JMenu m, Application app, @Nullable View v) {
    }

    /** Adds items for the following actions to the menu:
     * <ul>
     * <li>{@link AboutAction}</li>
     * </ul>
     */
    @Override
    public void addAboutItems(JMenu m, Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(AboutAction.ID))) {
            m.add(a);
        }
    }
}
