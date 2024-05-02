/*
 * @(#)DelegationSelectionTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.util.ActionUtil;

/**
 * A SelectionTool, which recognizes double clicks and popup menu triggers. If a double click or
 * popup trigger is encountered a hook method is called, which handles the event. This methods can
 * be overriden in subclasse to provide customized behaviour.
 *
 * <p>By default, this Tool delegates mouse events to a specific Tool if the figure which has been
 * double clicked, provides a specialized tool.
 */
public class DelegationSelectionTool extends SelectionTool {

  private static final long serialVersionUID = 1L;

  /** A set of actions which is applied to the drawing. */
  private Collection<Action> drawingActions;

  /** A set of actions which is applied to a selection of figures. */
  private Collection<Action> selectionActions;

  /**
   * We use this timer, to show a popup menu, when the user presses the mouse key for a second
   * without moving the mouse.
   */
  private javax.swing.Timer popupTimer;

  /** When the popup menu is visible, we do not track mouse movements. */
  private JPopupMenu popupMenu;

  /**
   * We store the last mouse click here, to support multi-click behavior, that is, a behavior that
   * is invoked, when the user clicks multiple on the same spot, but in a longer interval than
   * needed for a double click.
   */
  private MouseEvent lastClickEvent;

  /** This variable is set to true, if a mouse pressed event is a popup trigger. */
  private boolean isMousePressedPopupTrigger;

  public DelegationSelectionTool() {
    this(new ArrayList<Action>(), new ArrayList<Action>());
  }

  public DelegationSelectionTool(
      Collection<Action> drawingActions, Collection<Action> selectionActions) {
    this.drawingActions = drawingActions;
    this.selectionActions = selectionActions;
  }

  public void setDrawingActions(Collection<Action> drawingActions) {
    this.drawingActions = drawingActions;
  }

  public void setFigureActions(Collection<Action> selectionActions) {
    this.selectionActions = selectionActions;
  }

