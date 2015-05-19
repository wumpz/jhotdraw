/*
 * Copyright (C) 2015 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw;

import static com.sun.java.accessibility.util.AWTEventMonitor.addFocusListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JLabel;
import javax.swing.event.EventListenerList;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_OPACITY;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_HEIGHT;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_WIDTH;
import static org.jhotdraw.draw.DrawingView.ACTIVE_HANDLE_PROPERTY;
import static org.jhotdraw.draw.DrawingView.CONSTRAINER_VISIBLE_PROPERTY;
import static org.jhotdraw.draw.DrawingView.DRAWING_PROPERTY;
import static org.jhotdraw.draw.DrawingView.INVISIBLE_CONSTRAINER_PROPERTY;
import static org.jhotdraw.draw.DrawingView.VISIBLE_CONSTRAINER_PROPERTY;
import org.jhotdraw.draw.event.CompositeFigureEvent;
import org.jhotdraw.draw.event.CompositeFigureListener;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.jhotdraw.draw.event.HandleEvent;
import org.jhotdraw.draw.event.HandleListener;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.ReversedList;

/**
 * Implementation of DrawingView using no JComponent as a backend. The displaying container comes
 * with a delegator class. Therefore we have a Swing independend implementaion.
 *
 * @author tw
 */
public abstract class AbstractDrawingView implements DrawingView, EditableComponent {

    private static final Logger LOG = Logger.getLogger(AbstractDrawingView.class.getName());

    @Nullable
    private Drawing drawing;
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private transient final EventListenerList listenerList = new EventListenerList();

    /**
     * Holds the selected figures in an ordered put. The ordering reflects the sequence that was
     * used to select the figures.
     */
    private final Set<Figure> selectedFigures = new LinkedHashSet<>();
    private final List<Handle> selectionHandles = new LinkedList<>();
    private boolean isConstrainerVisible = false;
    private Constrainer visibleConstrainer = new GridConstrainer(8, 8);
    private Constrainer invisibleConstrainer = new GridConstrainer();
    private Handle secondaryHandleOwner;
    @Nullable
    private Handle activeHandle;
    private final List<Handle> secondaryHandles = new LinkedList<>();
    private boolean handlesAreValid = true;

    //TODO replace with AffineTransform to support rotation and more complex transformations from
    //document to view
    //private double scaleFactor = 1;
    //private Point translation = new Point(0, 0);
    private int detailLevel;
    @Nullable
    private DrawingEditor editor;
    private JLabel emptyDrawingLabel;
    private boolean paintBackground = true;
    protected BufferedImage backgroundTile;
    private final FigureListener handleInvalidator = new FigureAdapter() {

        @Override
        public void figureHandlesChanged(FigureEvent e) {
            invalidateHandles();
        }
    };

    public boolean isPaintBackground() {
        return paintBackground;
    }

    public void setPaintBackground(boolean paintBackground) {
        this.paintBackground = paintBackground;
    }

    private boolean paintEnabled = true;

    @Override
    public void repaintHandles() {
        validateHandles();
        Rectangle r = null;
        for (Handle h : getSelectionHandles()) {
            if (r == null) {
                r = h.getDrawingArea();
            } else {
                r.add(h.getDrawingArea());
            }
        }
        for (Handle h : getSecondaryHandles()) {
            if (r == null) {
                r = h.getDrawingArea();
            } else {
                r.add(h.getDrawingArea());
            }
        }
        if (r != null) {
            repaint(r);
        }
    }

