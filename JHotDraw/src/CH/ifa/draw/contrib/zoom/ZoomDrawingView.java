/*
 * File:   ZoomDrawingView.java
 * Author: Andre Spiegel <spiegel@gnu.org>
 *
 * $Id$
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.DrawingChangeEvent;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.SimpleUpdateStrategy;
import CH.ifa.draw.standard.StandardDrawing;
import CH.ifa.draw.standard.StandardDrawingView;
import CH.ifa.draw.util.Geom;

import javax.swing.JViewport;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

/**
 * A view that can display drawings at an arbitrary scale.
 */
public class ZoomDrawingView extends StandardDrawingView {

	/**
	 * The current scaling factor
	 */
	private double scale = 1.0;

	private Rectangle damagedArea = null;

	public ZoomDrawingView(DrawingEditor editor) {
		this(editor, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}

	public ZoomDrawingView(DrawingEditor editor, int width, int height) {
		super(editor, width, height);
	}

	/**
	 * @return The current zoom scale of this view.  The dimensions of
	 *         figures are multiplied by this number before display.
	 */
	public final double getScale() {
		return scale;
	}

	/**
	 * Sets a new zoom scale for this view.  The dimensions of figures
	 * are multiplied by this number before display.
	 */
	private void setScale(double scale) {
		// "de"-scale with old scale
		Dimension oldSize = getUserSize();
		this.scale = scale;
		// re-scale with new scale
		setUserSize(oldSize.width, oldSize.height);
		centralize(drawing());
		forceRedraw();
	}

	private void forceRedraw() {
		drawingInvalidated(new DrawingChangeEvent
				(drawing(), new Rectangle(getSize())));
		repairDamage();
	}

	/**
	 * Sets the size of this view in user coordinates.  The size of the view
	 * on the screen will be this size, multiplied by the current scale.
	 */
	public void setUserSize(int width, int height) {
		setSize((int) (width * getScale()),
				(int) (height * getScale()));
	}

	/**
	 * Sets the size of this view in user coordinates.  The size of the view
	 * on the screen will be this size, multiplied by the current scale.
	 */
	public void setUserSize(Dimension d) {
		setUserSize(d.width, d.height);
	}

	/**
	 * @return the size of this view, in screen coordinates
	 */
	public Dimension getSize() {
		return super.getSize();
	}

	public Dimension getViewportSize() {
		return getParent().getSize();
	}

	protected boolean hasZoomSupport() {
		return getParent() instanceof JViewport;
	}

	protected void setViewPosition(Point newPosition) {
		((JViewport)getParent()).setViewPosition(newPosition);
	}

	/**
	 * @return The size of this view, in user coordinates.  The size
	 *         on the screen is this size, multiplied by the current scale.
	 */
	public Dimension getUserSize() {
		Dimension screenSize = getSize();
		return new Dimension((int) (screenSize.width / getScale()),
				(int) (screenSize.height / getScale()));
	}

	/**
	 * Readjusts this view and its containing ScrollPane to display the
	 * given rectangle, which is given in user coordinates.  This method
	 * only works if this view is contained in a JViewport.  It throws
	 * a RuntimeException otherwise.
	 */
	public void zoom(int x, int y, int width, int height) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			double xScale = (double) viewportSize.width / (double) width;
			double yScale = (double) viewportSize.height / (double) height;
			double newScale = Math.min(xScale, yScale);

			// "de"-scale with old scale
			Dimension userSize = getUserSize();
			this.scale = newScale;
			// re-scale with new scale
			setUserSize(userSize);

			revalidate();
			setViewPosition(
					new Point((int) (x * getScale()), (int) (y * getScale())));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * Set this view's scale factor
	 */
	public void zoom(float scale) {
		if (hasZoomSupport()) {
			JViewport viewport = (JViewport) getParent();
			Dimension viewportSize = viewport.getSize();
			Dimension userSize = getUserSize();
			this.scale = scale;
			Point viewOrg = viewport.getViewPosition();
			viewOrg.x = viewOrg.x + (viewportSize.width / 2);
			viewOrg.y = viewOrg.y + (viewportSize.height / 2);
			int xScreen = (int) (viewOrg.x * scale);
			int yScreen = (int) (viewOrg.y * scale);
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			setUserSize(userSize);
			revalidate();
			viewport.setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		} else
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
	}

	/**
	 * Zooms out by a factor of two, keeping point (x,y), which is given
	 * in user coordinates, in the center.
	 */
	public void zoomOut(int x, int y) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			// "de"-scale with old scale
			Dimension userSize = getUserSize();
			this.scale = getScale() / 2.0;
			int xScreen = (int) (x * getScale());
			int yScreen = (int) (y * getScale());
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			// re-scale with new scale
			setUserSize(userSize);
			revalidate();
			setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * InContext
	 * Zooms in by a factor of the current scale, keeping point (x,y), which is given
	 * in user coordinates, in the center.
	 */
	public void zoomIn(int x, int y) {
		if (hasZoomSupport()) {
			JViewport viewport = (JViewport) getParent();
			Dimension viewportSize = viewport.getSize();
			Dimension userSize = getUserSize();
			this.scale = getScale() * 1.1;
			int xScreen = (int) (x * getScale());
			int yScreen = (int) (y * getScale());
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			setUserSize(userSize);
			revalidate();
			viewport.setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		} else
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
	}

	/**
	 * Sets the zoom scale to 1.0 and adjusts the scroll pane
	 * so that point (x, y) is in the center.
	 */
	public void deZoom(int x, int y) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			Dimension userSize = getUserSize();
			int xOrigin = x - viewportSize.width / 2;
			int yOrigin = y - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			this.scale = 1.0;
			setUserSize(userSize);
			revalidate();
			setViewPosition(new Point((int) (xOrigin),
					(int) (yOrigin)));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	public void paint(Graphics g) {
		super.paint(transformGraphics(g, getScale()));
	}

	public Graphics getGraphics() {
		return transformGraphics(super.getGraphics(), getScale());
	}

	private final Graphics transformGraphics(Graphics g, double scale) {
		if (scale != 1.0) {
			Graphics2D g2 = (Graphics2D) g;
			// Don't use setTransform() here because that would destroy
			// any transformation that Swing sets for partial redrawing.
			// Simply add our own transformation to any existing one.
			g2.transform(AffineTransform.getScaleInstance(scale, scale));
		}
        return g;
	}

	/**
	 * Constrain to user coordinates, not screen coordinates.
	 */
	protected Point constrainPoint(Point p) {
		Dimension size = getSize();
		p.x = Geom.range(1, (int) (size.width / getScale()), p.x);
		p.y = Geom.range(1, (int) (size.height / getScale()), p.y);
		if (getConstrainer() != null) {
			return getConstrainer().constrainPoint(p);
		}
		return p;
	}

	public void drawBackground(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0,
				(int) (getBounds().width / getScale()),
				(int) (getBounds().height / getScale()));
	}

