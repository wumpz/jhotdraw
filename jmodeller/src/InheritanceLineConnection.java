/**
 * JModeller
 *
 * @version 1.0     15.01.2001
 * @author Wolfram Kaiser (©2001)
 */

import java.awt.Color;

import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.LineConnection;
import org.jhotdraw.framework.Figure;

/**
 * An InheritanceLineConnection is a graphical representation for
 * an inheritance relationship (is-a) between two classes (represented
 * by ClassFigures).
 */
public class InheritanceLineConnection extends LineConnection {

    static final long serialVersionUID = 3140686678671889499L;

    /**
     * Create a new instance with a predefined arrow
     */
    public InheritanceLineConnection() {
        setStartDecoration(null);
        ArrowTip arrow = new ArrowTip(0.35, 20.0 , 20.0);
        arrow.setFillColor(Color.white);
        arrow.setBorderColor(Color.black);
        setEndDecoration(arrow);
    }
        
    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a
     * connection between two objects has been established.
     */
    protected void handleConnect(Figure start, Figure end) {
        super.handleConnect(start, end);

        JModellerClass startClass = ((ClassFigure)start).getModellerClass();
        JModellerClass endClass = ((ClassFigure)end).getModellerClass();

        startClass.addSuperclass(endClass);
    }

    /**
     * Hook method to plug in application behaviour into
     * a template method. This method is called when a 
     * connection between two objects has been cancelled.
     */
    protected void handleDisconnect(Figure start, Figure end) {
        super.handleDisconnect(start, end);
        if ((start != null) && (end!= null)) {
            JModellerClass startClass = ((ClassFigure)start).getModellerClass();
            JModellerClass endClass = ((ClassFigure)end).getModellerClass();
            startClass.removeSuperclass(endClass);
        }
    }

    /**
     * Test whether an inheritance relationship between two ClassFigures can
     * be established. An inheritance relationshop can be established if
     * there is no cyclic inheritance graph. This method is called before
     * the two classes are connected in the diagram.
     *
     * @param   start   subclass
     * @param   end     superclass
     * @return  true, if an inheritance relationship can be established, false otherwise
     */
    public boolean canConnect(Figure start, Figure end) {
        JModellerClass startClass = ((ClassFigure)start).getModellerClass();
        JModellerClass endClass = ((ClassFigure)end).getModellerClass();

        return !endClass.hasInheritanceCycle(startClass);
    }
}