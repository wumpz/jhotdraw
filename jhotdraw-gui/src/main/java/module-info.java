module jhotdrawGui {
  requires java.desktop;
  requires java.prefs;
  requires jhotdrawActions;
  requires jhotdrawApi;
  requires jhotdrawCore;
  requires jhotdrawUtils;

  exports org.jhotdraw.gui.event;
  exports org.jhotdraw.gui.action;
  exports org.jhotdraw.gui.plaf.palette.colorchooser;
  exports org.jhotdraw.color;
  exports org.jhotdraw.draw.gui;
  exports org.jhotdraw.gui.plaf.palette;
  exports org.jhotdraw.gui;
  exports org.jhotdraw.gui.fontchooser;
  exports org.jhotdraw.text;
  exports org.jhotdraw.gui.plaf;
}
