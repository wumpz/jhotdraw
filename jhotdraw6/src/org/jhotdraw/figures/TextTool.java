/*
 * @(#)TextTool.java 5.2
 *
 */

package CH.ifa.draw.figures;

import java.awt.*;
import java.awt.event.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.FloatingTextField;

/**
 * Tool to create new or edit existing text figures.
 * The editing behavior is implemented by overlaying the
 * Figure providing the text with a FloatingTextField.<p>
 * A tool interaction is done once a Figure that is not
 * a TextHolder is clicked.
 *
 * @see TextHolder
 * @see FloatingTextField
 */
public class TextTool extends CreationTool {

    private FloatingTextField   fTextField;
    private TextHolder  fTypingTarget;

    public TextTool(DrawingView view, Figure prototype) {
        super(view, prototype);
    }

    /**
     * If the pressed figure is a TextHolder it can be edited otherwise
     * a new text figure is created.
     */
    public void mouseDown(MouseEvent e, int x, int y)
    {
	    TextHolder textHolder = null;

	    Figure pressedFigure = drawing().findFigureInside(x, y);
	    if (pressedFigure instanceof TextHolder) {
	        textHolder = (TextHolder) pressedFigure;
	        if (!textHolder.acceptsTyping())
	            textHolder = null;
        }
	    if (textHolder != null) {
	        beginEdit(textHolder);
	        return;
	    }
	    if (getTypingTarget() != null) {
	        editor().toolDone();
	        endEdit();
	    } else {
    	    super.mouseDown(e, x, y);
    	    textHolder = (TextHolder)createdFigure();
    	    beginEdit(textHolder);
        }
    }

    public void mouseDrag(MouseEvent e, int x, int y) {
    }

    public void mouseUp(MouseEvent e, int x, int y) {
    }

    /**
     * Terminates the editing of a text figure.
     */
    public void deactivate() {
        super.deactivate();
        endEdit();
    }

    /**
     * Sets the text cursor.
     */
    public void activate() {
        super.activate();
        view().clearSelection();
        // JDK1.1 TEXT_CURSOR has an incorrect hot spot
        //view().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

	/**
	 * Test whether the text tool is currently activated and is displaying
	 * a overlay TextFigure for accepting input.
	 *
	 * @return true, if the text tool has a accepting target TextFigure for its input, false otherwise
	 */
	public boolean isActivated() {
		return getTypingTarget() != null;
	}
	
    protected void beginEdit(TextHolder figure) {
        if (fTextField == null)
            fTextField = new FloatingTextField();

	    if (figure != getTypingTarget() && getTypingTarget() != null)
	        endEdit();

        fTextField.createOverlay((Container)view(), figure.getFont());
	    fTextField.setBounds(fieldBounds(figure), figure.getText());
	    setTypingTarget(figure);

	    view().checkDamage();
    }

    protected void endEdit() {
	    if (getTypingTarget() != null) {
	        if (fTextField.getText().length() > 0)
	            getTypingTarget().setText(fTextField.getText());
	        else {
	            drawing().remove((Figure)getTypingTarget());
	        }
	        setTypingTarget(null);
	        fTextField.endOverlay();
	        view().checkDamage();
	    }
    }

    private Rectangle fieldBounds(TextHolder figure) {
    	Rectangle box = figure.textDisplayBox();
    	int nChars = figure.overlayColumns();
        Dimension d = fTextField.getPreferredSize(nChars);
        return new Rectangle(box.x, box.y, d.width, d.height);
    }
    
    protected void setTypingTarget(TextHolder newTypingTarget) {
        fTypingTarget = newTypingTarget;
    }
    
    protected TextHolder getTypingTarget() {
        return fTypingTarget;
    }
}

