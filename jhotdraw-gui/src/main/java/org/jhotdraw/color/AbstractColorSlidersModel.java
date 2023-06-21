/**
 * @(#)AbstractColorSlidersModel.java
 *
 * <p>Copyright (c) 2008 The authors and contributors of JHotDraw. You may not use, copy or modify
 * this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.color;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jhotdraw.beans.AbstractBean;

/** AbstractColorSlidersModel. */
public abstract class AbstractColorSlidersModel extends AbstractBean implements ColorSliderModel {

  private static final long serialVersionUID = 1L;
  /** ChangeListener's listening to changes in this model. */
  protected List<ChangeListener> listeners;

  @Override
  public void addChangeListener(ChangeListener l) {
    if (listeners == null) {
      listeners = new ArrayList<>();
    }
    listeners.add(l);
  }

  @Override
  public void removeChangeListener(ChangeListener l) {
    listeners.remove(l);
  }

  public void fireStateChanged() {
    if (listeners != null) {
      ChangeEvent event = new ChangeEvent(this);
      for (ChangeListener l : listeners) {
        l.stateChanged(event);
      }
    }
  }
}
