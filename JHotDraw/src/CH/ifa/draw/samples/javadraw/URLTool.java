/*
 * @(#)URLTool.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import java.awt.event.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.*;


/**
 * A tool to attach URLs to figures.
 * The URLs are stored in the figure's "URL" attribute.
 * The URL text is entered with a FloatingTextField.
 * @see FloatingTextField
 */
public  class URLTool extends AbstractTool {

    private FloatingTextField   fTextField;
    private Figure              fURLTarget;

    public URLTool(DrawingView view) {
        super(view);
    }

    public void mouseDown(MouseEvent e, int x, int y)
    {
	    Figure pressedFigure;

	    pressedFigure =  drawing().findFigureInside(x, y);
	    if (pressedFigure != null) {
	        beginEdit(pressedFigure);
	        return;
	    }
	    endEdit();
    }

    public void mouseUp(MouseEvent e, int x, int y) {
    }

    public void deactivate(DrawingView view) {
        super.deactivate();
        endEdit();
    }

    public void endAction(ActionEvent e) {
        endEdit();
    }

    private void beginEdit(Figure figure) {
        if (fTextField == null) {
            fTextField = new FloatingTextField();
		    fTextField.addActionListener(
		        new ActionListener() {
		            public void actionPerformed(ActionEvent event) {
		                endAction(event);
		            }
		        }
		    );
        }

	    if (figure != fURLTarget && fURLTarget != null)
	        endEdit();
        if (figure != fURLTarget) {
            fTextField.createOverlay((Container)view());
	        fTextField.setBounds(fieldBounds(figure), getURL(figure));
	        fURLTarget = figure;
	    }
    }

    private void endEdit() {
	    if (fURLTarget != null) {
		    setURL(fURLTarget, fTextField.getText());
	        fURLTarget = null;
	        fTextField.endOverlay();
	    }
    }

    private Rectangle fieldBounds(Figure figure) {
    	Rectangle box = figure.displayBox();
        int nChars = Math.max(20, getURL(figure).length());
        Dimension d = fTextField.getPreferredSize(nChars);
        box.x = Math.max(0, box.x + (box.width - d.width)/2);
        box.y = Math.max(0, box.y + (box.height - d.height)/2);
        return new Rectangle(box.x, box.y, d.width, d.height);
    }

    private String getURL(Figure figure) {
        String url = (String) figure.getAttribute("URL");
        if (url == null)
            url = "";
        return url;
    }

    private void setURL(Figure figure, String url) {
        figure.setAttribute("URL", url);
    }
}

