package CH.ifa.draw.samples.minimap;

import CH.ifa.draw.contrib.Desktop;
import CH.ifa.draw.contrib.SplitPaneDrawApplication;

public class MiniMapApplication extends SplitPaneDrawApplication {
	protected Desktop createDesktop() {
		return new MiniMapDesktop();
	}

	public static void main(String[] args) {
		MiniMapApplication window = new MiniMapApplication();
		window.open();
	}
}
