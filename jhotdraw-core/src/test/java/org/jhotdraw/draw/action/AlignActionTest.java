/*
 * @(#)AlignActionTest.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.constrainer.Constrainer;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.handle.Handle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for AlignAction.
 *
 * <p>These tests pin the observable alignment behaviour for all six directions. They use a minimal
 * stub for DrawingView so no Swing infrastructure is required. The same tests pass before and after
 * the Form Template Method refactoring, demonstrating behaviour preservation.
 *
 * <p>Test figures (x, y, width, height):
 *
 * <pre>
 *   f1 = (10, 20, 50, 30)
 *   f2 = (40, 60, 60, 40)
 *   f3 = (80, 10, 30, 50)
 *   selectionBounds = (10, 10, 100, 90)
 * </pre>
 */
public class AlignActionTest {

  private static final double DELTA = 1e-9;

  private RectangleFigure f1;
  private RectangleFigure f2;
  private RectangleFigure f3;
  private Set<Figure> figures;
  private Rectangle2D.Double selectionBounds;

  @BeforeEach
  void setUp() {
    f1 = new RectangleFigure(10, 20, 50, 30);
    f2 = new RectangleFigure(40, 60, 60, 40);
    f3 = new RectangleFigure(80, 10, 30, 50);
    figures = new LinkedHashSet<>();
    figures.add(f1);
    figures.add(f2);
    figures.add(f3);
    // union of all three bounds: x=10, y=10, w=100, h=90
    selectionBounds = new Rectangle2D.Double(10, 10, 100, 90);
  }

  // -------------------------------------------------------------------------
  // Helper: create the action with a stub view and a no-op undo sink.
  // All six subclasses are constructed with null editor (safe: see setEditor).
  // -------------------------------------------------------------------------

  private DrawingView viewFor(Set<Figure> figs) {
    return new DrawingViewStub(figs);
  }

  // ---- North: top edges move to selectionBounds.y -------------------------