    /**
     * Draws the background of the drawing view.
     */
    protected void drawBackground(Graphics2D g) {
        if (drawing == null) {
            // there is no drawing and thus no canvas
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        } else if (drawing.get(CANVAS_WIDTH) == null
                || drawing.get(CANVAS_HEIGHT) == null) {
            // the canvas is infinitely large
            Color canvasColor = drawing.get(CANVAS_FILL_COLOR);
            double canvasOpacity = drawing.get(CANVAS_FILL_OPACITY);
            if (canvasColor != null) {
                if (canvasOpacity == 1) {
                    g.setColor(new Color(canvasColor.getRGB()));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    Point r = drawingToView(new Point2D.Double(0, 0));
                    g.setPaint(getBackgroundPaint(r.x, r.y));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(new Color(canvasColor.getRGB() & 0xfffff | ((int) (canvasOpacity * 256) << 24), true));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            } else {
                Point r = drawingToView(new Point2D.Double(0, 0));
                g.setPaint(getBackgroundPaint(r.x, r.y));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        } else {
            // the canvas has a fixed size
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            Rectangle r = drawingToView(new Rectangle2D.Double(0, 0, drawing.get(CANVAS_WIDTH),
                    drawing.get(CANVAS_HEIGHT)));
            g.setPaint(getBackgroundPaint(r.x, r.y));
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    @Override
    public boolean isSelectionEmpty() {
        return selectedFigures.isEmpty();
    }

    private class EventHandler implements FigureListener, CompositeFigureListener, HandleListener, FocusListener {

        @Override
        public void figureAdded(CompositeFigureEvent evt) {
            if (drawing.getChildCount() == 1 && getEmptyDrawingMessage() != null) {
                repaint();
            } else {
                repaintDrawingArea(evt.getCompositeFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
            }
        }

        @Override
        public void figureRemoved(CompositeFigureEvent evt) {
            if (drawing.getChildCount() == 0 && getEmptyDrawingMessage() != null) {
                repaint();
            } else {
                repaintDrawingArea(evt.getCompositeFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
            }
            removeFromSelection(evt.getChildFigure());
        }

        @Override
        public void areaInvalidated(FigureEvent evt) {
            repaintDrawingArea(evt.getFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
        }

        @Override
        public void areaInvalidated(HandleEvent evt) {
            repaint(evt.getInvalidatedArea());
        }

        @Override
        public void handleRequestSecondaryHandles(HandleEvent e) {
            secondaryHandleOwner = e.getHandle();
            secondaryHandles.clear();
            secondaryHandles.addAll(secondaryHandleOwner.createSecondaryHandles());
            for (Handle h : secondaryHandles) {
                h.setView(AbstractDrawingView.this);
                h.addHandleListener(eventHandler);
            }
            repaint();
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (editor != null) {
                editor.setActiveView(AbstractDrawingView.this);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void handleRequestRemove(HandleEvent e) {
            selectionHandles.remove(e.getHandle());
            e.getHandle().dispose();
            invalidateHandles();
            repaint(e.getInvalidatedArea());
        }

        @Override
        public void attributeChanged(FigureEvent e) {
            if (e.getSource() == drawing) {
                AttributeKey<?> a = e.getAttribute();
                if (a.equals(CANVAS_HEIGHT) || a.equals(CANVAS_WIDTH)) {
                    repaint();
                }
                if (e.getInvalidatedArea() != null) {
                    repaintDrawingArea(e.getFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
                } else {
                    repaintDrawingArea(viewToDrawing(getCanvasViewBounds()));
                }
            } else {
                if (e.getInvalidatedArea() != null) {
                    repaintDrawingArea(e.getFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
                }
            }
        }

        @Override
        public void figureHandlesChanged(FigureEvent e) {
        }

        @Override
        public void figureChanged(FigureEvent e) {
            repaintDrawingArea(e.getFigure().getDrawingArea(AttributeKeys.getScaleFactor(getDrawingToViewTransform())));
        }

        @Override
        public void figureAdded(FigureEvent e) {
        }

        @Override
        public void figureRemoved(FigureEvent e) {
        }

        @Override
        public void figureRequestRemove(FigureEvent e) {
        }
    }

    private final EventHandler eventHandler = new EventHandler();

    public AbstractDrawingView() {
        addFocusListener(eventHandler);
    }

    @Override
    @Nullable
    public Drawing getDrawing() {
        return drawing;
    }

    public String getToolTipText(MouseEvent evt) {
        if (getEditor() != null && getEditor().getTool() != null) {
            return getEditor().getTool().getToolTipText(this, evt);
        }
        return null;
    }

    public void setEmptyDrawingMessage(String newValue) {
        String oldValue = (emptyDrawingLabel == null) ? null : emptyDrawingLabel.getText();
        if (newValue == null) {
            emptyDrawingLabel = null;
        } else {
            emptyDrawingLabel = new JLabel(newValue);
            emptyDrawingLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        firePropertyChange("emptyDrawingMessage", oldValue, newValue);
        repaint();
    }

    public String getEmptyDrawingMessage() {
        return (emptyDrawingLabel == null) ? null : emptyDrawingLabel.getText();
    }

    /**
     * Paints the drawing view. Uses rendering hints for fast painting. Paints the canvasColor, the
     * grid, the drawing, the handles and the current tool. TODO: Rename to reflect this is not a
     * Swing or AWT method.
     */
    public void paintComponent(Graphics gr) {
        if (!isPaintEnabled()) {
            return;
        }

        Graphics2D g = (Graphics2D) gr;
        setViewRenderingHints(g);
        if (isPaintBackground()) {
            drawBackground(g);
        }
        drawCanvas(g);
        drawConstrainer(g);
        drawDrawing(g);
        drawHandles(g);
        drawTool(g);
    }

    /**
     * Prints the drawing view. Uses high quality rendering hints for printing. Only prints the
     * drawing. Doesn't print the canvasColor, the grid, the handles and the tool. TODO: rename to
     * reflect that this is not swing or awt
     */
    public void printComponent(Graphics gr) {
        if (!isPaintEnabled()) {
            return;
        }

        Graphics2D g = (Graphics2D) gr;

        // Set rendering hints for quality
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawDrawing(g);
    }

    protected void setViewRenderingHints(Graphics2D g) {
        // Set rendering hints for speed
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Returns the bounds of the canvas on the drawing view.
     *
     * @return The current bounds of the canvas on the drawing view.
     */
    protected Rectangle getCanvasViewBounds() {
        // Position of the zero coordinate point on the view
        int x = 0;
        int y = 0;

        int w = getWidth();
        int h = getHeight();

        if (getDrawing() != null) {
            Double cw = getDrawing().get(CANVAS_WIDTH);
            Double ch = getDrawing().get(CANVAS_HEIGHT);
            if (cw != null && ch != null) {
                Point lowerRight = drawingToView(
                        new Point2D.Double(cw, ch));
                w = lowerRight.x - x;
                h = lowerRight.y - y;
            }

        }

        return new Rectangle(x, y, w, h);
    }

    /**
     * Draws the canvas. If the {@code AttributeKeys.CANVAS_FILL_OPACITY} is not fully opaque, the
     * canvas area is filled with the background paint before the
     * {@code AttributeKeys.CANVAS_FILL_COLOR} is drawn.
     */
    protected void drawCanvas(Graphics2D gr) {
        if (drawing != null) {
            Graphics2D g = (Graphics2D) gr.create();
            AffineTransform tx = g.getTransform();
            g.setTransform(tx);

            drawing.setFontRenderContext(g.getFontRenderContext());
            drawing.drawCanvas(g);
            g.dispose();
        }
    }

    protected void drawConstrainer(Graphics2D g) {
        if (getConstrainer() != null) {
            Shape clip = g.getClip();

            Rectangle r = getCanvasViewBounds();
            g.clipRect(r.x, r.y, r.width, r.height);
            getConstrainer().draw(g, this);

            g.setClip(clip);
        }
    }

    protected void drawDrawing(Graphics2D gr) {
        if (drawing != null) {
            if (drawing.getChildCount() == 0 && emptyDrawingLabel != null) {
                emptyDrawingLabel.setBounds(0, 0, getWidth(), getHeight());
                emptyDrawingLabel.paint(gr);
            } else {
                Graphics2D g = (Graphics2D) gr.create();
                AffineTransform tx = g.getTransform();
                if (getDrawingToViewTransform() != null) {
                    tx.concatenate(getDrawingToViewTransform());
                }
                g.setTransform(tx);

                drawing.setFontRenderContext(g.getFontRenderContext());
                drawing.draw(g);

                g.dispose();
            }

        }
    }

    protected void drawHandles(java.awt.Graphics2D g) {
        if (editor != null && editor.getActiveView() == this) {
            validateHandles();
            for (Handle h : getSelectionHandles()) {
                h.draw(g);
            }

            for (Handle h : getSecondaryHandles()) {
                h.draw(g);
            }

        }
    }

    protected void drawTool(Graphics2D g) {
        if (editor != null && editor.getActiveView() == this && editor.getTool() != null) {
            editor.getTool().draw(g);
        }

    }

    @Override
    public void setDrawing(@Nullable Drawing newValue) {
        Drawing oldValue = drawing;
        if (this.drawing != null) {
            this.drawing.removeCompositeFigureListener(eventHandler);
            this.drawing.removeFigureListener(eventHandler);
            clearSelection();
        }

        this.drawing = newValue;
        if (this.drawing != null) {
            this.drawing.addCompositeFigureListener(eventHandler);
            this.drawing.addFigureListener(eventHandler);
        }

        firePropertyChange(DRAWING_PROPERTY, oldValue, newValue);

        // Revalidate without flickering
        revalidate();

        paintEnabled = false;
        javax.swing.Timer t = new javax.swing.Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
                paintEnabled = true;
            }
        });
        t.setRepeats(false);
        t.start();
    }

    protected void repaintDrawingArea(Rectangle2D.Double r) {
        Rectangle vr = drawingToView(r);
        vr.grow(2, 2);

        repaint(vr);
    }

    /**
     * Adds a figure to the current selection.
     */
    @Override
    public void addToSelection(Figure figure) {
        Set<Figure> oldSelection = new HashSet<>(selectedFigures);
        if (selectedFigures.add(figure)) {
            figure.addFigureListener(handleInvalidator);
            Set<Figure> newSelection = new HashSet<>(selectedFigures);
            Rectangle invalidatedArea = null;
            if (handlesAreValid && getEditor() != null) {
                for (Handle h : figure.createHandles(detailLevel)) {
                    h.setView(this);
                    selectionHandles.add(h);
                    h.addHandleListener(eventHandler);
                    if (invalidatedArea == null) {
                        invalidatedArea = h.getDrawingArea();
                    } else {
                        invalidatedArea.add(h.getDrawingArea());
                    }

                }
            }
            fireSelectionChanged(oldSelection, newSelection);
            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }

        }
    }

    /**
     * Adds a collection of figures to the current selection.
     */
    @Override
    public void addToSelection(Collection<Figure> figures) {
        Set<Figure> oldSelection = new HashSet<>(selectedFigures);
        Set<Figure> newSelection = new HashSet<>(selectedFigures);
        boolean selectionChanged = false;
        Rectangle invalidatedArea = null;
        for (Figure figure : figures) {
            if (selectedFigures.add(figure)) {
                selectionChanged = true;
                newSelection.add(figure);
                figure.addFigureListener(handleInvalidator);
                if (handlesAreValid && getEditor() != null) {
                    for (Handle h : figure.createHandles(detailLevel)) {
                        h.setView(this);
                        selectionHandles.add(h);
                        h.addHandleListener(eventHandler);
                        if (invalidatedArea == null) {
                            invalidatedArea = h.getDrawingArea();
                        } else {
                            invalidatedArea.add(h.getDrawingArea());
                        }

                    }
                }
            }
        }
        if (selectionChanged) {
            fireSelectionChanged(oldSelection, newSelection);
            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }
        }
    }

    /**
     * Removes a figure from the selection.
     */
    @Override
    public void removeFromSelection(Figure figure) {
        Set<Figure> oldSelection = new HashSet<>(selectedFigures);
        if (selectedFigures.remove(figure)) {
            Set<Figure> newSelection = new HashSet<>(selectedFigures);
            invalidateHandles();

            figure.removeFigureListener(handleInvalidator);
            fireSelectionChanged(oldSelection, newSelection);
            repaint();
        }
    }

    /**
     * If a figure isn't selected it is added to the selection. Otherwise it is removed from the
     * selection.
     */
    @Override
    public void toggleSelection(Figure figure) {
        if (selectedFigures.contains(figure)) {
            removeFromSelection(figure);
        } else {
            addToSelection(figure);
        }

    }

    /**
     * Selects all selectable figures.
     */
    @Override
    public void selectAll() {
        Set<Figure> oldSelection = new HashSet<>(selectedFigures);
        selectedFigures.clear();

        for (Figure figure : drawing.getChildren()) {
            if (figure.isSelectable()) {
                selectedFigures.add(figure);
            }
        }

        Set<Figure> newSelection = new HashSet<>(selectedFigures);
        invalidateHandles();

        fireSelectionChanged(oldSelection, newSelection);
        repaint();
    }

    /**
     * Clears the current selection.
     */
    @Override
    public void clearSelection() {
        if (getSelectionCount() > 0) {
            Set<Figure> oldSelection = new HashSet<>(selectedFigures);
            selectedFigures.clear();
            Set<Figure> newSelection = new HashSet<>(selectedFigures);
            invalidateHandles();

            fireSelectionChanged(oldSelection, newSelection);
        }
    }

    /**
     * Test whether a given figure is selected.
     */
    @Override
    public boolean isFigureSelected(Figure checkFigure) {
        return selectedFigures.contains(checkFigure);
    }

    /**
     * Gets the current selection as a FigureSelection. A FigureSelection can be cut, copied,
     * pasted.
     */
    @Override
    public Set<Figure> getSelectedFigures() {
        return Collections.unmodifiableSet(selectedFigures);
    }

    /**
     * Gets the number of selected figures.
     */
    @Override
    public int getSelectionCount() {
        return selectedFigures.size();
    }

    /**
     * Gets the currently active selection handles.
     */
    private List<Handle> getSelectionHandles() {
        validateHandles();
        return Collections.unmodifiableList(selectionHandles);
    }

    /**
     * Gets the currently active secondary handles.
     */
    private List<Handle> getSecondaryHandles() {
        validateHandles();
        return Collections.unmodifiableList(secondaryHandles);
    }

    /**
     * Invalidates the handles.
     */
    private void invalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;

            Rectangle invalidatedArea = null;
            for (Handle handle : selectionHandles) {
                handle.removeHandleListener(eventHandler);
                if (invalidatedArea == null) {
                    invalidatedArea = handle.getDrawingArea();
                } else {
                    invalidatedArea.add(handle.getDrawingArea());
                }

                handle.dispose();
            }

            for (Handle handle : secondaryHandles) {
                handle.removeHandleListener(eventHandler);
                if (invalidatedArea == null) {
                    invalidatedArea = handle.getDrawingArea();
                } else {
                    invalidatedArea.add(handle.getDrawingArea());
                }

                handle.dispose();
            }

            selectionHandles.clear();
            secondaryHandles.clear();
            setActiveHandle(null);
            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }

        }
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        // Validate handles only, if they are invalid, and if
        // the DrawingView has a DrawingEditor.
        if (!handlesAreValid && getEditor() != null) {
            handlesAreValid = true;
            selectionHandles.clear();
            Rectangle invalidatedArea = null;
            while (true) {
                for (Figure figure : getSelectedFigures()) {
                    for (Handle handle : figure.createHandles(detailLevel)) {
                        handle.setView(this);
                        selectionHandles.add(handle);
                        handle.addHandleListener(eventHandler);
                        if (invalidatedArea == null) {
                            invalidatedArea = handle.getDrawingArea();
                        } else {
                            invalidatedArea.add(handle.getDrawingArea());
                        }

                    }
                }

                if (selectionHandles.isEmpty() && detailLevel != 0) {
                    // No handles are available at the desired detail level.
                    // Retry with detail level 0.
                    detailLevel = 0;
                    continue;
                }
                break;
            }

            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }

        }

    }

    /**
     * Finds a handle at a given coordinates.
     *
     * @return A handle, null if no handle is found.
     */
    @Override
    public Handle findHandle(
            Point p) {
        validateHandles();

        for (Handle handle : new ReversedList<>(getSecondaryHandles())) {
            if (handle.contains(p)) {
                return handle;
            }

        }
        for (Handle handle : new ReversedList<>(getSelectionHandles())) {
            if (handle.contains(p)) {
                return handle;
            }
        }
        return null;
    }

    /**
     * Gets compatible handles.
     *
     * @return A collection containing the handle and all compatible handles.
     */
    @Override
    public Collection<Handle> getCompatibleHandles(Handle master) {
        validateHandles();

        HashSet<Figure> owners = new HashSet<>();
        LinkedList<Handle> compatibleHandles = new LinkedList<>();
        owners.add(master.getOwner());
        compatibleHandles.add(master);

        for (Handle handle : getSelectionHandles()) {
            if (!owners.contains(handle.getOwner()) && handle.isCombinableWith(master)) {
                owners.add(handle.getOwner());
                compatibleHandles.add(handle);
            }

        }
        return compatibleHandles;

    }

    /**
     * Finds a figure at a given coordinates.
     *
     * @return A figure, null if no figure is found.
     */
    @Override
    public Figure findFigure(Point p) {
        return drawing == null ? null : drawing.findFigure(viewToDrawing(p));
    }

    @Override
    public Collection<Figure> findFigures(Rectangle r) {
        return drawing == null ? Collections.EMPTY_LIST : drawing.findFigures(viewToDrawing(r));
    }

    @Override
    public Collection<Figure> findFiguresWithin(Rectangle r) {
        return drawing == null ? Collections.EMPTY_LIST : drawing.findFiguresWithin(viewToDrawing(r));
    }

    @Override
    public void addFigureSelectionListener(FigureSelectionListener fsl) {
        listenerList.add(FigureSelectionListener.class, fsl);
    }

    @Override
    public void removeFigureSelectionListener(FigureSelectionListener fsl) {
        listenerList.remove(FigureSelectionListener.class, fsl);
    }

    /**
     * Notify all listenerList that have registered interest for notification on this event type.
     * Also notify listeners who listen for {@link EditableComponent#SELECTION_EMPTY_PROPERTY}.
     */
    protected void fireSelectionChanged(
            Set<Figure> oldValue,
            Set<Figure> newValue) {
        if (listenerList.getListenerCount() > 0) {
            FigureSelectionEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i
                    >= 0; i
                    -= 2) {
                if (listeners[i] == FigureSelectionListener.class) {
                    // Lazily create the event:
                    if (event == null) {
                        event = new FigureSelectionEvent(this, oldValue, newValue);
                    }
                    ((FigureSelectionListener) listeners[i + 1]).selectionChanged(event);
                }
            }

        }

        firePropertyChange(EditableComponent.SELECTION_EMPTY_PROPERTY, oldValue.isEmpty(), newValue.isEmpty());
    }

    @Override
    public Constrainer getConstrainer() {
        return isConstrainerVisible() ? visibleConstrainer : invisibleConstrainer;
    }

    protected Rectangle2D.Double getDrawingArea() {
        return (Rectangle2D.Double) drawing.getDrawingArea().clone();
    }

    /**
     * Converts drawing coordinates to view coordinates.
     */
    @Override
    public Point drawingToView(Point2D.Double p) {
        AffineTransform transform = getDrawingToViewTransform();
        Point2D pnt = transform.transform(p, null);
        return new Point((int) pnt.getX(), (int) pnt.getY());
    }

    /**
     * Minimal rectangle that encloses drawing rectangle (could be rotated).
     *
     * @param r
     * @return
     */
    @Override
    public Rectangle drawingToView(Rectangle2D.Double r) {
        Point pnt = drawingToView(new Point2D.Double(r.getMinX(), r.getMinY()));
        Rectangle rect = new Rectangle(pnt.x, pnt.y, 1, 1);
        rect.add(drawingToView(new Point2D.Double(r.getMaxX(), r.getMinY())));
        rect.add(drawingToView(new Point2D.Double(r.getMaxX(), r.getMaxY())));
        rect.add(drawingToView(new Point2D.Double(r.getMinX(), r.getMaxY())));
        return rect;
    }

    /**
     * Converts view coordinates to drawing coordinates.
     */
    @Override
    public Point2D.Double viewToDrawing(Point p) {
        try {
            AffineTransform transform = getDrawingToViewTransform().createInverse();
            Point2D.Double pint = new Point2D.Double();
            transform.transform(p, pint);
            return pint;
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(AbstractDrawingView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Rectangles are not rotated. Therefore we deliver the smallest rectangle that encloses the
     * rotated target rectangle.
     */
    @Override
    public Rectangle2D.Double viewToDrawing(Rectangle r) {
        Point2D.Double pnt = viewToDrawing(new Point(r.x, r.y));
        Rectangle2D.Double rect = new Rectangle2D.Double(pnt.x, pnt.y, 1, 1);
        rect.add(viewToDrawing(new Point((int) r.getMaxX(), (int) r.getMinY())));
        rect.add(viewToDrawing(new Point((int) r.getMaxX(), (int) r.getMaxY())));
        rect.add(viewToDrawing(new Point((int) r.getMinX(), (int) r.getMaxY())));
        return rect;
    }

    public void fireViewTransformChanged() {
        for (Handle handle : selectionHandles) {
            handle.viewTransformChanged();
        }

        for (Handle handle : secondaryHandles) {
            handle.viewTransformChanged();
        }
    }

    @Override
    public void setHandleDetailLevel(int newValue) {
        if (newValue != detailLevel) {
            detailLevel = newValue;
            invalidateHandles();
            validateHandles();
        }
    }

    @Override
    public int getHandleDetailLevel() {
        return detailLevel;
    }

    @Override
    public abstract AffineTransform getDrawingToViewTransform();

    @Override
    public void delete() {
        final List<Figure> deletedFigures = drawing.sort(getSelectedFigures());

        for (Figure f : deletedFigures) {
            if (!f.isRemovable()) {
                return;
            }
        }

        // Get z-indices of deleted figures
        final int[] deletedFigureIndices = new int[deletedFigures.size()];
        for (int i = 0; i
                < deletedFigureIndices.length; i++) {
            deletedFigureIndices[i] = drawing.indexOf(deletedFigures.get(i));
        }

        clearSelection();
        drawing.removeAll(deletedFigures);

        drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                return labels.getString("edit.delete.text");
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                clearSelection();

                Drawing d = drawing;
                for (int i = 0; i
                        < deletedFigureIndices.length; i++) {
                    d.add(deletedFigureIndices[i], deletedFigures.get(i));
                }

                addToSelection(deletedFigures);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                for (int i = 0; i
                        < deletedFigureIndices.length; i++) {
                    drawing.remove(deletedFigures.get(i));
                }
            }
        });
    }

    @Override
    public void duplicate() {
        Collection<Figure> sorted = drawing.sort(getSelectedFigures());
        HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<>(sorted.size());

        clearSelection();

        final ArrayList<Figure> duplicates = new ArrayList<>(sorted.size());
        AffineTransform tx = new AffineTransform();
        tx.translate(5, 5);
        for (Figure f : sorted) {
            Figure d = f.clone();
            d.transform(tx);
            duplicates.add(d);
            originalToDuplicateMap.put(f, d);
            drawing.add(d);
        }

        for (Figure f : duplicates) {
            f.remap(originalToDuplicateMap, false);
        }

        addToSelection(duplicates);

        drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                return labels.getString("edit.duplicate.text");
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                drawing.removeAll(duplicates);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                drawing.addAll(duplicates);
            }
        });
    }

