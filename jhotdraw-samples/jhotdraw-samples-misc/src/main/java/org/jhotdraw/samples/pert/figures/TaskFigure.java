/*
 * @(#)TaskFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.pert.figures;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.util.*;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.figure.GraphicalCompositeFigure;
import org.jhotdraw.draw.figure.ListFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.ConnectorHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.util.*;

/**
 * TaskFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class TaskFigure extends GraphicalCompositeFigure {

  private static final long serialVersionUID = 1L;
  private HashSet<DependencyFigure> dependencies;

  /** This adapter is used, to connect a TextFigure with the name of the TaskFigure model. */
  private static class NameAdapter extends FigureListenerAdapter {

    private TaskFigure target;

    public NameAdapter(TaskFigure target) {
      this.target = target;
    }

    @Override
    public void attributeChanged(FigureEvent e) {
      // We could fire a property change event here, in case
      // some other object would like to observe us.
      // target.firePropertyChange("name", e.getOldValue(), e.getNewValue());
    }
  }

  private static class DurationAdapter extends FigureListenerAdapter {

    private TaskFigure target;

    public DurationAdapter(TaskFigure target) {
      this.target = target;
    }

    @Override
    public void attributeChanged(FigureEvent evt) {
      // We could fire a property change event here, in case
      // some other object would like to observe us.
      // target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
      for (TaskFigure succ : target.getSuccessors()) {
        succ.updateStartTime();
      }
    }
  }

  public TaskFigure() {
    super(new RectangleFigure());
    setLayouter(new VerticalLayouter());
    RectangleFigure nameCompartmentPF = new RectangleFigure();
    nameCompartmentPF.attr().set(STROKE_COLOR, null);
    nameCompartmentPF.attr().setAttributeEnabled(STROKE_COLOR, false);
    nameCompartmentPF.attr().set(FILL_COLOR, null);
    nameCompartmentPF.attr().setAttributeEnabled(FILL_COLOR, false);
    ListFigure nameCompartment = new ListFigure(nameCompartmentPF);
    ListFigure attributeCompartment = new ListFigure();
    SeparatorLineFigure separator1 = new SeparatorLineFigure();
    add(nameCompartment);
    add(separator1);
    add(attributeCompartment);
    Insets2D.Double insets = new Insets2D.Double(4, 8, 4, 8);
    nameCompartment.attr().set(LAYOUT_INSETS, insets);
    attributeCompartment.attr().set(LAYOUT_INSETS, insets);
    TextFigure nameFigure;
    nameCompartment.add(nameFigure = new TextFigure());
    nameFigure.attr().set(FONT_BOLD, true);
    nameFigure.attr().setAttributeEnabled(FONT_BOLD, false);
    TextFigure durationFigure;
    attributeCompartment.add(durationFigure = new TextFigure());
    durationFigure.attr().set(FONT_BOLD, true);
    durationFigure.setText("0");
    durationFigure.attr().setAttributeEnabled(FONT_BOLD, false);
    TextFigure startTimeFigure;
    attributeCompartment.add(startTimeFigure = new TextFigure());
    startTimeFigure.setEditable(false);
    startTimeFigure.setText("0");
    startTimeFigure.attr().setAttributeEnabled(FONT_BOLD, false);
    attr().setAttributeEnabled(STROKE_DASHES, false);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.pert.Labels");
    setName(labels.getString("pert.task.defaultName"));
    setDuration(0);
    dependencies = new HashSet<DependencyFigure>();
    nameFigure.addFigureListener(new NameAdapter(this));
    durationFigure.addFigureListener(new DurationAdapter(this));
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    java.util.List<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel) {
      case -1:
        handles.add(new BoundsOutlineHandle(getPresentationFigure(), false, true));
        break;
      case 0:
        handles.add(new MoveHandle(this, RelativeLocator.northWest()));
        handles.add(new MoveHandle(this, RelativeLocator.northEast()));
        handles.add(new MoveHandle(this, RelativeLocator.southWest()));
        handles.add(new MoveHandle(this, RelativeLocator.southEast()));
        ConnectorHandle ch;
        handles.add(
            ch = new ConnectorHandle(
                new LocatorConnector(this, RelativeLocator.east()), new DependencyFigure()));
        ch.setToolTipText("Drag the connector to a dependent task.");
        break;
    }
    return handles;
  }

  public void setName(String newValue) {
    getNameFigure().setText(newValue);
  }

  public String getName() {
    return getNameFigure().getText();
  }

  public void setDuration(int newValue) {
    int oldValue = getDuration();
    getDurationFigure().setText(Integer.toString(newValue));
    if (oldValue != newValue) {
      for (TaskFigure succ : getSuccessors()) {
        succ.updateStartTime();
      }
    }
  }

  public int getDuration() {
    try {
      return Integer.valueOf(getDurationFigure().getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public void updateStartTime() {
    willChange();
    int oldValue = getStartTime();
    int newValue = 0;
    for (TaskFigure pre : getPredecessors()) {
      newValue = Math.max(newValue, pre.getStartTime() + pre.getDuration());
    }
    getStartTimeFigure().setText(Integer.toString(newValue));
    if (newValue != oldValue) {
      for (TaskFigure succ : getSuccessors()) {
        // The if-statement here guards against
        // cyclic task dependencies.
        if (!this.isDependentOf(succ)) {
          succ.updateStartTime();
        }
      }
    }
    changed();
  }

  public int getStartTime() {
    try {
      return Integer.valueOf(getStartTimeFigure().getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private TextFigure getNameFigure() {
    return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
  }

  private TextFigure getDurationFigure() {
    return (TextFigure) ((ListFigure) getChild(2)).getChild(0);
  }

  private TextFigure getStartTimeFigure() {
    return (TextFigure) ((ListFigure) getChild(2)).getChild(1);
  }

  @Override
  public TaskFigure clone() {
    TaskFigure that = (TaskFigure) super.clone();
    that.dependencies = new HashSet<DependencyFigure>();
    that.getNameFigure().addFigureListener(new NameAdapter(that));
    that.getDurationFigure().addFigureListener(new DurationAdapter(that));
    that.updateStartTime();
    return that;
  }

  //  @Override
  //  public void read(DOMInput in) throws IOException {
  //    double x = in.getAttribute("x", 0d);
  //    double y = in.getAttribute("y", 0d);
  //    double w = in.getAttribute("w", 0d);
  //    double h = in.getAttribute("h", 0d);
  //    setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
  //    readAttributes(in);
  //    in.openElement("model");
  //    in.openElement("name");
  //    setName((String) in.readObject());
  //    in.closeElement();
  //    in.openElement("duration");
  //    setDuration((Integer) in.readObject());
  //    in.closeElement();
  //    in.closeElement();
  //  }
  //
  //  @Override
  //  public void write(DOMOutput out) throws IOException {
  //    Rectangle2D.Double r = getBounds();
  //    out.addAttribute("x", r.x);
  //    out.addAttribute("y", r.y);
  //    writeAttributes(out);
  //    out.openElement("model");
  //    out.openElement("name");
  //    out.writeObject(getName());
  //    out.closeElement();
  //    out.openElement("duration");
  //    out.writeObject(getDuration());
  //    out.closeElement();
  //    out.closeElement();
  //  }

  @Override
  public int getLayer() {
    return 0;
  }

  public Set<DependencyFigure> getDependencies() {
    return Collections.unmodifiableSet(dependencies);
  }

  public void addDependency(DependencyFigure f) {
    dependencies.add(f);
    updateStartTime();
  }

  public void removeDependency(DependencyFigure f) {
    dependencies.remove(f);
    updateStartTime();
  }

  /**
   * Returns dependent PertTasks which are directly connected via a PertDependency to this
   * TaskFigure.
   */
  public List<TaskFigure> getSuccessors() {
    LinkedList<TaskFigure> list = new LinkedList<TaskFigure>();
    for (DependencyFigure c : getDependencies()) {
      if (c.getStartFigure() == this) {
        list.add((TaskFigure) c.getEndFigure());
      }
    }
    return list;
  }

  /**
   * Returns predecessor PertTasks which are directly connected via a PertDependency to this
   * TaskFigure.
   */
  public List<TaskFigure> getPredecessors() {
    LinkedList<TaskFigure> list = new LinkedList<TaskFigure>();
    for (DependencyFigure c : getDependencies()) {
      if (c.getEndFigure() == this) {
        list.add((TaskFigure) c.getStartFigure());
      }
    }
    return list;
  }

  /**
   * Returns true, if the current task is a direct or indirect dependent of the specified task. If
   * the dependency is cyclic, then this method returns true if <code>this</code> is passed as a
   * parameter and for every other task in the cycle.
   */
  public boolean isDependentOf(TaskFigure t) {
    if (this == t) {
      return true;
    }
    for (TaskFigure pre : getPredecessors()) {
      if (pre.isDependentOf(t)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "TaskFigure#"
        + hashCode()
        + " "
        + getName()
        + " "
        + getDuration()
        + " "
        + getStartTime();
  }
}
