/*
 * @(#)AbstractApplicationModel.java
 * 
 * Copyright (c) 2009-2010 by the original authors of JHotDraw
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

import org.jhotdraw.beans.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;

/**
 * This abstract class can be extended to implement an {@link ApplicationModel}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractApplicationModel extends AbstractBean
        implements ApplicationModel {

    protected String name;
    protected String version;
    protected String copyright;
    protected Class viewClass;
    protected String viewClassName;
    public final static String NAME_PROPERTY = "name";
    public final static String VERSION_PROPERTY = "version";
    public final static String COPYRIGHT_PROPERTY = "copyright";
    public final static String VIEW_CLASS_NAME_PROPERTY = "viewClassName";
    public final static String VIEW_CLASS_PROPERTY = "viewClass";

    /** Creates a new instance. */
    public AbstractApplicationModel() {
    }

    public void setName(String newValue) {
        String oldValue = name;
        name = newValue;
        firePropertyChange(NAME_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setVersion(String newValue) {
        String oldValue = version;
        version = newValue;
        firePropertyChange(VERSION_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setCopyright(String newValue) {
        String oldValue = copyright;
        copyright = newValue;
        firePropertyChange(COPYRIGHT_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getCopyright() {
        return copyright;
    }

    /**
     * Use this method for best application startup performance.
     */
    public void setViewClassName(String newValue) {
        String oldValue = viewClassName;
        viewClassName = newValue;
        firePropertyChange(VIEW_CLASS_NAME_PROPERTY, oldValue, newValue);
    }

    /**
     * Use this method only, if setViewClassName() does not suit you.
     */
    public void setViewClass(Class newValue) {
        Class oldValue = viewClass;
        viewClass = newValue;
        firePropertyChange(VIEW_CLASS_PROPERTY, oldValue, newValue);
    }

    public Class getViewClass() {
        if (viewClass == null) {
            if (viewClassName != null) {
                try {
                    viewClass = Class.forName(viewClassName);
                } catch (Exception e) {
                    InternalError error = new InternalError("unable to get view class");
                    error.initCause(e);
                    throw error;
                }
            }
        }
        return viewClass;
    }

    @Override
    public View createView() {
        try {
            return (View) getViewClass().newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("unable to create view");
            error.initCause(e);
            throw error;
        }
    }

    /**
     * Creates toolbars for the application.
     */
    @Override
    public abstract List<JToolBar> createToolBars(Application a, View p);

    @Override
    public abstract List<JMenu> createMenus(Application a, View p);

    /** This method is empty. */
    @Override
    public void initView(Application a, View p) {
    }
    /** This method is empty. */
    @Override
    public void destroyView(Application a, View p) {
    }

    /** This method is empty. */
    @Override
    public void initApplication(Application a) {
    }
    /** This method is empty. */
    @Override
    public void destroyApplication(Application a) {
    }

    @Override
    public URIChooser createOpenChooser(Application a, View v) {
        URIChooser c = new JFileURIChooser();

        return c;
    }
    @Override
    public URIChooser createOpenDirectoryChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return c;
    }


    @Override
    public URIChooser createSaveChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        return c;
    }
    /** Returns createOpenChooser. */
    @Override
    public URIChooser createImportChooser(Application a, View v) {
        return createOpenChooser(a,v);
    }

    /** Returns createSaveChooser. */
    @Override
    public URIChooser createExportChooser(Application a, View v) {
        return createSaveChooser(a,v);
    }

  
}