    @Override
    public void removeNotify(DrawingEditor editor) {
        this.editor = null;
        repaint();

    }

    @Override
    public void addNotify(DrawingEditor editor) {
        DrawingEditor oldValue = editor;
        this.editor = editor;
        firePropertyChange("editor", oldValue, editor);
        invalidateHandles();
        repaint();
    }

    @Override
    public void setVisibleConstrainer(Constrainer newValue) {
        Constrainer oldValue = visibleConstrainer;
        visibleConstrainer
                = newValue;
        firePropertyChange(VISIBLE_CONSTRAINER_PROPERTY, oldValue, newValue);
    }

    @Override
    public Constrainer getVisibleConstrainer() {
        return visibleConstrainer;
    }

    @Override
    public void setInvisibleConstrainer(Constrainer newValue) {
        Constrainer oldValue = invisibleConstrainer;
        invisibleConstrainer
                = newValue;
        firePropertyChange(INVISIBLE_CONSTRAINER_PROPERTY, oldValue, newValue);
    }

    @Override
    public Constrainer getInvisibleConstrainer() {
        return invisibleConstrainer;
    }

    @Override
    public void setConstrainerVisible(boolean newValue) {
        boolean oldValue = isConstrainerVisible;
        isConstrainerVisible
                = newValue;
        firePropertyChange(CONSTRAINER_VISIBLE_PROPERTY, oldValue, newValue);
        repaint();

    }

