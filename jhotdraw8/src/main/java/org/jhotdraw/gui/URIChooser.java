/* @(#)URIChooser.java
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.gui;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 *{@code URIChooser} provides a mechanism for the user to choose an URI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface URIChooser {
    // **************************************
    // ***** URIChooser Dialog methods *****
    // **************************************
    /**
     * Pops up an URI chooser dialog. 
     *
     * @param    parent  the parent component of the dialog,
     *			can be {@code null} ;
     *                  see {@code showDialog}  for details
     * @return   the selected URIs or an empty list if no selection has been made.
     */
    public @Nullable URI showDialog(@Nullable Window parent);
    /**
     * Pops up an URI chooser dialog. 
     *
     * @param    node  the parent component of the dialog,
     *			can be {@code null} ;
     *                  see {@code showDialog}  for details
     * @return   the selected URIs or an empty list if no selection has been made.
     */
   default public @Nullable URI showDialog(@Nullable Node node) {
       @Nullable Scene scene = node==null?null:node.getScene();
       return showDialog(scene==null?null:scene.getWindow());
   }
}
