package CH.ifa.draw.standard;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.FigureSelectionListener;
import CH.ifa.draw.util.Command;

/**
 * Creation date: (18.07.2000 13:23:20)
 * @author: Helge Horch
 */
public abstract class AbstractCommand implements Command, FigureSelectionListener {

    private String  fName;
	
	/** the DrawingView this command applies to */
	private DrawingView fView;

	/**
	 * Constructs a command with the given name that applies to the given view.
	 * @param name java.lang.String
	 */
	public AbstractCommand(String newName, DrawingView newView) {
		setName(newName);
		setView(newView);
		view().addFigureSelectionListener(this);
	}

	/**
	 * @param view jhotdraw.framework.DrawingView
	 */
	public void figureSelectionChanged(DrawingView view) {
	}

	/**
	 * @return view associated with this command
	 */	
	public DrawingView view() {
		return fView;
	}
	
	private void setView(DrawingView newView) {
		fView = newView;
	}

    /**
     * Gets the command name.
     */
    public String name() {
        return fName;
    }
    
    private void setName(String newName)
    {
    	fName = newName;
    }
    
	/**
	 * Insert the method's description here.
	 */
	public void dispose() {
		view().removeFigureSelectionListener(this);
	}

    /**
     * Executes the command.
     */
    public abstract void execute();

    /**
     * Tests if the command can be executed.
     */
    public boolean isExecutable() {
        return true;
    }

}
