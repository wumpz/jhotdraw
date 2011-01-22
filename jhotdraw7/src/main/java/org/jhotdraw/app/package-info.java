/* @(#)package-info.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */

/**
* Defines a framework for document oriented applications and provides default
* implementations.
* <p>
* Supports single document interface (SDI), multiple document interface (MDI),
* the Mac OS X application document interface (OSX), and applets.
* </p>
* <p>
* Key interfaces in this framework: {@link org.jhotdraw.app.Application},
* {@link org.jhotdraw.app.ApplicationModel}, {@link org.jhotdraw.app.View}.
* </p>
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Open last URI on launch</em><br>
 * When the application is started, the last opened URI is opened in a view.<br>
 * The core behavior of this feature is implemented in {@link org.jhotdraw.app.Application#launch}.<br>
 * This feature is enabled if {@link org.jhotdraw.app.ApplicationModel#isOpenLastURIOnLaunch()} returns true.<br>
 * The last opened URI is retrieved from {@link org.jhotdraw.app.Application#getRecentURIs()}.<br>
 * The last opened URI's are supplied by actions that open files.
 * Known data suppliers are {@link org.jhotdraw.app.action.file.OpenFileAction},
 * {@link org.jhotdraw.app.action.app.OpenApplicationFileAction},
 * {@link org.jhotdraw.app.action.file.SaveFileAction},
 * {@link org.jhotdraw.app.action.file.LoadFileAction}.<br>
 * The data suppliers use method {@link org.jhotdraw.app.Application#addRecentURI}.
 * </p>
 *
 * <p><em>Allow multiple views per URI</em><br>
 * Allows opening the same URI in multiple views.
 * When the feature is disabled, opening multiple views is prevented, and saving
 * to a file for which another view is open is prevented.<br>
 * The core behavior of this feature is implemented by actions that open and save files.<br>
 * Known actions are {@link org.jhotdraw.app.action.file.OpenFileAction},
 * {@link org.jhotdraw.app.action.file.OpenRecentFileAction},
 * {@link org.jhotdraw.app.action.file.SaveFileAction},
 * {@link org.jhotdraw.app.action.file.LoadFileAction},
 * {@link org.jhotdraw.app.action.file.LoadRecentFileAction},
 * {@link org.jhotdraw.app.action.file.ExportFileAction},
 * {@link org.jhotdraw.app.action.app.OpenApplicationFileAction}.<br>
 * This feature is enabled if {@link org.jhotdraw.app.ApplicationModel#isAllowMultipleViewsPerURI()} returns true.<br>
 * The list of open URI's can be retrieved via {@link org.jhotdraw.app.Application#getViews}
 * and then calling {@link org.jhotdraw.app.View#getURI}.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
@DefaultAnnotation(NonNull.class)
package org.jhotdraw.app;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