  @Test
  void alignNorth_allTopEdgesEqualSelectionTop() {
    AlignAction.North action = new AlignAction.North(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double top = selectionBounds.y; // 10
    assertThat(f1.getBounds().y).isEqualTo(top, offset(DELTA));
    assertThat(f2.getBounds().y).isEqualTo(top, offset(DELTA));
    assertThat(f3.getBounds().y).isEqualTo(top, offset(DELTA));
  }

  // ---- South: bottom edges move to selectionBounds.y + selectionBounds.height

  @Test
  void alignSouth_allBottomEdgesEqualSelectionBottom() {
    AlignAction.South action = new AlignAction.South(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double bottom = selectionBounds.y + selectionBounds.height; // 100
    assertThat(f1.getBounds().y + f1.getBounds().height).isEqualTo(bottom, offset(DELTA));
    assertThat(f2.getBounds().y + f2.getBounds().height).isEqualTo(bottom, offset(DELTA));
    assertThat(f3.getBounds().y + f3.getBounds().height).isEqualTo(bottom, offset(DELTA));
  }

  // ---- West: left edges move to selectionBounds.x -------------------------

  @Test
  void alignWest_allLeftEdgesEqualSelectionLeft() {
    AlignAction.West action = new AlignAction.West(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double left = selectionBounds.x; // 10
    assertThat(f1.getBounds().x).isEqualTo(left, offset(DELTA));
    assertThat(f2.getBounds().x).isEqualTo(left, offset(DELTA));
    assertThat(f3.getBounds().x).isEqualTo(left, offset(DELTA));
  }

  // ---- East: right edges move to selectionBounds.x + selectionBounds.width

  @Test
  void alignEast_allRightEdgesEqualSelectionRight() {
    AlignAction.East action = new AlignAction.East(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double right = selectionBounds.x + selectionBounds.width; // 110
    assertThat(f1.getBounds().x + f1.getBounds().width).isEqualTo(right, offset(DELTA));
    assertThat(f2.getBounds().x + f2.getBounds().width).isEqualTo(right, offset(DELTA));
    assertThat(f3.getBounds().x + f3.getBounds().width).isEqualTo(right, offset(DELTA));
  }

  // ---- Horizontal: figure centers-X align to selection center-X ----------

  @Test
  void alignHorizontal_allCentersXEqualSelectionCenterX() {
    AlignAction.Horizontal action = new AlignAction.Horizontal(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double cx = selectionBounds.x + selectionBounds.width / 2; // 60
    assertThat(f1.getBounds().x + f1.getBounds().width / 2).isEqualTo(cx, offset(DELTA));
    assertThat(f2.getBounds().x + f2.getBounds().width / 2).isEqualTo(cx, offset(DELTA));
    assertThat(f3.getBounds().x + f3.getBounds().width / 2).isEqualTo(cx, offset(DELTA));
  }

  // ---- Vertical: figure centers-Y align to selection center-Y ------------

  @Test
  void alignVertical_allCentersYEqualSelectionCenterY() {
    AlignAction.Vertical action = new AlignAction.Vertical(null) {
      @Override
      protected DrawingView getView() {
        return viewFor(figures);
      }

      @Override
      protected void fireUndoableEditHappened(UndoableEdit edit) {}
    };
    action.alignFigures(figures, selectionBounds);
    double cy = selectionBounds.y + selectionBounds.height / 2; // 55
    assertThat(f1.getBounds().y + f1.getBounds().height / 2).isEqualTo(cy, offset(DELTA));
    assertThat(f2.getBounds().y + f2.getBounds().height / 2).isEqualTo(cy, offset(DELTA));
    assertThat(f3.getBounds().y + f3.getBounds().height / 2).isEqualTo(cy, offset(DELTA));
  }

  // =========================================================================
  // Stub: minimal DrawingView — only getSelectedFigures() is exercised by
  // alignFigures(). All other methods are either no-ops (void) or throw.
  // =========================================================================
  @SuppressWarnings("unused")
  private static class DrawingViewStub implements DrawingView {

    private final Set<Figure> figures;

    DrawingViewStub(Set<Figure> figures) {
      this.figures = figures;
    }

    @Override
    public Set<Figure> getSelectedFigures() {
      return figures;
    }

    @Override
    public int getSelectionCount() {
      return figures.size();
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public Drawing getDrawing() {
      return null;
    }

    // void no-ops
    @Override
    public void setDrawing(Drawing d) {}

    @Override
    public void setCursor(Cursor c) {}

    @Override
    public void addToSelection(Figure f) {}

    @Override
    public void addToSelection(Collection<Figure> figs) {}

    @Override
    public void removeFromSelection(Figure f) {}

    @Override
    public void toggleSelection(Figure f) {}

    @Override
    public void clearSelection() {}

    @Override
    public void selectAll() {}

    @Override
    public void setActiveHandle(Handle h) {}

    @Override
    public void addNotify(DrawingEditor e) {}

    @Override
    public void removeNotify(DrawingEditor e) {}

    @Override
    public void addFigureSelectionListener(FigureSelectionListener l) {}

    @Override
    public void removeFigureSelectionListener(FigureSelectionListener l) {}

    @Override
    public void requestFocus() {}

    @Override
    public void setVisibleConstrainer(Constrainer c) {}

    @Override
    public void setInvisibleConstrainer(Constrainer c) {}

    @Override
    public void setConstrainerVisible(boolean v) {}

    @Override
    public void setScaleFactor(double v) {}

    @Override
    public void setHandleDetailLevel(int v) {}

    @Override
    public void setEnabled(boolean v) {}

    @Override
    public void repaintHandles() {}

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {}

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {}

    @Override
    public void addMouseListener(MouseListener l) {}

    @Override
    public void removeMouseListener(MouseListener l) {}

    @Override
    public void addKeyListener(KeyListener l) {}

    @Override
    public void removeKeyListener(KeyListener l) {}

    @Override
    public void addMouseMotionListener(MouseMotionListener l) {}

    @Override
    public void removeMouseMotionListener(MouseMotionListener l) {}

    @Override
    public void addMouseWheelListener(MouseWheelListener l) {}

    @Override
    public void removeMouseWheelListener(MouseWheelListener l) {}

    // unsupported — not called during alignFigures()
    @Override
    public boolean isFigureSelected(Figure f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Handle findHandle(Point p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Handle> getCompatibleHandles(Handle h) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Handle getActiveHandle() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Figure findFigure(Point p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Figure> findFigures(Rectangle r) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Figure> findFiguresWithin(Rectangle r) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DrawingEditor getEditor() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Point drawingToView(Point2D.Double p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Point2D.Double viewToDrawing(Point p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Rectangle drawingToView(Rectangle2D.Double p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Rectangle2D.Double viewToDrawing(Rectangle p) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Constrainer getConstrainer() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Constrainer getVisibleConstrainer() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Constrainer getInvisibleConstrainer() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstrainerVisible() {
      throw new UnsupportedOperationException();
    }

    @Override
    public JComponent getComponent() {
      throw new UnsupportedOperationException();
    }

    @Override
    public AffineTransform getDrawingToViewTransform() {
      throw new UnsupportedOperationException();
    }

    @Override
    public double getScaleFactor() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getHandleDetailLevel() {
      throw new UnsupportedOperationException();
    }
  }
}
