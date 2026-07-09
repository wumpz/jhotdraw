module jhotdrawApp {
  requires java.datatransfer;
  requires java.desktop;
  requires java.logging;
  requires java.prefs;
  requires jhotdrawActions;
  requires jhotdrawApi;
  requires jhotdrawGui;
  requires jhotdrawUtils;

  exports org.jhotdraw.app.action.app;
  exports org.jhotdraw.app.action.window;
  exports org.jhotdraw.app;
  exports org.jhotdraw.app.action.file;
  exports org.jhotdraw.app.osx;
  exports org.jhotdraw.app.action;
}
