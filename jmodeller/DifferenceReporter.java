package org.jhotdraw.tools.libchecker;

public interface DifferenceReporter extends DifferenceCollector {
	public void startLibrary();
	public void endLibrary();
	public void startPackage();
	public void endPackage();
	public void startClass();
	public void endClass();
}