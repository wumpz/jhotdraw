/*
 * @(#)ComponentFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.figures.AttributeFigure;
import CH.ifa.draw.standard.BoxHandleKit;
import CH.ifa.draw.framework.DrawingEditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.CellRendererPane;

/**
 * @author  Ming Fang
 * @version <$CURRENT_VERSION$>
 */
public class ComponentFigure extends AttributeFigure {
	private Rectangle bounds = new Rectangle();

	/** Holds value of property component. */
	private Component component;
	private transient DrawingEditor myDrawingEditor;

	/**
	 * @param component a lightweight component
	 * @param container the container that provides the screen realestate for paint the component in
	 * you may use StandardDrawingView
	*/
	public ComponentFigure(Component newComponent, DrawingEditor newDrawingEditor) {
		setComponent(newComponent);
		setEditor(newDrawingEditor);
	}

	/**
	 * Changes the display box of a figure. This method is
	 * always implemented in figure subclasses.
	 * It only changes
	 * the displaybox and does not announce any changes. It
	 * is usually not called by the client. Clients typically call
	 * displayBox to change the display box.
	 * @param origin the new origin
	 * @param corner the new corner
	 * @see #displayBox
	 */
	public void basicDisplayBox(Point origin, Point corner) {
		bounds = new Rectangle(origin);
		bounds.add(corner);
	}

	/**
	 * Moves the figure. This is the
	 * method that subclassers override. Clients usually
	 * call displayBox.
	 * @see #moveBy
	 */
	protected void basicMoveBy(int dx, int dy) {
		bounds.translate(dx, dy);
	}

	/**
	 * Gets the display box of a figure
	 * @see #basicDisplayBox
	 */
	public Rectangle displayBox() {
		return new Rectangle(bounds);
	}

	/**
	 * Returns the handles used to manipulate
	 * the figure. Handles is a Factory Method for
	 * creating handle objects.
	 *
	 * @return a Vector of handles
	 * @see Handle
	 */
	public Vector handles() {
		Vector handles = new Vector();
		BoxHandleKit.addHandles(this, handles);
		return handles;
	}

	/**
	 * Getter for property component.
	 * @return Value of property component.
	 */
	public Component getComponent() {
		return this.component;
	}

	/**
	 * Setter for property component.
	 * @param component New value of property component.
	 */
	protected void setComponent(Component newComponent) {
		this.component = newComponent;
	}

	protected DrawingEditor getEditor() {
		return myDrawingEditor;
	}

	protected void setEditor(DrawingEditor newDrawingEditor) {
		myDrawingEditor = newDrawingEditor;
	}

	/**
	 * Draws the figure.
	 * @param g the Graphics to draw into
	 */
	public void draw(Graphics g) {
		// AWT code
		//getComponent().setBounds(displayBox());
		//must create a new graphics with a different cordinate
		//Graphics componentG = g.create(bounds.x, bounds.y, bounds.width, bounds.height);
		//getComponent().paint(componentG);

		Container container = (Container)getEditor().view();
		getCellRendererPane(component, container).paintComponent(g, component, null, bounds.x, bounds.y, bounds.width, bounds.height, true);
	}

	/* This was taken from SwingUtilities
	 *
	 * Ensures that cell renderer <code>c</code> has a
	 * <code>ComponentShell</code> parent and that
	 * the shell's parent is p.
	 */
	private static CellRendererPane getCellRendererPane(Component c, Container p) {
		Container shell = c.getParent();
		if (shell instanceof CellRendererPane) {
			if (shell.getParent() != p) {
				p.add(shell);
			}
		}
		else {
			shell = new CellRendererPane();
			shell.add(c);
			p.add(shell);
		}
		return (CellRendererPane)shell;
	}

	public Object clone() {
		ComponentFigure clonedFigure = (ComponentFigure)super.clone();
		// editor cannot be serialized (and should no be serialized
		// because that would mean a deep copy) so we set the reference now
		clonedFigure.setEditor(getEditor());
		return clonedFigure;
	}
}
