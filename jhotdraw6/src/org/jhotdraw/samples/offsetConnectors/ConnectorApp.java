/*

 * @(#)NetApp.java 5.2

 *

 */



package org.jhotdraw.samples.offsetConnectors;


import javax.swing.*;

import java.awt.*;

import java.util.*;

import java.io.*;

import org.jhotdraw.framework.*;

import org.jhotdraw.standard.*;

import org.jhotdraw.figures.*;

import org.jhotdraw.util.*;

import org.jhotdraw.application.*;



public  class ConnectorApp extends DrawApplication {



    ConnectorApp() {

        super("ConnectorApp");

    }



    

    protected void createTools(JToolBar palette) {

        super.createTools(palette);



        Tool tool = new TextTool(this, new NodeFigure());

        palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));



        tool = new CreationTool(this, new NodeFigure());

        palette.add(createToolButton(IMAGES+"RECT", "Create Org Unit", tool));



        tool = new ConnectionTool(this, new LineConnection());

        palette.add(createToolButton(IMAGES+"CONN", "Connection Tool", tool));

        

        tool = new CreationTool(this, new MyEllipseFigure());

        palette.add(createToolButton(IMAGES+"RRECT", "Round Rectangle Tool", tool));

        

        tool = new CreationTool(this, new MyFigure());

        palette.add(createToolButton(IMAGES+"RECT", "Rectangle Tool", tool));

    }



    //-- main -----------------------------------------------------------



	public static void main(String[] args) {

		DrawApplication window = new ConnectorApp();

		window.open();

    }

}

