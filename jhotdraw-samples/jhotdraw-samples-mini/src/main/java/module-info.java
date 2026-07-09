module jhotdrawSamplesMini {
  requires java.desktop;
  requires java.logging;
  requires jhotdrawActions;
  requires jhotdrawApi;
  requires jhotdrawCore;
  requires jhotdrawGui;
  requires jhotdrawIo;
  requires jhotdrawSamplesMisc;
  requires jhotdrawUtils;
  requires jhotdrawXml;

  exports org.jhotdraw.samples.mini;
  exports org.jhotdraw.samples.font;
  exports org.jhotdraw.samples.color;
}
