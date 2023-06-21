/*
 * @(#)AbstractConstrainer.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.constrainer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/** This abstract class can be extended to implement a {@link Constrainer}. */
public abstract class AbstractConstrainer implements Constrainer {

  private static final long serialVersionUID = 1L;
  /** The listeners waiting for model changes. */
  protected EventListenerList listenerList = new EventListenerList();
  /**
   * Only one <code>ChangeEvent</code> is needed per model instance since the event's only
   * (read-only) state is the source property. The source of events generated here is always "this".
   */
  protected transient ChangeEvent changeEvent = null;

  public AbstractConstrainer() {}

  @Override
  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }

  @Override
  public void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }

  /**
   * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
   *
   * @see EventListenerList
   */
  protected void fireStateChanged() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
      }
    }
  }

  @Override
  public AbstractConstrainer clone() {
    AbstractConstrainer that;
    try {
      that = (AbstractConstrainer) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError("unable to clone", ex);
    }
    that.listenerList = new EventListenerList();
    return that;
  }
}
