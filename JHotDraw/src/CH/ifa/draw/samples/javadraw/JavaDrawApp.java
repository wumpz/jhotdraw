/*
 * @(#)JavaDrawApp.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.application.*;
import CH.ifa.draw.contrib.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class JavaDrawApp extends MDI_DrawApplication {

	private Animator            fAnimator;
	private static String       fgSampleImagesPath = "CH/ifa/draw/samples/javadraw/sampleimages/";
	private static String       fgSampleImagesResourcePath = "/"+fgSampleImagesPath;

	JavaDrawApp() {
		super("JHotDraw");
	}

	/**
	 * Factory method which create a new instance of this
	 * application.
	 *
	 * @return	newly created application
	 */
	protected DrawApplication createApplication() {
		return new JavaDrawApp();
	}
	
	public void open() {
		super.open();
	}

	//-- application life cycle --------------------------------------------

	public void destroy() {
		super.destroy();
		endAnimation();
	}

	//-- DrawApplication overrides -----------------------------------------

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new UndoableTool(new TextTool(view(), new TextFigure()));
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new UndoableTool(new ConnectedTextTool(view(), new TextFigure()));
//        tool = new ConnectedTextTool(view(), new TextFigure());
		palette.add(createToolButton(IMAGES + "ATEXT", "Connected Text Tool", tool));

		tool = new URLTool(view());
		palette.add(createToolButton(IMAGES + "URL", "URL Tool", tool));

		tool = new UndoableTool(new CreationTool(view(), new RectangleFigure()));
		palette.add(createToolButton(IMAGES + "RECT", "Rectangle Tool", tool));

		tool = new UndoableTool(new CreationTool(view(), new RoundRectangleFigure()));
		palette.add(createToolButton(IMAGES + "RRECT", "Round Rectangle Tool", tool));

		tool = new UndoableTool(new CreationTool(view(), new EllipseFigure()));
		palette.add(createToolButton(IMAGES + "ELLIPSE", "Ellipse Tool", tool));

		tool = new UndoableTool(new PolygonTool(view()));
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new UndoableTool(new CreationTool(view(), new TriangleFigure()));
		palette.add(createToolButton(IMAGES + "TRIANGLE", "Triangle Tool", tool));
		
		tool = new UndoableTool(new CreationTool(view(), new DiamondFigure()));
		palette.add(createToolButton(IMAGES + "DIAMOND", "Diamond Tool", tool));
			
		tool = new UndoableTool(new CreationTool(view(), new LineFigure()));
		palette.add(createToolButton(IMAGES + "LINE", "Line Tool", tool));

		tool = new UndoableTool(new ConnectionTool(view(), new LineConnection()));
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));

		tool = new UndoableTool(new ConnectionTool(view(), new ElbowConnection()));
		palette.add(createToolButton(IMAGES + "OCONN", "Elbow Connection Tool", tool));

		tool = new UndoableTool(new ScribbleTool(view()));
		palette.add(createToolButton(IMAGES + "SCRIBBL", "Scribble Tool", tool));

		tool = new UndoableTool(new BorderTool(view()));
		palette.add(createToolButton(IMAGES + "BORDDEC", "Border Tool", tool));
	}

	protected Tool createSelectionTool() {
		return new MySelectionTool(view());
	}

	protected void createMenus(JMenuBar mb) {
		super.createMenus(mb);
		addMenuIfPossible(mb, createAnimationMenu());
		addMenuIfPossible(mb, createImagesMenu());
		addMenuIfPossible(mb, createWindowMenu());
	}

	protected JMenu createAnimationMenu() {
		JMenu menu = new JMenu("Animation");
		JMenuItem mi = new JMenuItem("Start Animation");
		mi.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					startAnimation();
				}
			}
		);
		menu.add(mi);

		mi = new JMenuItem("Stop Animation");
		mi.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					endAnimation();
				}
			}
		);
		menu.add(mi);
		return menu;
	}

	protected JMenu createWindowMenu() {
		JMenu menu = new JMenu("Window");
		JMenuItem mi = new JMenuItem("New View");
		mi.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newView();
				}
			}
		);
		menu.add(mi);
		mi = new JMenuItem("New Window");
		mi.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newWindow();
				}
			}
		);
		menu.add(mi);

		menu.addSeparator();
		menu.add(new WindowMenu("Window List", (MDIDesktopPane)getDesktop()));
				
		return menu;
	}

	protected JMenu createImagesMenu() {
		CommandMenu menu = new CommandMenu("Images");
		File imagesDirectory = new File(fgSampleImagesPath);
		try {
			String[] list = imagesDirectory.list();
			for (int i = 0; i < list.length; i++) {
				String name = list[i];
				String path = fgSampleImagesResourcePath+name;
				menu.add(new UndoableCommand(
					new InsertImageCommand(name, path, view())));
			}
		} catch (Exception e) {}
		return menu;
	}

	protected Drawing createDrawing() {
		return new BouncingDrawing();
		//return new StandardDrawing();
	}

	//---- animation support --------------------------------------------

	public void startAnimation() {
		if (drawing() instanceof Animatable && fAnimator == null) {
			fAnimator = new Animator((Animatable)drawing(), view());
			fAnimator.start();
		}
	}

	public void endAnimation() {
		if (fAnimator != null) {
			fAnimator.end();
			fAnimator = null;
		}
	}

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		JavaDrawApp window = new JavaDrawApp();
		window.open();
	}
}
