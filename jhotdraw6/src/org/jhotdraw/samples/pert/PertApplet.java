/*
 * @(#)PertApplet.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import javax.swing.JPanel;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.applet.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class PertApplet extends DrawApplet {

    private final static String PERTIMAGES = "/CH/ifa/draw/samples/pert/images/";

    protected void createTools(JPanel palette) {
        super.createTools(palette);

        Tool tool = new TextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

        tool = new PertFigureCreationTool(view());
        palette.add(createToolButton(PERTIMAGES+"PERT", "Task Tool", tool));

        tool = new ConnectionTool(view(), new PertDependency());
        palette.add(createToolButton(IMAGES+"CONN", "Dependency Tool", tool));

        tool = new CreationTool(view(), new LineFigure());
        palette.add(createToolButton(IMAGES+"Line", "Line Tool", tool));
    }
}
