module jhotdrawActions {
  requires java.datatransfer;
  requires java.desktop;
  requires jhotdrawApi;
  requires jhotdrawCore;
  requires jhotdrawDatatarnsfer;
  requires jhotdrawUtils;

  exports org.jhotdraw.action.window;
  exports org.jhotdraw.action.view;
  exports org.jhotdraw.action;
  exports org.jhotdraw.editor;
  exports org.jhotdraw.action.edit;
}
