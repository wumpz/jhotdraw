/*
 * @(#)SplitPaneDrawApplication.java 5.2
 *
 */

package CH.ifa.draw.contrib;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.application.*;

/**
 * A specialised DrawApplication, which offers basic support for a simple
 * splitted pane content.
 *
 * @author  Wolfram Kaiser
 * @version JHotDraw 5.2    31.08.1999
 */
public  class SplitPaneDrawApplication extends DrawApplication {

	private JComponent leftComponent;
	private JComponent rightComponent;

    /**
     * Constructs a drawing window with a default title.
     */
    public SplitPaneDrawApplication() {
        super("JHotDraw");
    }

    /**
     * Constructs a drawing window with the given title.
     */
    public SplitPaneDrawApplication(String title) {
        super(title);
    }

    /**
     * Opens the window and initializes its contents.
     * Clients usually only call but don't override it.
     */
    protected JComponent createContents(DrawingView view) {
		createLeftComponent(view);
		createRightComponent(view);

		if ((getLeftComponent() == null) && (getRightComponent() == null)) {
			return super.createContents(view);
		}
		else if (getLeftComponent() == null) {
			return getRightComponent();
		}
		else if (getRightComponent() == null) {
			return getLeftComponent();
		}
		else {
			return createSplitPane(view);
		}
    }
 
    /**
     * Method which creates the basic split pane. Subclasses may override
     * this method.
     *
     * @param   view    DrawingView for which the JSplitPane should be created
     * @return          the created JSplitPane
     */
 	protected JSplitPane createSplitPane(DrawingView view) {
        JSplitPane dividedContents = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeftComponent(), getRightComponent());
        dividedContents.setAlignmentX(JSplitPane.LEFT_ALIGNMENT);
        dividedContents.setOneTouchExpandable(true);
        return dividedContents;
 	}

    /**
     * Method which creates the left component for the JSplitPane.
     * Subclasses may override this method but should call setLeftComponent()
     * to associate the created component with the JSplitPane.
     */
    protected void createLeftComponent(DrawingView view) {
    	setLeftComponent(new JScrollPane(new JList()));
    }

	/**
	 * Set the left component of the JSplitPane.
	 *
	 * @param	newLeftComponent	left component
	 */
    protected final void setLeftComponent(JComponent newLeftComponent) {
    	leftComponent = newLeftComponent;
    }
    
	/**
	 * Get the left component of the JSplitPane.
	 *
	 * @return	newLeftComponent	left component
	 */
    public JComponent getLeftComponent() {
    	return leftComponent;
    }

    /**
     * Method which creates the right component for the JSplitPane.
     * Subclasses may override this method but should call setLeftComponent()
     * to associate the created component with the JSplitPane.
     */
    protected void createRightComponent(DrawingView view) {
        setRightComponent(super.createContents(view));
    }

	/**
	 * Set the right component of the JSplitPane.
	 *
	 * @param	newLeftComponent	right component
	 */
    protected final void setRightComponent(JComponent newRightComponent) {
    	rightComponent = newRightComponent;
    }

	/**
	 * Get the right component of the JSplitPane.
	 *
	 * @return	newLeftComponent	right component
	 */
    public JComponent getRightComponent() {
    	return rightComponent;
    }
}
