/**
 * JModeller
 *
 * @version 1.0     15.01.2001
 * @author Wolfram Kaiser (©2001)
 */

import java.awt.event.MouseEvent;

import org.jhotdraw.contrib.CustomSelectionTool;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.figures.TextTool;
import org.jhotdraw.framework.DrawingEditor;
import org.jhotdraw.framework.Figure;

/**
 * Delegate mouse selection to a specific TextTool if
 * the figure selected inside a CompositeFigure is a 
 * TextFigure
 */
public class DelegationSelectionTool extends CustomSelectionTool {

    /**
     * TextTool which will be invoked at the top level container.
     */
    private TextTool myTextTool;
    
    public DelegationSelectionTool(DrawingEditor newEditor) {
        super(newEditor);
        setTextTool(new TextTool(newEditor, new TextFigure()));
    }
    
    /**
     * Hook method which can be overriden by subclasses to provide
     * specialised behaviour in the event of a mouse double click.
     */
    protected void handleMouseDoubleClick(MouseEvent e, int x, int y) {
        Figure figure = drawing().findFigureInside(e.getX(), e.getY());
        if ((figure != null) && (figure instanceof TextFigure)) {
            getTextTool().activate();
            getTextTool().mouseDown(e, x, y);
        }
    }

    /**
     * Hook method which can be overriden by subclasses to provide
     * specialised behaviour in the event of a mouse down.
     */
    protected void handleMouseClick(MouseEvent e, int x, int y) {
        deactivate();
    }

    /**
     * Terminates the editing of a text figure.
     */
    public void deactivate() {
        super.deactivate();
        if (getTextTool().isActive()) {
    	    getTextTool().deactivate();
        }
    }

    /**
     * Set the text tool to which double clicks should be delegated. The text tool is shared by
     * all figures upon which this selection tool operates.
     *
     * @param newTextTool delegate text tool
     */
    protected void setTextTool(TextTool newTextTool) {
        myTextTool = newTextTool;
    }

    /**
     * Return the text tool to which double clicks are delegated. The text tool is shared by
     * all figures upon which this selection tool operates.
     *
     * @return delegate text tool
     */    
    protected TextTool getTextTool() {
       return myTextTool;
    }
}