package CH.ifa.draw.samples.minimap;

import CH.ifa.draw.contrib.SplitPaneDrawApplication;
import CH.ifa.draw.contrib.MiniMapView;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.figures.ImageFigure;
import CH.ifa.draw.util.Iconkit;

import javax.swing.*;
import java.awt.*;

public class MiniMapApplication extends SplitPaneDrawApplication {

	private String imageName = "/CH/ifa/draw/samples/javadraw/sampleimages/view.gif";

	protected void createRightComponent(DrawingView view) {
		Image image = Iconkit.instance().registerAndLoadImage(
			(Component)view, imageName);
		view.add(new ImageFigure(image, imageName, new Point(0,0)));
		view.checkDamage();
//		((CH.ifa.draw.standard.StandardDrawingView)view).checkMinimumSize();
		super.createRightComponent(view);
	}

	protected void createLeftComponent(DrawingView view) {
		JPanel blankPanel = new JPanel();
		blankPanel.setPreferredSize(new Dimension(200, 200));

		MiniMapView mmv = new MiniMapView(view, (JScrollPane)getRightComponent());
		mmv.setPreferredSize(new Dimension(200, 200));

		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, blankPanel, mmv);
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setDividerLocation(200);
		leftSplitPane.setPreferredSize(new Dimension(200, 400));
		leftSplitPane.resetToPreferredSizes();

		setLeftComponent(leftSplitPane);
	}

	public static void main(String[] args) {
		MiniMapApplication window = new MiniMapApplication();
		window.open();
	}
}
