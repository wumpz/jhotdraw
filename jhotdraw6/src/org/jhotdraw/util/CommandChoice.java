/*
 * @(#)CommandChoice.java 5.2
 *
 */

package CH.ifa.draw.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * A Command enabled choice. Selecting a choice executes the
 * corresponding command.
 *
 * @see Command
 */


public  class CommandChoice
        extends JComboBox implements ItemListener {

    private Vector   fCommands;

    public CommandChoice() {
    	super();
        fCommands = new Vector(10);
        addItemListener(this);
    }

    /**
     * Adds a command to the menu.
     */
    public synchronized void addItem(Command command) {
        addItem(command.name());
        fCommands.addElement(command);
    }

    /**
     * Executes the command.
     */
    public void itemStateChanged(ItemEvent e) {
    	if ((getSelectedIndex() >= 0) && (getSelectedIndex() < fCommands.size())) {
    	    Command command = (Command)fCommands.elementAt(getSelectedIndex());
	        command.execute();
        }
    }
}