    @Override
    public boolean isConstrainerVisible() {
        return isConstrainerVisible;
    }

    /**
     * Returns a paint for drawing the background of the drawing area.
     *
     * @return Paint.
     */
    protected Paint getBackgroundPaint(int x, int y) {
        if (backgroundTile == null) {
            backgroundTile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = backgroundTile.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, 16, 16);
            g.setColor(new Color(0xdfdfdf));
            g.fillRect(0, 0, 8, 8);
            g.fillRect(8, 8, 8, 8);
            g.dispose();
        }

        return new TexturePaint(backgroundTile,
                new Rectangle(x, y, backgroundTile.getWidth(), backgroundTile.getHeight()));
    }

    @Override
    public DrawingEditor getEditor() {
        return editor;
    }

    @Override
    public void setActiveHandle(@Nullable Handle newValue) {
        Handle oldValue = activeHandle;
        if (oldValue != null) {
            repaint(oldValue.getDrawingArea());
        }

        activeHandle = newValue;
        if (newValue != null) {
            repaint(newValue.getDrawingArea());
        }

        firePropertyChange(ACTIVE_HANDLE_PROPERTY, oldValue, newValue);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public Handle getActiveHandle() {
        return activeHandle;
    }

    public boolean isPaintEnabled() {
        return paintEnabled;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public abstract void repaint(Rectangle r);

    public abstract Color getBackground();

    public abstract void repaint();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void revalidate();
}
