/* @(#)package-info.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 *
 * @author Werner Randelshofer
 * @version $Id$
*/

/**
 * Provides general purpose graphical user interface classes leveraging the
 * javax.swing package.
 * 
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Activity monitoring</em><br>
 * Background activities can be monitored using the {@link org.jhotdraw.gui.ActivityModel} class.
 * A activity model can have an owner. This allows to associate activities to
 * different views of an application.
 * All current activity models can be viewed in the {@link org.jhotdraw.gui.JActivityWindow}.
 * A {@code JActivityIndicator} can be used to indicate that one or more 
 * activity is active. {@code JActivityIndicator} can either indicate all
 * running activities, or only those belonging to a specific owner.
 * </p>
 *
 */
@DefaultAnnotation(Nonnull.class)
package org.jhotdraw.gui;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import javax.annotation.Nonnull;
