/*
 * @(#)CommandMenu.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.JHotDrawRuntimeException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * A Command enabled menu. Selecting a menu item
 * executes the corresponding command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandMenu extends JMenu implements ActionListener, CommandListener {

	private Vector   fCommands;

	public CommandMenu(String name) {
		super(name);
		fCommands = new Vector(10);
	}

	/**
	 * Adds a command to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void add(Command command) {
		addMenuItem(command, new JMenuItem(command.name()));
	}

	/**
	 * Adds a command with the given short cut to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void add(Command command, MenuShortcut shortcut) {
		addMenuItem(command, new JMenuItem(command.name(), shortcut.getKey()));
	}

	/**
	 * Adds a command with the given short cut to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void addCheckItem(Command command) {
		addMenuItem(command, new JCheckBoxMenuItem(command.name()));
	}

	protected void addMenuItem(Command command, JMenuItem m) {
		m.setName(command.name());
		m.addActionListener(this);
		add(m);
		fCommands.addElement(command);
		command.addCommandListener(this);
//		checkEnabled();
	}
	
	public synchronized void remove(Command command) {
		throw new JHotDrawRuntimeException("not implemented");
	}

	public synchronized void remove(MenuItem item) {
		throw new JHotDrawRuntimeException("not implemented");
	}

	/**
	 * Changes the enabling/disabling state of a named menu item.
	 */
	public synchronized void enable(String name, boolean state) {
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			if (name.equals(item.getLabel())) {
				item.setEnabled(state);
				return;
			}
		}
	}

	public synchronized void checkEnabled() {
		int j = 0;
		for (int i = 0; i < getMenuComponentCount(); i++) {
			// ignore separators
			// a separator has a hyphen as its label
			if (getMenuComponent(i) instanceof JSeparator) {
				continue;
			}
			Command cmd = (Command)fCommands.elementAt(j);
			getMenuComponent(i).setEnabled(cmd.isExecutable());
			j++;
		}
	}

	/**
	 * Executes the command.
	 */
	public void actionPerformed(ActionEvent e) {
		int j = 0;
		Object source = e.getSource();
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			// ignore separators
			// a separator has a hyphen as its label
			if (getMenuComponent(i) instanceof JSeparator) {
				continue;
			}
			if (source == item) {
				Command cmd = (Command)fCommands.elementAt(j);
				cmd.execute();
				break;
			}
			j++;
		}
	}

	public void commandExecuted(EventObject commandEvent) {
//		checkEnabled();
	}
	
	public void commandExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
	
	public void commandNotExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
}


