/*
 * @(#)ExtensionFileFilter.java  1.2  2006-05-19
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

package org.jhotdraw.io;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;
/**
 * Filters files by their extensions.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-05-19 Method append extension added.
 * <br>1.1 2006-04-11 Method getExtensions added.
 * <br>1.0 7. April 2006 Created.
 */
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {
    private String description;
    private HashSet<String> extensions;
    private String defaultExtension;
    
    /**
     * Creates a new instance.
     * @param description A human readable description.
     * @param extension The filename extension. This will be converted to
     * lower-case by this method.
     */
    public ExtensionFileFilter(String description, String extension) {
        this.description = description;
        this.extensions = new HashSet<String>();
        extensions.add(extension.toLowerCase());
        defaultExtension = extension;
    }
    /**
     * Creates a new instance.
     * @param description A human readable description.
     * @param extensions The filename extensions. These will be converted to
     * lower-case by this method.
     */
    public ExtensionFileFilter(String description, String[] extensions) {
        this.description = description;
        this.extensions = new HashSet<String>();
        
        String[] extlc = new String[extensions.length];
        for (int i=0; i < extlc.length; i++) {
            extlc[i] = extensions[i].toLowerCase();
        }
        
        this.extensions.addAll(Arrays.asList(extlc));
        defaultExtension = extensions[0];
    }
    
    /**
     * Returns an unmodifiable set with the filename extensions.
     * All extensions are lower case.
     */
    public Set<String> getExtensions() {
        return Collections.unmodifiableSet(extensions);
    }
    
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        } else {
            String name = pathname.getName();
            int p = name.lastIndexOf('.');
            if (p == -1 || p == name.length() - 1) {
                return extensions.contains("");
            } else {
                return extensions.contains(name.substring(p + 1).toLowerCase());
            }
        }
    }
    
    /**
     * Appends the extension to the filename, in case it is missing.
     */
    public File makeAcceptable(File pathname) {
        if (accept(pathname)) {
            return pathname;
        } else {
            return new File(pathname.getPath()+'.'+defaultExtension);
        }
    }
    
    public String getDescription() {
        return description;
    }
    
}
