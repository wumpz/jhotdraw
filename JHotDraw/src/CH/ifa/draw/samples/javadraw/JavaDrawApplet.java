/*
 * @(#)JavaDrawApplet.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.applet.*;
import CH.ifa.draw.contrib.*;


public  class JavaDrawApplet extends DrawApplet {

    transient private Button          fAnimationButton;
    transient private Animator        fAnimator;

    //-- applet life cycle --------------------------------------------

    public void destroy() {
        super.destroy();
        endAnimation();
    }

    //-- DrawApplet overrides -----------------------------------------

    protected void createTools(Panel palette) {
        super.createTools(palette);

        Tool tool = new TextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

        tool = new ConnectedTextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"ATEXT", "Connected Text Tool", tool));

        tool = new URLTool(view());
        palette.add(createToolButton(IMAGES+"URL", "URL Tool", tool));

        tool = new CreationTool(view(), new RectangleFigure());
        palette.add(createToolButton(IMAGES+"RECT", "Rectangle Tool", tool));

        tool = new CreationTool(view(), new RoundRectangleFigure());
        palette.add(createToolButton(IMAGES+"RRECT", "Round Rectangle Tool", tool));

        tool = new CreationTool(view(), new EllipseFigure());
        palette.add(createToolButton(IMAGES+"ELLIPSE", "Ellipse Tool", tool));

        tool = new CreationTool(view(), new LineFigure());
        palette.add(createToolButton(IMAGES+"LINE", "Line Tool", tool));

        tool = new ConnectionTool(view(), new LineConnection());
        palette.add(createToolButton(IMAGES+"CONN", "Connection Tool", tool));

        tool = new ConnectionTool(view(), new ElbowConnection());
        palette.add(createToolButton(IMAGES+"OCONN", "Elbow Connection Tool", tool));

        tool = new ScribbleTool(view());
        palette.add(createToolButton(IMAGES+"SCRIBBL", "Scribble Tool", tool));

        tool = new PolygonTool(view());
        palette.add(createToolButton(IMAGES+"POLYGON", "Polygon Tool", tool));

        tool = new BorderTool(view());
        palette.add(createToolButton(IMAGES+"BORDDEC", "Border Tool", tool));
    }

    protected void createButtons(Panel panel) {
        super.createButtons(panel);
        fAnimationButton = new Button("Start Animation");
		fAnimationButton.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            toggleAnimation();
		        }
		    }
		);
        panel.add(fAnimationButton);
    }

    protected Drawing createDrawing() {
        return new BouncingDrawing();
    }

    //-- animation support ----------------------------------------------

    public void startAnimation() {
        if (drawing() instanceof Animatable && fAnimator == null) {
            fAnimator = new Animator((Animatable)drawing(), view());
            fAnimator.start();
            fAnimationButton.setLabel("End Animation");
        }
    }

    public void endAnimation() {
        if (fAnimator != null) {
            fAnimator.end();
            fAnimator = null;
            fAnimationButton.setLabel("Start Animation");
        }
    }

    public void toggleAnimation() {
        if (fAnimator != null)
            endAnimation();
        else
            startAnimation();
    }

}
