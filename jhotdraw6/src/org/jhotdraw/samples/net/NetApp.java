/*
 * @(#)NetApp.java 5.1
 *
 */

package CH.ifa.draw.samples.net;

import java.awt.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.application.*;

public  class NetApp extends DrawApplication {

    NetApp() {
        super("Net");
    }

    protected void createTools(Panel palette) {
        super.createTools(palette);

        Tool tool = new TextTool(view(), new NodeFigure());
        palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

        tool = new CreationTool(view(), new NodeFigure());
        palette.add(createToolButton(IMAGES+"RECT", "Create Org Unit", tool));

        tool = new ConnectionTool(view(), new LineConnection());
        palette.add(createToolButton(IMAGES+"CONN", "Connection Tool", tool));
    }

    //-- main -----------------------------------------------------------

	public static void main(String[] args) {
		DrawApplication window = new NetApp();
		window.open();
    }
}
