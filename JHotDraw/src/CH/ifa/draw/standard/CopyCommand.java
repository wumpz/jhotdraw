/*
 * @(#)CopyCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.util.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Copy the selection to the clipboard.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class CopyCommand extends FigureTransferCommand {

   /**
    * Constructs a copy command.
    * @param name the command name
    * @param view the target view
    */
    public CopyCommand(String name, DrawingView view) {
        super(name, view);
    }

    public void execute() {
        copyFigures(view().selectionElements(), view().selectionCount());
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }
}
