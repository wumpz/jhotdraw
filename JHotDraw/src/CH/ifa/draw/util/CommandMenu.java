/*
 * @(#)CommandMenu.java 5.2
 *
 */

package CH.ifa.draw.util;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A Command enabled menu. Selecting a menu item
 * executes the corresponding command.
 *
 * @see Command
 */

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public  class CommandMenu
        extends JMenu implements ActionListener {

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
        JMenuItem m = new JMenuItem(command.name());
        m.addActionListener(this);
        add(m);
        fCommands.addElement(command);
    }

    /**
     * Adds a command with the given short cut to the menu. The item's label is
     * the command's name.
     */
    public synchronized void add(Command command, MenuShortcut shortcut) {
        JMenuItem m = new JMenuItem(command.name(), shortcut.getKey());
        m.setName(command.name());
        m.addActionListener(this);
        add(m);
        fCommands.addElement(command);
    }

    public synchronized void remove(Command command) {
        System.out.println("not implemented");
    }

    public synchronized void remove(MenuItem item) {
        System.out.println("not implemented");
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
            if (getMenuComponent(i) instanceof JSeparator)
                continue;
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
            if (getMenuComponent(i) instanceof JSeparator)
                continue;
            if (source == item) {
                Command cmd = (Command)fCommands.elementAt(j);
                cmd.execute();
                break;
            }
            j++;
        }
    }
}


