/*
 * @(#)StandardDrawingView.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.TextTool;

/**
 * The standard implementation of DrawingView.
 *
 * @see DrawingView
 * @see Painter
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public  class StandardDrawingView
        extends JPanel
        implements DrawingView,
                   MouseListener,
                   MouseMotionListener,
                   KeyListener {

    /**
     * The DrawingEditor of the view.
     * @see #tool
     * @see #setStatus
     */
    transient private DrawingEditor   fEditor;

    /**
	 * the registered listeners for selection changes
	 */
	private transient Vector fSelectionListeners;
	
    /**
     * The shown drawing.
     */
    private Drawing         fDrawing;

    /**
     * the accumulated damaged area
     */
    private transient Rectangle fDamage = null;

    /**
     * The list of currently selected figures.
     */
    transient private Vector fSelection;

    /**
     * The shown selection handles.
     */
    transient private Vector fSelectionHandles;

    /**
     * The preferred size of the view
     */
    private Dimension fViewSize;

    /**
     * The position of the last mouse click
     * inside the view.
     */
    private Point fLastClick;

    /**
     * A vector of optional backgrounds. The vector maintains
     * a list a view painters that are drawn before the contents,
     * that is in the background.
     */
    private Vector fBackgrounds = null;

    /**
     * A vector of optional foregrounds. The vector maintains
     * a list a view painters that are drawn after the contents,
     * that is in the foreground.
     */
    private Vector fForegrounds = null;

    /**
     * The update strategy used to repair the view.
     */
    private Painter fUpdateStrategy;

    /**
     * The grid used to constrain points for snap to
     * grid functionality.
     */
    private PointConstrainer fConstrainer;

	/**
	 *
	 */
	private transient UndoManager myUndoManager;
	
    /**
     * Scrolling increment
     */
    public static final int MINIMUM_WIDTH = 400;
    public static final int MINIMUM_HEIGHT = 300;
    public static final int SCROLL_INCR = 100;
    public static final int SCROLL_OFFSET = 10;
     
    /*
     * Serialization support. In JavaDraw only the Drawing is serialized.
     * However, for beans support StandardDrawingView supports
     * serialization
     */
    private static final long serialVersionUID = -3878153366174603336L;
    private int drawingViewSerializedDataVersion = 1;

    /**
     * Constructs the view.
     */
    public StandardDrawingView(DrawingEditor editor) {
        this(editor, MINIMUM_WIDTH, MINIMUM_HEIGHT);
    }
    
    public StandardDrawingView(DrawingEditor editor, int width, int height) {
        fEditor = editor;
        fViewSize = new Dimension(width,height);
		fSelectionListeners = new Vector();
		addFigureSelectionListener(editor());
        fLastClick = new Point(0, 0);
        fConstrainer = null;
        fSelection = new Vector();
        setUndoManager(new UndoManager());
        // JFC/Swing uses double buffering automatically as default
        setDisplayUpdate(new SimpleUpdateStrategy());
        // TODO: Test FastBufferedUpdateStrategy with JFC/Swing double buffering
        //setDisplayUpdate(new FastBufferedUpdateStrategy());
        setBackground(Color.lightGray);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    /**
     * Sets the view's editor.
     */
    public void setEditor(DrawingEditor editor) {
        fEditor = editor;
    }

    /**
     * Gets the current tool.
     */
    public Tool tool() {
        return editor().tool();
    }

    /**
     * Gets the drawing.
     */
    public Drawing drawing() {
        return fDrawing;
    }

    /**
     * Sets and installs another drawing in the view.
     */
    public void setDrawing(Drawing d) {
        if (fDrawing != null) {
            clearSelection();
            fDrawing.removeDrawingChangeListener(this);
        }

        fDrawing = d;
        if (fDrawing != null) {
            fDrawing.addDrawingChangeListener(this);
        }

        checkMinimumSize();
        repaint();
    }

    /**
     * Gets the editor.
     */
    public DrawingEditor editor() {
        return fEditor;
    }

    /**
     * Automatically adjusts the size of view (optionally) and
     * scroll the invalidated rectangle to be visible in viewport
     * Params r - Rectangle to be scrolled
     *        bSizeChange - If true, view may be resized to accomodate "r".
     */     
    protected void scrollToVisible(Rectangle r, boolean bSizeChange) {
        if (bSizeChange) {
            // Flag for size change
            boolean bChanged = false;
            
            // New width and height
            int newWidth  = r.x + r.width  + SCROLL_INCR;
            int newHeight = r.y + r.height + SCROLL_INCR;
      
            // Check the adjustments in size
            if (newWidth > fViewSize.width) {
                fViewSize.width = newWidth;
                bChanged = true;
            }
            
            if (newHeight > fViewSize.height) {
                fViewSize.height = newHeight;
                bChanged = true;
            }
      
            // Auto size
            if (bChanged) {
                setSize(fViewSize);
            }
        }
      
        // Compute intersection of view size and to be scrolled rectangle
        // in order to prevent scrolling beyond view size
        scrollRectToVisible(r.intersection(new Rectangle(0, 0, fViewSize.width, fViewSize.height)));
    }
    
    /**
     * Adds a figure to the drawing.
     * @return the added figure.
     */
    public Figure add(Figure figure) {
        Figure f = drawing().add(figure);
        
        // If new figure is added, check for size and scrolling
        if(f != null) {
            Rectangle r = f.displayBox();
            scrollToVisible(r, true);
        }

        return f;
    }

    /**
     * Removes a figure from the drawing.
     * @return the removed figure
     */
    public Figure remove(Figure figure) {
        return drawing().remove(figure);
    }

    /**
     * Adds a vector of figures to the drawing.
     */
    public void addAll(Vector figures) {
        FigureEnumeration k = new FigureEnumerator(figures);
        while (k.hasMoreElements()) {
            add(k.nextFigure());
        }
    }

    /**
     * Check existance of figure in the drawing
     */
    public boolean figureExists(Figure inf, FigureEnumeration e) {
        while(e.hasMoreElements()) {
            Figure figure = e.nextFigure();

            if(figure.includes(inf)) {
                return true;
            }
        }

      return false;    
    }

    /**
     * Inserts a vector of figures and translates them by the
     * given offset. This function is used to insert figures from clipboards (cut/copy)
     *
     * @return enumeration which has been added to the drawing. The figures in the enumeration
     *         can have changed during adding them (e.g. they could have been decorated).
     */
    public FigureEnumeration insertFigures(FigureEnumeration fe, int dx, int dy, boolean bCheck) {
        if (fe == null) {
            return FigureEnumerator.getEmptyEnumeration();
        }
    
    	Vector addedFigures = new Vector();
        Vector vCF = new Vector(10);
    
        while (fe.hasMoreElements()) {
            Figure figure = fe.nextFigure();
            if (figure instanceof ConnectionFigure) {
                vCF.addElement(figure);
            }
            else if (figure != null) {
                figure.moveBy(dx, dy);
                figure = add(figure);
                addToSelection(figure);
	            // figure might has changed during adding so add it afterwards
	            addedFigures.addElement(figure);
            }
        }
    
        FigureEnumeration ecf = new FigureEnumerator(vCF);
      
        while (ecf.hasMoreElements()) {
            ConnectionFigure cf = (ConnectionFigure) ecf.nextFigure();      
            Figure sf = cf.startFigure();
            Figure ef = cf.endFigure();

            if (figureExists(sf, drawing().figures()) &&
                figureExists(ef, drawing().figures()) &&
                (!bCheck || cf.canConnect(sf, ef))) {

                if (bCheck) {
                    Point sp = sf.center();
                    Point ep = ef.center();            
                    Connector fStartConnector = cf.startFigure().connectorAt(ep.x, ep.y);
                    Connector fEndConnector = cf.endFigure().connectorAt(sp.x, sp.y);
        
                    if (fEndConnector != null && fStartConnector != null) {
                        cf.connectStart(fStartConnector);
                        cf.connectEnd(fEndConnector);
                        cf.updateConnection();
                    }
                }
        
                Figure nf = add(cf);
                addToSelection(nf);
	            // figure might has changed during adding so add it afterwards
	            addedFigures.addElement(nf);
            }
        }
        
        return new FigureEnumerator(addedFigures);
    }

    /**
     * Returns a vector of connectionfigures attached to this figure
     */
    public Vector getConnectionFigures(Figure inFigure) {
        // If no figure or figure is non connectable, just return null
        if (inFigure == null || !inFigure.canConnect()) {
            return null;
        }
        
        // if (inFigure instanceof ConnectionFigure)
        //  return null;

        Vector result = new Vector(5);
        FigureEnumeration figures = drawing().figures();

        // Find all connection figures
        while (figures.hasMoreElements()) {
            Figure f= figures.nextFigure();
        
            if ((f instanceof ConnectionFigure) && !(isFigureSelected(f))) {
                ConnectionFigure cf = (ConnectionFigure) f;
          
                if (cf.startFigure().includes(inFigure) ||
                    cf.endFigure().includes(inFigure)) {
                    result.addElement(f);
                }
            }
        }

        return result;
   }

    /**
     * Gets the minimum dimension of the drawing.
     */
    public Dimension getMinimumSize() {
        return fViewSize;
    }

    /**
     * Gets the preferred dimension of the drawing..
     */
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Sets the current display update strategy.
     * @see Painter
     */
    public void setDisplayUpdate(Painter updateStrategy) {
        fUpdateStrategy = updateStrategy;
    }

    /**
     * Sets the current display update strategy.
     * @see Painter
     */
    public Painter getDisplayUpdate() {
        return fUpdateStrategy;
    }

    /**
     * Gets the currently selected figures.
     * @return a vector with the selected figures. The vector
     * is a copy of the current selection.
     */
    public Vector selection() {
        // protect the vector with the current selection
        return (Vector)fSelection.clone();
    }

    /**
     * Gets an enumeration over the currently selected figures.
     */
    public FigureEnumeration selectionElements() {
        return new FigureEnumerator(selectionZOrdered());
    }

    /**
     * Gets the currently selected figures in Z order.
     * @see #selection
     * @return a vector with the selected figures. The vector
     * is a copy of the current selection.
     */
    public Vector selectionZOrdered() {
        Vector result = new Vector(selectionCount());
        FigureEnumeration figures = drawing().figures();

        while (figures.hasMoreElements()) {
            Figure f= figures.nextFigure();
            if (isFigureSelected(f)) {
                result.addElement(f);
            }
        }
        return result;
    }

    /**
     * Gets the number of selected figures.
     */
    public int selectionCount() {
        return fSelection.size();
    }

	/**
	 * Test whether a given figure is selected.
	 */
	public boolean isFigureSelected(Figure checkFigure) {
		return fSelection.contains(checkFigure);
	}

    /**
     * Adds a figure to the current selection. The figure is only selected if
     * it is also contained in the Drawing associated with this DrawingView.
     */
    public void addToSelection(Figure figure) {
        if (!isFigureSelected(figure) && drawing().includes(figure)) {
            fSelection.addElement(figure);
            fSelectionHandles = null;
            figure.invalidate();
            fireSelectionChanged();
        }
    }

    /**
     * Adds a vector of figures to the current selection.
     */
    public void addToSelectionAll(Vector figures) {
        addToSelectionAll(new FigureEnumerator(figures));
    }

    /**
     * Adds a FigureEnumeration to the current selection.
     */
    public void addToSelectionAll(FigureEnumeration fe) {
        while (fe.hasMoreElements()) {
            addToSelection(fe.nextFigure());
        }
    }

    /**
     * Removes a figure from the selection.
     */
    public void removeFromSelection(Figure figure) {
        if (isFigureSelected(figure)) {
            fSelection.removeElement(figure);
            fSelectionHandles = null;
            figure.invalidate();
            fireSelectionChanged();
        }
    }

    /**
     * If a figure isn't selected it is added to the selection.
     * Otherwise it is removed from the selection.
     */
    public void toggleSelection(Figure figure) {
        if (isFigureSelected(figure)) {
            removeFromSelection(figure);
        }
        else {
            addToSelection(figure);
        }
        fireSelectionChanged();
    }

    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        Figure figure;

        FigureEnumeration k = selectionElements();

        while (k.hasMoreElements()) {
            k.nextFigure().invalidate();
        }
        fSelection = new Vector();
        fSelectionHandles = null;
        fireSelectionChanged();
    }

    /**
     * Gets an enumeration of the currently active handles.
     */
    private Enumeration selectionHandles() {
        if (fSelectionHandles == null) {
            fSelectionHandles = new Vector();
            FigureEnumeration k = selectionElements();
            while (k.hasMoreElements()) {
                Figure figure = k.nextFigure();
                Enumeration kk = figure.handles().elements();
                while (kk.hasMoreElements()) {
                    fSelectionHandles.addElement(kk.nextElement());
                }
            }
        }
        return fSelectionHandles.elements();
    }

    /**
     * Gets the current selection as a FigureSelection. A FigureSelection
     * can be cut, copied, pasted.
     */
    public FigureSelection getFigureSelection() {
        return new StandardFigureSelection(new FigureEnumerator(selectionZOrdered()), selectionCount());
    }

    /**
     * Finds a handle at the given coordinates.
     * @return the hit handle, null if no handle is found.
     */
    public Handle findHandle(int x, int y) {
        Handle handle;

        Enumeration k = selectionHandles();
        while (k.hasMoreElements()) {
            handle = (Handle) k.nextElement();
            if (handle.containsPoint(x, y)) {
                return handle;
            }
        }
        return null;
    }

    /**
     * Informs that the current selection changed.
     * By default this event is forwarded to the
     * drawing editor.
     */
    protected void fireSelectionChanged() {
		if (fSelectionListeners != null) {
			for (int i = 0; i < fSelectionListeners.size(); i++) {
				FigureSelectionListener l = (FigureSelectionListener)fSelectionListeners.elementAt(i);
				l.figureSelectionChanged(this);
			}
		}
    }

    /**
     * Gets the position of the last click inside the view.
     */
    public Point lastClick() {
        return fLastClick;
    }

    /**
     * Sets the grid spacing that is used to constrain points.
     */
    public void setConstrainer(PointConstrainer c) {
        fConstrainer = c;
    }

    /**
     * Gets the current constrainer.
     */
    public PointConstrainer getConstrainer() {
        return fConstrainer;
    }

    /**
     * Constrains a point to the current grid.
     */
    protected Point constrainPoint(Point p) {
        // constrin to view size
        Dimension size = getSize();
        //p.x = Math.min(size.width, Math.max(1, p.x));
        //p.y = Math.min(size.height, Math.max(1, p.y));
        p.x = Geom.range(1, size.width, p.x);
        p.y = Geom.range(1, size.height, p.y);

        if (fConstrainer != null ) {
            return fConstrainer.constrainPoint(p);
        }
        return p;
	}

    /**
     * Handles mouse down events. The event is delegated to the
     * currently active tool.
     * @return whether the event was handled.
     */
    public void mousePressed(MouseEvent e) {
        requestFocus(); // JDK1.1
        Point p = constrainPoint(new Point(e.getX(), e.getY()));
        fLastClick = new Point(e.getX(), e.getY());
        tool().mouseDown(e, p.x, p.y);
        checkDamage();
    }

    /**
     * Handles mouse drag events. The event is delegated to the
     * currently active tool.
     * @return whether the event was handled.
     */
    public void mouseDragged(MouseEvent e) {
        Point p = constrainPoint(new Point(e.getX(), e.getY()));
        tool().mouseDrag(e, p.x, p.y);
        checkDamage();
    }

    /**
     * Handles mouse move events. The event is delegated to the
     * currently active tool.
     * @return whether the event was handled.
     */
    public void mouseMoved(MouseEvent e) {
        tool().mouseMove(e, e.getX(), e.getY());
    }

    /**
     * Handles mouse up events. The event is delegated to the
     * currently active tool.
     * @return whether the event was handled.
     */
    public void mouseReleased(MouseEvent e) {
        Point p = constrainPoint(new Point(e.getX(), e.getY()));
        tool().mouseUp(e, p.x, p.y);
        checkDamage();
    }

    /**
     * Handles key down events. Cursor keys are handled
     * by the view the other key events are delegated to the
     * currently active tool.
     * @return whether the event was handled.
     */
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_BACK_SPACE) || (code == KeyEvent.VK_DELETE)) {
            Command cmd = new DeleteCommand("Delete", this);
            cmd.execute();
        }
        else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP ||
            code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT) {
            handleCursorKey(code);
        }
        else {
            tool().keyDown(e, code);
        }
        checkDamage();
    }

    /**
     * Handles cursor keys by moving all the selected figures
     * one grid point in the cursor direction.
     */
    protected void handleCursorKey(int key) {
        int dx = 0, dy = 0;
        int stepX = 1, stepY = 1;
        // should consider Null Object.
        if (fConstrainer != null) {
            stepX = fConstrainer.getStepX();
            stepY = fConstrainer.getStepY();
        }

        switch (key) {
        case KeyEvent.VK_DOWN:
            dy = stepY;
            break;
        case KeyEvent.VK_UP:
            dy = -stepY;
            break;
        case KeyEvent.VK_RIGHT:
            dx = stepX;
            break;
        case KeyEvent.VK_LEFT:
            dx = -stepX;
            break;
        }
        moveSelection(dx, dy);
    }

    private void moveSelection(int dx, int dy) {
        FigureEnumeration figures = selectionElements();
        while (figures.hasMoreElements()) {
            figures.nextFigure().moveBy(dx, dy);
        }
        checkDamage();
    }

    /**
     * Determines whether to auto scroll damaged rectangle.
     * Override if you do not want drag scroll mechanism.
     * By default this method returns true.
     */
    protected boolean doDragScroll() {
        return true;
    }
    
    /**
     * Refreshes the drawing if there is some accumulated damage
     */
    public synchronized void checkDamage() {
        Enumeration each = drawing().drawingChangeListeners();
        while (each.hasMoreElements()) {
            Object l = each.nextElement();
            if (l instanceof DrawingView) {
                ((DrawingView)l).repairDamage();
            }
        }
    }

    public void repairDamage() {
        if (fDamage == null) {
//            repaint();
    	}
    	else {

// TextFigures have problems with scrolling: avoid scrolling them
// TextTool does not call checkDamage at the moment to avoid scrolling
//            if (doDragScroll() && !(tool() instanceof TextTool)) {
            if (doDragScroll() ) {
                scrollToVisible(fDamage, false);
            }

            repaint(1, fDamage.x, fDamage.y, fDamage.width, fDamage.height);
            fDamage = null;
        }
    }

    public void drawingInvalidated(DrawingChangeEvent e) {
        Rectangle r = e.getInvalidatedRectangle();
        if (fDamage == null) {
            fDamage = r;
        }
        else {
            fDamage.add(r);
        }
    }

    public void drawingRequestUpdate(DrawingChangeEvent e) {
        repairDamage();
    }

	/**
	 * Paints the drawing view. The actual drawing is delegated to
	 * the current update strategy.
	 * @see Painter
	 */
	protected void paintComponent(Graphics g) {
		getDisplayUpdate().draw(g, this);
	}

    /**
     * Draws the contents of the drawing view.
     * The view has three layers: background, drawing, handles.
     * The layers are drawn in back to front order.
     */
    public void drawAll(Graphics g) {
        boolean isPrinting = g instanceof PrintGraphics;
        drawBackground(g);
        if (fBackgrounds != null && !isPrinting) {
            drawPainters(g, fBackgrounds);
        }
        drawDrawing(g);
        if (fForegrounds != null && !isPrinting) {
            drawPainters(g, fForegrounds);
        }
        if (!isPrinting) {
            drawHandles(g);
        }
    }

    /**
     * Draws the given figures.
     * The view has three layers: background, drawing, handles.
     * The layers are drawn in back to front order.
     * No background is drawn.
     */
   public void draw(Graphics g, FigureEnumeration fe) {
        boolean isPrinting = g instanceof PrintGraphics;
        //drawBackground(g);
        if (fBackgrounds != null && !isPrinting) {
            drawPainters(g, fBackgrounds);
        }
        fDrawing.draw(g, fe);
        if (fForegrounds != null && !isPrinting) {
            drawPainters(g, fForegrounds);
        }
        if (!isPrinting) {
            drawHandles(g);
        }
    }

    /**
     * Draws the currently active handles.
     */
    public void drawHandles(Graphics g) {
        Enumeration k = selectionHandles();
        while (k.hasMoreElements()) {
            ((Handle) k.nextElement()).draw(g);
        }
    }

    /**
     * Draws the drawing.
     */
    public void drawDrawing(Graphics g) {
        fDrawing.draw(g);
    }

    /**
     * Draws the background. If a background pattern is set it
     * is used to fill the background. Otherwise the background
     * is filled in the background color.
     */
    public void drawBackground(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getBounds().width, getBounds().height);
    }

    private void drawPainters(Graphics g, Vector v) {
        for (int i = 0; i < v.size(); i++) {
            ((Painter)v.elementAt(i)).draw(g, this);
        }
    }

    /**
     * Adds a background.
     */
    public void addBackground(Painter painter)  {
        if (fBackgrounds == null) {
            fBackgrounds = new Vector(3);
        }
        fBackgrounds.addElement(painter);
        repaint();
    }

    /**
     * Removes a background.
     */
    public void removeBackground(Painter painter)  {
        if (fBackgrounds != null) {
            fBackgrounds.removeElement(painter);
        }
        repaint();
    }

    /**
     * Removes a foreground.
     */
    public void removeForeground(Painter painter)  {
        if (fForegrounds != null) {
            fForegrounds.removeElement(painter);
        }
        repaint();
    }

    /**
     * Adds a foreground.
     */
    public void addForeground(Painter painter)  {
        if (fForegrounds == null) {
            fForegrounds = new Vector(3);
        }
        fForegrounds.addElement(painter);
        repaint();
    }

    /**
     * Freezes the view by acquiring the drawing lock.
     * @see Drawing#lock
     */
    public void freezeView() {
        drawing().lock();
    }

    /**
     * Unfreezes the view by releasing the drawing lock.
     * @see Drawing#unlock
     */
    public void unfreezeView() {
        drawing().unlock();
    }

    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException {

        s.defaultReadObject();

        fSelection = new Vector(); // could use lazy initialization instead
        if (fDrawing != null) {
            fDrawing.addDrawingChangeListener(this);
        }
		fSelectionListeners= new Vector();
    }

    private void checkMinimumSize() {
        FigureEnumeration k = drawing().figures();
        Dimension d = new Dimension(0, 0);
        while (k.hasMoreElements()) {
            Rectangle r = k.nextFigure().displayBox();
            d.width = Math.max(d.width, r.x+r.width);
            d.height = Math.max(d.height, r.y+r.height);
        }
        if (fViewSize.height < d.height || fViewSize.width < d.width) {
            fViewSize.height = d.height + SCROLL_OFFSET;
            fViewSize.width = d.width + SCROLL_OFFSET;
            setSize(fViewSize);
        }
    }

    public boolean isFocusTraversable() {
        return true;
    }

    // listener methods we are not interested in
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

	/**
	 * Add a listener for selection changes.
	 * @param fsl jhotdraw.framework.FigureSelectionListener
	 */
	public void addFigureSelectionListener(FigureSelectionListener fsl) {
		fSelectionListeners.add(fsl);
	}

	/**
	 * Remove a listener for selection changes.
	 * @param fsl jhotdraw.framework.FigureSelectionListener
	 */
	public void removeFigureSelectionListener(FigureSelectionListener fsl) {
		fSelectionListeners.remove(fsl);
	}

	protected void setUndoManager(UndoManager newUndoManager) {
		myUndoManager = newUndoManager;
	}
	
	public UndoManager getUndoManager() {
		return myUndoManager;
	}
}
