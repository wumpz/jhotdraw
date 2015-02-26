/*
 * @(#)PrintableView.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app;

import java.awt.print.*;

/**
 * The interface of a {@link View} which can print its document.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below define together the contracts
 * of a smaller framework inside of the JHotDraw framework for document oriented
 * applications.<br>
 * Contract: {@link PrintableView}.<br>
 * Client: {@link org.jhotdraw.app.action.file.PrintFileAction}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PrintableView extends View {
public Pageable createPageable();   
}
