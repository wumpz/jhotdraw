/*
 * @(#)ChangeAttributeCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.awt.Color;
import java.util.Hashtable;

/**
 * Command to change a named figure attribute.
 *
 * @version <$CURRENT_VERSION$>
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
    	setUndoActivity(createUndoActivity());
    	getUndoActivity().setAffectedFigures(view().selectionElements());
        FigureEnumeration fe = getUndoActivity().getAffectedFigures();
        while (fe.hasMoreElements()) {
	        fe.nextFigure().setAttribute(fAttribute, fValue);
        }
        view().checkDamage();
    }

    public boolean isExecutable() {
        return view().selectionCount() > 0;
    }

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new ChangeAttributeCommand.UndoActivity(view(), fAttribute, fValue);
	}

	public static class UndoActivity extends UndoableAdapter {
		private Hashtable	myOriginalValues;
	    private String      myUndoAttribute;
	    private Object      myUndoValue;
	
		public UndoActivity(DrawingView newDrawingView, String newUndoAttribute, Object newUndoValue) {
			super(newDrawingView);
			myOriginalValues = new Hashtable();
			setAttributeName(newUndoAttribute);
			setBackupValue(newUndoValue);
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
	
	        FigureEnumeration k = getAffectedFigures();
	        while (k.hasMoreElements()) {
	            Figure f = k.nextFigure();
	            f.setAttribute(getAttributeName(), getOriginalValue(f));
	        }
	        
	        return true;
		}
		
		public boolean redo() {
			if (!isRedoable()) {
				return false;
			}
	
	        FigureEnumeration k = getAffectedFigures();
	        while (k.hasMoreElements()) {
	            Figure f = k.nextFigure();
	            f.setAttribute(getAttributeName(), getBackupValue());
	        }			
			
			return true;
		}
		
		protected void addOriginalValue(Figure affectedFigure, Object newOriginalValue) {
			myOriginalValues.put(affectedFigure, newOriginalValue);
		}
		
		protected Object getOriginalValue(Figure lookupAffectedFigure) {
			return myOriginalValues.get(lookupAffectedFigure);
		}
		
		protected void setAttributeName(String newUndoAttribute) {
			myUndoAttribute = newUndoAttribute;
		}
		
		public String getAttributeName() {
			return myUndoAttribute;
		}

		protected void setBackupValue(Object newUndoValue) {
			myUndoValue = newUndoValue;
		}
		
		public Object getBackupValue() {
			return myUndoValue;
		}
		
		public void release() {
			super.release();
			myOriginalValues = null;
		}
		
		public void setAffectedFigures(FigureEnumeration fe) {
			// first make copy of FigureEnumeration in superclass
			super.setAffectedFigures(fe);
			// then get new FigureEnumeration of copy to save attributes
	        FigureEnumeration copyFe = getAffectedFigures();
	        while (copyFe.hasMoreElements()) {
	            Figure f = copyFe.nextFigure();
            	addOriginalValue(f, f.getAttribute(getAttributeName()));
	        }
		}
	}
}
