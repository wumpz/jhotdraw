/*
 * @(#)DefaultOSXWindowManager.java  1.0  22. März 2007
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

package org.jhotdraw.application;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * DefaultOSXWindowManager.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public class DefaultOSXWindowManager extends AbstractWindowManager {
    
    /** Creates a new instance. */
    public DefaultOSXWindowManager() {
    }
    
    protected void initProjectActions(Project p) {
    }
    
    public void preLaunch() {
    }
    
    public void show(Project p) {
    }
    
    public void hide(Project p) {
    }
    
    public Project getCurrentProject() {
        return null;
    }
    
    public boolean isSharingToolsAmongProjects() {
        return false;
    }
    
    public Component getComponent() {
        return null;
    }
    public  void addPalette(Window w) {
        
    }
    public  void removePalette(Window w) {
    }
}
