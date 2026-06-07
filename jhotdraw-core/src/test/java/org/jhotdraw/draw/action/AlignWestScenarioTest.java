/*
 * @(#)AlignWestScenarioTest.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit5.ScenarioTest;
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
import org.junit.jupiter.api.Test;

/**
 * BDD scenario for the Align West user story.
 *
 * <p>User story: As a user, when I select several figures at different x positions and invoke Align
 * West, all figures should share the leftmost x coordinate of the selection.
 */
public class AlignWestScenarioTest
    extends ScenarioTest<
        AlignWestScenarioTest.GivenFiguresAtDifferentX,
        AlignWestScenarioTest.WhenAlignWestIsInvoked,
        AlignWestScenarioTest.ThenAllFiguresShareLeftmostX> {

  @Test
  void figures_at_different_x_positions_are_aligned_to_the_leftmost_x() {
    given().several_figures_at_different_x_positions();
    when().align_west_is_invoked();
    then().all_figures_share_the_leftmost_x_coordinate();
  }

  // -------------------------------------------------------------------------
  // Given
  // -------------------------------------------------------------------------

  public static class GivenFiguresAtDifferentX extends Stage<GivenFiguresAtDifferentX> {

    @ProvidedScenarioState
    Set<Figure> figures;

    @ProvidedScenarioState
    Rectangle2D.Double selectionBounds;

    GivenFiguresAtDifferentX several_figures_at_different_x_positions() {
      figures = new LinkedHashSet<>();
      figures.add(new RectangleFigure(30, 10, 50, 40));
      figures.add(new RectangleFigure(10, 50, 60, 30));
      figures.add(new RectangleFigure(70, 20, 40, 50));
      // selection bounds: x=10, y=10, w=100, h=70
      selectionBounds = new Rectangle2D.Double(10, 10, 100, 70);
      return self();
    }
  }

  // -------------------------------------------------------------------------
  // When
  // -------------------------------------------------------------------------

  public static class WhenAlignWestIsInvoked extends Stage<WhenAlignWestIsInvoked> {

    @ExpectedScenarioState
    Set<Figure> figures;

    @ExpectedScenarioState
    Rectangle2D.Double selectionBounds;

    WhenAlignWestIsInvoked align_west_is_invoked() {
      AlignAction.West action = new AlignAction.West(null) {
        @Override
        protected DrawingView getView() {
          return new MinimalStub(figures);
        }

        @Override
        protected void fireUndoableEditHappened(UndoableEdit edit) {}
      };
      action.alignFigures(figures, selectionBounds);
      return self();
    }
  }

  // -------------------------------------------------------------------------
  // Then
  // -------------------------------------------------------------------------

  public static class ThenAllFiguresShareLeftmostX extends Stage<ThenAllFiguresShareLeftmostX> {

    @ExpectedScenarioState
    Set<Figure> figures;

    @ExpectedScenarioState
    Rectangle2D.Double selectionBounds;

    ThenAllFiguresShareLeftmostX all_figures_share_the_leftmost_x_coordinate() {
      double leftmost = selectionBounds.x; // 10
      for (Figure f : figures) {
        assertThat(f.getBounds().x)
            .as("left edge of figure %s", f)
            .isEqualTo(leftmost, offset(1e-9));
      }
      return self();
    }
  }

  // -------------------------------------------------------------------------
  // Minimal DrawingView stub (same contract as in AlignActionTest)
  // -------------------------------------------------------------------------

  private static class MinimalStub implements DrawingView {

    private final Set<Figure> figures;

    MinimalStub(Set<Figure> figures) {
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
