module jhotdrawUtils {
  requires java.desktop;
  requires java.logging;
  requires java.prefs;

  exports org.jhotdraw.utils.util.prefs;
  exports org.jhotdraw.utils.beans;
  exports org.jhotdraw.utils.geom.path;
  exports org.jhotdraw.utils.io;
  exports org.jhotdraw.utils.geom;
  exports org.jhotdraw.utils.util;
  exports org.jhotdraw.utils.formatter;
  exports org.jhotdraw.utils.net;
  exports org.jhotdraw.utils.undo;
}
