/*
 * @(#)PertApplication.java 5.1
 *
 */

package CH.ifa.draw.samples.pert;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.application.*;

public  class PertApplication extends DrawApplication {

    static private final String PERTIMAGES = "/CH/ifa/draw/samples/pert/images/";

    PertApplication() {
        super("PERT Editor");
    }

    protected void createTools(Panel palette) {
        super.createTools(palette);

        Tool tool;
        tool = new TextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

        // the generic but slower version
        //tool = new CreationTool(new PertFigure());
        //palette.add(createToolButton(PERTIMAGES+"PERT", "Task Tool", tool));

        tool = new PertFigureCreationTool(view());
        palette.add(createToolButton(PERTIMAGES+"PERT", "Task Tool", tool));

        tool = new ConnectionTool(view(), new PertDependency());
        palette.add(createToolButton(IMAGES+"CONN", "Dependency Tool", tool));

        tool = new CreationTool(view(), new LineFigure());
        palette.add(createToolButton(IMAGES+"Line", "Line Tool", tool));
    }

    //-- main -----------------------------------------------------------

	public static void main(String[] args) {
		PertApplication pert = new PertApplication();
		pert.open();
    }
}

