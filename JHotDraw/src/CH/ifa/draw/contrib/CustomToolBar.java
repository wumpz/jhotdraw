package CH.ifa.draw.contrib;

import javax.swing.JToolBar;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;

/**
 * This ToolBar allows to use several panels with tools. It manages each
 * panel and enables to swap between them. There can only one panel with
 * tools be activated at a time. Currently, only two panels are supported
 * (standard tools and edit tools).
 *
 * @author  Wolfram Kaiser
 * @version JHotDraw 5.2    31.08.1999
 */
public class CustomToolBar extends JToolBar {

	/**
	 * Vector containing all tools for the standard ToolBar
	 */
    private Vector standardTools;
    
    /**
     * Vector containing all tools for the edit ToolBar
     */
    private Vector editTools;
    
    /**
     * Vector containing all tools, which are currently activated
     */
    private Vector currentTools;
    
    /**
     * Flag which determines whether the tool palette must be updated
     */
    private boolean needsUpdate;
    
    /**
     * Create a new ToolBar
     */
    public CustomToolBar() {
        super();
        standardTools = new Vector();
        editTools = new Vector();
        currentTools = standardTools;
        needsUpdate = false;
    }

	/**
	 * Switch between the two available palettes with tools
	 */
    public void switchToolBar() {
        if (currentTools == standardTools) {
            switchToEditTools();
        }
        else {
            switchToStandardTools();
        }
    }
    
    /**
     * Select the palette with the edit tools
     */
    public void switchToEditTools() {
        if (currentTools != editTools) {
            currentTools = editTools;
            needsUpdate = true;
        }
    }

    /**
     * Select the palette with the standard tools
     */
    public void switchToStandardTools() {
        if (currentTools != standardTools) {
            currentTools = standardTools;
            needsUpdate = true;
        }
    }

    /**
     * Activate a palette of the ToolBar by setting all Tools
     */
    public void activateTools() {
        if (!needsUpdate) {
            return;
        }
        else {
            removeAll();

            JComponent currentTool = null;
            Enumeration enum = currentTools.elements();
            while (enum.hasMoreElements()) {
                currentTool = (JComponent)enum.nextElement();
                super.add(currentTool);
            }
            validate();
            needsUpdate = false;
        }
    }

    /**
     * Add a new tool the the current palette of the ToolBar
     */
    public Component add(Component newTool) {
        if (currentTools == editTools) {
            editTools.addElement(newTool);
        }
        else {
            standardTools.addElement(newTool);
        }
        needsUpdate = true;
        return super.add(newTool);
    }
}