	private void centralize(Drawing d, Dimension bounds) {
		Point boundsCenter = new Point(bounds.width / 2, bounds.height / 2);
		Rectangle r = ((StandardDrawing) d).displayBox();
		Point drawingCenter = new Point(r.x + r.width / 2, r.y + r.height / 2);
		int diffX = boundsCenter.x - drawingCenter.x;
		int diffY = boundsCenter.y - drawingCenter.y;
		if (diffX != 0 || diffY != 0) {
			for (FigureEnumeration k = d.figures(); k.hasMoreElements();) {
				k.nextFigure().moveBy(diffX, diffY);
			}
		}
	}

	private void centralize(Drawing d) {
		centralize(d, getUserSize());
	}

	public void setDrawing(Drawing d) {
		super.setDrawing(d);

		Rectangle r = ((StandardDrawing) d).displayBox();
		Dimension drawingSize = new Dimension(r.width, r.height);
		Dimension viewportSize = new Dimension(r.width, r.height);
		if (getParent() != null) {
			viewportSize = getViewportSize();
		}
/*
		Dimension userSize = new Dimension(viewportSize);
		this.scale = 1.0;

		while (drawingSize.width > userSize.width ||
				drawingSize.height > userSize.height) {
			this.scale = getScale() / 2.0;
			userSize.width = userSize.width * 2;
			userSize.height = userSize.height * 2;
		}
		centralize(d, userSize);
*/
		super.setPreferredSize(viewportSize);
		super.setSize(viewportSize);
		revalidate();
	}

	public Dimension getMinimumSize() {
		return super.getSize();
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * Overridden to scale damage to screen coordinates.
	 */
	public void repairDamage() {
		if (damagedArea != null) {
			repaint((int) (damagedArea.x * getScale()),
					(int) (damagedArea.y * getScale()),
					(int) (damagedArea.width * getScale()),
					(int) (damagedArea.height * getScale()));
			damagedArea = null;
		}
	}

	/**
	 * Overridden to accumulate damage in an instance variable of this class.
	 */
	public void drawingInvalidated(DrawingChangeEvent e) {
		Rectangle r = e.getInvalidatedRectangle();
		if (damagedArea == null) {
			damagedArea = r;
		}
		else {
			damagedArea.add(r);
		}
	}

	/**
	 * @return a new MouseEvent, the coordinates of which are transformed
	 *         to compensate for the current zoom factor
	 */
	private MouseEvent createScaledEvent(MouseEvent e) {
		return new MouseEvent(e.getComponent(),
				e.getID(),
				e.getWhen(),
				e.getModifiers(),
				(int) (e.getX() / getScale()),
				(int) (e.getY() / getScale()),
				e.getClickCount(),
				e.isPopupTrigger());
	}


	protected MouseListener createMouseListener() {
		return new StandardDrawingView.DrawingViewMouseListener() {
			public void mousePressed(MouseEvent e) {
				super.mousePressed(createScaledEvent(e));
			}
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(createScaledEvent(e));
			}
		};
	}

	protected MouseMotionListener createMouseMotionListener() {
		return new StandardDrawingView.DrawingViewMouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(createScaledEvent(e));
			}
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(createScaledEvent(e));
			}
		};
	}

	protected KeyListener createKeyListener() {
		return new StandardDrawingView.DrawingViewKeyListener() {
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyChar() == ' ') {
					forceRedraw();
				}
				else if (e.getKeyChar() == 'o') {
					setScale(getScale() / 2);
				}
				else if (e.getKeyChar() == 'i') {
					setScale(getScale() * 2);
				}
				else if (e.getKeyChar() == 'c') {
					centralize(drawing());
				}
				else {
					super.keyPressed(e);
				}
			}
		};
	}
}
