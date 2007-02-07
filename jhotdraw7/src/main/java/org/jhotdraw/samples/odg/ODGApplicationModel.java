/*
 * @(#)ODGApplicationModel.java  1.0  January 15, 2007
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.odg;

import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;

/**
 * ODGApplicationModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 15, 2007 Created.
 */
public class ODGApplicationModel extends DefaultApplicationModel {
    
    /** Creates a new instance. */
    public ODGApplicationModel() {
setProjectClass(ODGProject.class);
    }
    
    public LinkedList<JToolBar> createToolBars(Application a, Project p) {
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
       // list.add(new TestPalette());
        return list;
    }
}
