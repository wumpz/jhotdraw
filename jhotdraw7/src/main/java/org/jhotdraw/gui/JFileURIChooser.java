/*
 * @(#)JFileURIChooser.java
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
package org.jhotdraw.gui;

import java.io.File;
import java.net.URI;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * JFileURIChooser.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class JFileURIChooser extends JFileChooser implements URIChooser {

    @Override
    public void setSelectedURI(URI uri) {
        setSelectedFile(new File(uri));
    }

    @Override
    public URI getSelectedURI() {
        return getSelectedFile() == null ? null : getSelectedFile().toURI();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
