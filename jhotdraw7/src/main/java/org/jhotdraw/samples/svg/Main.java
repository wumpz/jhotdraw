/*
 * @(#)Main.java  1.0  July 8, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.svg;

import org.jhotdraw.app.*;
/**
 * Main.
 *
 * @author Werner Randelshofer.
 * @version 1.0 July 8, 2006 Created.
 */
public class Main {
    
    /** Creates a new instance. */
    public static void main(String[] args) {
        Application app = new DefaultOSXApplication();
        
        
        SVGApplicationModel model = new SVGApplicationModel();
        model.setName("SVG Draw");
        model.setVersion("0.1");
        model.setCopyright("Copyright 2006 (c) Werner Randelshofer.");
        model.setProjectClassName("org.jhotdraw.samples.svg.SVGProject");
        app.setModel(model);
        app.launch(args);
    }
    
}
