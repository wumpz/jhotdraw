/**
 * JModeller
 *
 * @version 1.0     15.01.2001
 * @author Wolfram Kaiser (©2001)
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.LineConnection;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.FigureChangeEvent;

/**
 * An AssociationLineConnection represents an association relationship (has-a)
 * between two classes (represented by their ClassFigures). An association
 * can either be bi-directional or uni-directional. An association can
 * be turned into an aggregation which can be regard a special kind of association.
 */
public class AssociationLineConnection extends LineConnection {

    /**
     * PopupMenu for an associations which allows to switch between
     * directed and not directed associations and associations and
     * aggregations
     */
    private transient JPopupMenu myPopupMenu;

    static final long serialVersionUID = 6492295462615980490L;
    
    /*
     * Create a new un-directed AssociationLineConnection
     */
    public AssociationLineConnection() {
        super();

        setStartDecoration(null);
        setEndDecoration(null);
    
        setAttribute(FigureAttributeConstant.POPUP_MENU, createPopupMenu());
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
        startClass.addAssociation(endClass);
        endClass.addAssociation(startClass);
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
            startClass.removeAssociation(endClass);
            endClass.removeAssociation(startClass);
        }
    }

    /**
     * Sets the named attribute to the new value.
     * Intercept to enable popup menus.
     */
    public void setAttribute(FigureAttributeConstant constant, Object value) {
        if ((constant != null) && (constant.equals(FigureAttributeConstant.POPUP_MENU))) {
            myPopupMenu = (JPopupMenu)value;
        }
        else {
            super.setAttribute(constant, value);
        }
    }

    /**
     * Return the named attribute or null if a
     * a figure doesn't have an attribute.
     * All figures support the attribute names
     * FillColor and FrameColor
     */
    public Object getAttribute(FigureAttributeConstant constant) {
        if ((constant != null) && (constant.equals(FigureAttributeConstant.POPUP_MENU))) {
            return myPopupMenu;
        }
        else {
            return super.getAttribute(constant);
        }
    }

    /**
     * Factory method to create the associated popup menu.
     * It allows switching between associations and aggregation
     * and directed and not-directed associations depending
     * on the current kind of association. For uni-directional
     * associations the reference from the target class to
     * the start class is removed, while for bi-directional
     * associations, this relation is established again.
     *
     * @return newly created popup menu
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("aggregation") {
                public void actionPerformed(ActionEvent event) {
                    setAggregation(!isAggregation());
                    if (isAggregation()) {
                        ((JMenuItem)event.getSource()).setText("no aggregation");
                    }
                    else {
                        ((JMenuItem)event.getSource()).setText("aggregation");
                    }
                }
            });
        popupMenu.add(new AbstractAction("uni-directional") {
                public void actionPerformed(ActionEvent event) {
                    setUniDirectional(!isUniDirectional());
                    if (isUniDirectional()) {
                        ((JMenuItem)event.getSource()).setText("bi-directional");
                        JModellerClass startClass = ((ClassFigure)startFigure()).getModellerClass();
                        JModellerClass endClass = ((ClassFigure)endFigure()).getModellerClass();
                        endClass.addAssociation(startClass);
                    }
                    else {
                        ((JMenuItem)event.getSource()).setText("uni-directional");
                        JModellerClass startClass = ((ClassFigure)startFigure()).getModellerClass();
                        JModellerClass endClass = ((ClassFigure)endFigure()).getModellerClass();
                        endClass.removeAssociation(startClass);
                    }
                }
            });
            
        popupMenu.setLightWeightPopupEnabled(true);
        return popupMenu;
    }

    /**
     * Turn an association into an aggregation or vice versa.
     * Whether an association is an aggregation is determined
     * by an internal flag that can be set with this method.
     *
     * @param isAggregation true to turn an association into an aggregation, false for the opposite effect
     */
    protected void setAggregation(boolean isAggregation) {
        willChange();
        if (isAggregation) {
            setStartDecoration(new AggregationDecoration());
        }
        else {
            setStartDecoration(null);
        }
        change();
        changed();
    }

    /**
     * Test whether an association is an aggregation or not
     *
     * @return true if the association is an aggregation, false otherwise
     */
    protected boolean isAggregation() {
        return getStartDecoration() != null;
    }

    /**
     * Make an association directed or not directed.
     *
     * @param isDirected true for a directed association, false otherwise
     */
    protected void setUniDirectional(boolean isDirected) {
        willChange();
        if (isDirected) {
            ArrowTip arrow = new ArrowTip(0.4, 12.0, 0.0);
            arrow.setBorderColor(Color.black);
            setEndDecoration(arrow);
        }
        else {
            setEndDecoration(null);
        }
        change();
        changed();
    }

    /**
     * Test whether an associations is directed or not
     *
     * @return true, if the association is directed, false otherwise
     */    
    protected boolean isUniDirectional() {
        return getEndDecoration() != null;
    }

    /**
     * Notify listeners about a change
     */
    protected void change() {
        if (listener() != null) {
            listener().figureRequestUpdate(new FigureChangeEvent(this));
        }
    }   

    /**
     * Read a serialized AssociationLineConnection from an input stream and activate the
     * popup menu again.
     */
    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        // call superclass' private readObject() indirectly
        s.defaultReadObject();
        
        setAttribute(FigureAttributeConstant.POPUP_MENU, createPopupMenu());
    }
}