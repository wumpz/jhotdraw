/*
 * @(#)ODGDrawing.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg;

import org.jhotdraw.draw.*;

/**
 * ODGDrawing.
 *
 * <p>XXX - This class is going away in future versions: We don't need to subclass QuadTreeDrawing
 * for ODG since we can represent all ODG-specific AttributeKey's instead of using JavaBeans
 * properties.
 */
public class ODGDrawing extends QuadTreeDrawing {

  private static final long serialVersionUID = 1L;
  private String title;
  private String description;

  public ODGDrawing() {}

  public void setTitle(String newValue) {
    String oldValue = title;
    title = newValue;
  }

  public String getTitle() {
    return title;
  }

  public void setDescription(String newValue) {
    String oldValue = description;
    description = newValue;
  }

  public String getDescription() {
    return description;
  }
}
