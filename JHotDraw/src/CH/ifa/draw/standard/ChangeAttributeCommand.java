/*
 * @(#)ChangeAttributeCommand.java 5.2
 *
 */

package CH.ifa.draw.standard;

import java.awt.Color;
import CH.ifa.draw.framework.*;

/**
 * Command to change a named figure attribute.
 */
public  class ChangeAttributeCommand extends AbstractCommand {

    private String      fAttribute;
    private Object      fValue;

   /**
    * Constructs a change attribute command.
    * @param name the command name
    * @param attributeName the name of the attribute to be changed
    * @param value the new attribute value
    * @param view the target view
    */
    public ChangeAttributeCommand(String name, String attributeName,
                           Object value, DrawingView view) {
        super(name, view);
        fAttribute = attributeName;
        fValue = value;
    }

    public void execute() {
        FigureEnumeration k = view().selectionElements();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();
            f.setAttribute(fAttribute, fValue);
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

}


