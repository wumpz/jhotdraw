/*
 * @(#)Command.java 5.2
 *
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.DrawingView;

/**
 * Commands encapsulate an action to be executed. Commands have
 * a name and can be used in conjunction with <i>Command enabled</i>
 * ui components.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld010.htm>Command</a></b><br>
 * Command is a simple instance of the command pattern without undo
 * support.
 * <hr>
 *
 * @see CommandButton
 * @see CommandMenu
 * @see CommandChoice
 */
public interface Command {

    /**
     * Executes the command.
     */
    public void execute();

    /**
     * Tests if the command can be executed.
     */
    public boolean isExecutable();

    /**
     * Gets the command name.
     */
    public String name();
    
    public DrawingView view();
}