  /**
   * MouseListener method for mousePressed events. If the popup trigger has been activated, then the
   * appropriate hook method is called.
   */
  @Override
  public void mousePressed(final MouseEvent evt) {
    if (popupTimer != null) {
      popupTimer.stop();
      popupTimer = null;
    }
    // XXX - When we want to support multiple views, we have to
    //       implement this:
    // setView((DrawingView)e.getSource());
    isMousePressedPopupTrigger = evt.isPopupTrigger();
    if (isMousePressedPopupTrigger) {
      getView().requestFocus();
      handlePopupMenu(evt);
    } else {
      super.mousePressed(evt);
      popupTimer = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent aevt) {
          handlePopupMenu(evt);
          popupTimer = null;
        }
      });
      popupTimer.setRepeats(false);
      popupTimer.start();
    }
  }

  /**
   * MouseListener method for mouseReleased events. If the popup trigger has been activated, then
   * the appropriate hook method is called.
   */
  @Override
  public void mouseReleased(MouseEvent evt) {
    if (popupTimer != null) {
      popupTimer.stop();
      popupTimer = null;
    }
    if (isMousePressedPopupTrigger) {
      isMousePressedPopupTrigger = false;
    } else {
      if (evt.isPopupTrigger()) {
        handlePopupMenu(evt);
      } else {
        super.mouseReleased(evt);
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent evt) {
    if (popupTimer != null) {
      popupTimer.stop();
      popupTimer = null;
    }
    if (popupMenu == null || !popupMenu.isVisible()) {
      super.mouseDragged(evt);
    }
  }

  @Override
  public void mouseClicked(MouseEvent evt) {
    super.mouseClicked(evt);
    if (!evt.isConsumed()) {
      if (evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1) {
        handleDoubleClick(evt);
      } else if (evt.getClickCount() == 1
          && evt.getModifiersEx() == 0
          && lastClickEvent != null
          && lastClickEvent.getClickCount() == 1
          && lastClickEvent.getModifiersEx() == 0
          && lastClickEvent.getX() == evt.getX()
          && lastClickEvent.getY() == evt.getY()) {
        handleMultiClick(evt);
      }
    }
    lastClickEvent = evt;
  }

  /**
   * Hook method which can be overriden by subclasses to provide specialised behaviour in the event
   * of a popup trigger.
   */
  protected void handlePopupMenu(MouseEvent evt) {
    Point p = new Point(evt.getX(), evt.getY());
    Figure figure = getView().findFigure(p);
    if (figure != null || drawingActions.size() > 0) {
      showPopupMenu(figure, p, evt.getComponent());
    } else {
      popupMenu = null;
    }
  }

  protected void showPopupMenu(Figure figure, Point p, Component c) {
    JPopupMenu menu = new JPopupMenu();
    popupMenu = menu;
    JMenu submenu = null;
    String submenuName = null;
    List<Action> popupActions = new ArrayList<>();
    if (figure != null) {
      List<Action> figureActions = new ArrayList<>(figure.getActions(viewToDrawing(p)));
      if (popupActions.size() != 0 && figureActions.size() != 0) {
        popupActions.add(null);
      }
      popupActions.addAll(figureActions);
      if (popupActions.size() != 0 && selectionActions.size() != 0) {
        popupActions.add(null);
      }
      popupActions.addAll(selectionActions);
    }
    if (popupActions.size() != 0 && drawingActions.size() != 0) {
      popupActions.add(null);
    }
    popupActions.addAll(drawingActions);
    HashMap<Object, ButtonGroup> buttonGroups = new HashMap<>();
    for (Action a : popupActions) {
      if (a != null && a.getValue(ActionUtil.SUBMENU_KEY) != null) {
        if (submenuName == null || !submenuName.equals(a.getValue(ActionUtil.SUBMENU_KEY))) {
          submenuName = (String) a.getValue(ActionUtil.SUBMENU_KEY);
          submenu = new JMenu(submenuName);
          menu.add(submenu);
        }
      } else {
        submenuName = null;
        submenu = null;
      }
      if (a == null) {
        if (submenu != null) {
          submenu.addSeparator();
        } else {
          menu.addSeparator();
        }
      } else {
        AbstractButton button;
        if (a.getValue(ActionUtil.BUTTON_GROUP_KEY) != null) {
          ButtonGroup bg = buttonGroups.get(a.getValue(ActionUtil.BUTTON_GROUP_KEY));
          if (bg == null) {
            bg = new ButtonGroup();
            buttonGroups.put(a.getValue(ActionUtil.BUTTON_GROUP_KEY), bg);
          }
          button = new JRadioButtonMenuItem(a);
          bg.add(button);
          button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
        } else if (a.getValue(ActionUtil.SELECTED_KEY) != null) {
          button = new JCheckBoxMenuItem(a);
          button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
        } else {
          button = new JMenuItem(a);
        }
        if (submenu != null) {
          submenu.add(button);
        } else {
          menu.add(button);
        }
      }
    }
    menu.show(c, p.x, p.y);
  }

  /**
   * Hook method which can be overriden by subclasses to provide specialised behaviour in the event
   * of a double click.
   */
  protected void handleDoubleClick(MouseEvent evt) {
    DrawingView v = getView();
    Point pos = new Point(evt.getX(), evt.getY());
    Handle handle = v.findHandle(pos);
    if (handle != null) {
      handle.trackDoubleClick(pos, evt.getModifiersEx());
    } else {
      Point2D.Double p = viewToDrawing(pos);
      // Note: The search sequence used here, must be
      // consistent with the search sequence used by the
      // HandleTracker, the SelectAreaTracker and SelectionTool.
      // If possible, continue to work with the current selection
      Figure figure = null;
      if (isSelectBehindEnabled()) {
        for (Figure f : v.getSelectedFigures()) {
          if (f.contains(p)) {
            figure = f;
            break;
          }
        }
      }
      // If the point is not contained in the current selection,
      // search for a figure in the drawing.
      if (figure == null) {
        figure = v.findFigure(pos);
      }
      Figure outerFigure = figure;
      if (figure != null && figure.isSelectable()) {
        Tool figureTool = figure.getTool(p);
        if (figureTool == null) {
          figure = getDrawing().findFigureInside(p);
          if (figure != null) {
            figureTool = figure.getTool(p);
          }
        }
        if (figureTool != null) {
          setTracker(figureTool);
          figureTool.mousePressed(evt);
        } else {
          if (outerFigure.handleMouseClick(p, evt, getView())) {
            v.clearSelection();
            v.addToSelection(outerFigure);
          } else {
            v.clearSelection();
            v.addToSelection(outerFigure);
            v.setHandleDetailLevel(v.getHandleDetailLevel() + 1);
          }
        }
      }
    }
    evt.consume();
  }

  /**
   * Hook method which can be overriden by subclasses to provide specialised behaviour in the event
   * of a multi-click.
   */
  protected void handleMultiClick(MouseEvent evt) {
    DrawingView v = getView();
    Point pos = new Point(evt.getX(), evt.getY());
    Handle handle = v.findHandle(pos);
    if (handle == null) {
      v.setHandleDetailLevel(v.getHandleDetailLevel() + 1);
    }
  }

  @Override
  public String getToolTipText(DrawingView view, MouseEvent evt) {
    Handle handle = view.findHandle(evt.getPoint());
    if (handle != null) {
      return handle.getToolTipText(evt.getPoint());
    }
    Figure figure = view.findFigure(evt.getPoint());
    if (figure != null) {
      return figure.getToolTipText(viewToDrawing(evt.getPoint()));
    }
    return null;
  }
}
