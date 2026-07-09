module jhotdrawCore {
  exports org.jhotdraw.draw;
  exports org.jhotdraw.draw.event;
  exports org.jhotdraw.draw.action;
  exports org.jhotdraw.draw.io;
  exports org.jhotdraw.draw.figure;
  exports org.jhotdraw.draw.handle;
  exports org.jhotdraw.draw.tool;
  exports org.jhotdraw.draw.constrainer;
  exports org.jhotdraw.draw.print;
  exports org.jhotdraw.draw.connector;
  exports org.jhotdraw.draw.locator;
  exports org.jhotdraw.draw.decoration;
  exports org.jhotdraw.draw.layouter;
  exports org.jhotdraw.draw.text;
  exports org.jhotdraw.draw.liner;

  opens org.jhotdraw.draw;

  requires java.datatransfer;
  requires java.desktop;
  requires java.logging;
  requires jdk.accessibility;
  requires jhotdrawApi;
  requires jhotdrawDatatarnsfer;
  requires jhotdrawUtils;
}
