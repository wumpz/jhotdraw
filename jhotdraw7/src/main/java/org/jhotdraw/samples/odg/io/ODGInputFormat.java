/*
 * @(#)ODGInputFormat.java  1.0  April 11, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.odg.io;

import java.awt.datatransfer.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import net.n3.nanoxml.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.io.*;
import static org.jhotdraw.samples.odg.ODGConstants.*;
import org.jhotdraw.samples.odg.figures.ODGFigure;

/**
 * ODGInputFormat.
 * This format is aimed to comply to the Open Document Version 1.1 Drawing
 * format.
 * http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-v1.1.pdf
 *
 * @author Werner Randelshofer
 * @version 1.0 April 11, 2007 Created.
 */
public class ODGInputFormat implements InputFormat {
    /**
     * Set this to true, to get debug output on System.out.
     */
    private static final boolean DEBUG = false;
    /**
     * Holds the figures that are currently being read.
     */
    private LinkedList<Figure> figures;
    /**
     * Holds the document that is currently being read.
     */
    private IXMLElement document;
    
    /** Creates a new instance. */
    public ODGInputFormat() {
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Open Document Drawing (ODG)", "odg");
    }
    
    public JComponent getInputFormatAccessory() {
        return null;
    }
    
    public void read(File file, Drawing drawing) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            read(in, drawing);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    public void read(InputStream in, Drawing drawing) throws IOException {
        drawing.addAll(readFigures(in));
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getPrimaryType().equals("application") &&
                flavor.getSubType().equals("vnd.oasis.opendocument.graphics");
    }
    
    public List<Figure> readFigures(Transferable t) throws UnsupportedFlavorException, IOException {
        InputStream in = null;
        try {
            in = (InputStream) t.getTransferData(new DataFlavor("application/vnd.oasis.opendocument.graphics", "Image SVG"));
            return readFigures(in);
        } finally {
            if (in != null) { in.close(); }
        }
    }
    
    public LinkedList<Figure> readFigures(InputStream in) throws IOException {
        // XXX - Determine if "in" is a ZIP-File or a XML file.
        // XXX - If "in" is a ZIP-File, get the "content.xml" file from it.
        return readFiguresFromDocumentContent(in);
    }
    public LinkedList<Figure> readFiguresFromDocumentContent(InputStream in) throws IOException {
        this.figures = new LinkedList<Figure>();
        IXMLParser parser;
        try {
            parser = XMLParserFactory.createDefaultXMLParser();
        } catch (Exception ex) {
            InternalError e = new InternalError("Unable to instantiate NanoXML Parser");
            e.initCause(ex);
            throw e;
        }
        IXMLReader reader = new StdXMLReader(in);
        parser.setReader(reader);
        try {
            document = (IXMLElement) parser.parse();
        } catch (XMLException ex) {
            IOException e = new IOException(ex.getMessage());
            e.initCause(ex);
            throw e;
        }
        
        // Search for the first 'svg' element in the XML document
        // in preorder sequence
        IXMLElement drawing = document;
        Stack<Iterator> stack = new Stack<Iterator>();
        LinkedList<IXMLElement> ll = new LinkedList<IXMLElement>();
        ll.add(document);
        stack.push(ll.iterator());
        while (!stack.empty() && stack.peek().hasNext()) {
            Iterator<IXMLElement> iter = stack.peek();
            IXMLElement node = iter.next();
            Iterator<IXMLElement> children = node.getChildren().iterator();
            
            if (! iter.hasNext()) {
                stack.pop();
            }
            if (children.hasNext()) {
                stack.push(children);
            }
            if (node.getName() != null &&
                    node.getName().equals("drawing") &&
                    (node.getNamespace() == null ||
                    node.getNamespace().equals(OFFICE_NAMESPACE))) {
                drawing = node;
                break;
            }
        }
        
        
        if (drawing.getName() == null ||
                ! drawing.getName().equals("drawing") ||
                (drawing.getNamespace() != null &&
                ! drawing.getNamespace().equals(OFFICE_NAMESPACE))) {
            throw new IOException("'office:drawing' element expected: "+drawing.getName());
        }
        
        readDrawingElement(drawing);
        
        return figures;
    }
    /**
     * Reads an ODG "office:drawing" element.
     */
    private void readDrawingElement(IXMLElement elem)
    throws IOException {
        /*
        2.3.2Drawing Documents
        The content of drawing document consists of a sequence of draw pages.
        <define name="office-body-content" combine="choice">
        <element name="office:drawing">
        <ref name="office-drawing-attlist"/>
        <ref name="office-drawing-content-prelude"/>
        <ref name="office-drawing-content-main"/>
        <ref name="office-drawing-content-epilogue"/>
        </element>
        </define>
        <define name="office-drawing-attlist">
        <empty/>
        </define>
         
        Drawing Document Content Model
        The drawing document prelude may contain text declarations only. To allow office applications to
        implement functionality that usually is available in spreadsheets for drawing documents, it may
        also contain elements that implement enhanced table features. See also section 2.3.4.
        <define name="office-drawing-content-prelude">
        <ref name="text-decls"/>
        <ref name="table-decls"/>
        </define>
         
        The main document content contains a sequence of draw pages.
        <define name="office-drawing-content-main">
        <zeroOrMore>
        <ref name="draw-page"/>
        </zeroOrMore>
        </define>
         
        There are no drawing documents specific epilogue elements, but the epilogue may contain
        elements that implement enhanced table features. See also section 2.3.4.
        <define name="office-drawing-content-epilogue">
        <ref name="table-functions"/>
        </define>
         */
        
        for (IXMLElement node : elem.getChildren()) {
            if (node instanceof IXMLElement) {
                IXMLElement child = (IXMLElement) node;
                if (child.getNamespace() == null ||
                        child.getNamespace().equals(DRAWING_NAMESPACE)) {
                    String name = child.getName();
                    if (name.equals("page")) {
                        readPageElement(elem);
                    }
                }
            }
        }
    }
    /**
     * Reads an ODG "draw:page" element.
     */
    private void readPageElement(IXMLElement elem)
    throws IOException {
        /* 9.1.4Drawing Pages
         *
        The element <draw:page> is a container for content in a drawing or presentation document.
        Drawing pages are used for the following:
        • Forms (see section 11.1)
        • Drawings (see section 9.2)
        • Frames (see section 9.3)
        • Presentation Animations (see section 9.7)
        • Presentation Notes (see section 9.1.5)
         *
        A master page must be assigned to each drawing page.
         *
        <define name="draw-page">
        <element name="draw:page">
        <ref name="common-presentation-header-footer-attlist"/>
        <ref name="draw-page-attlist"/>
        <optional>
        <ref name="office-forms"/>
        </optional>
        <zeroOrMore>
        <ref name="shape"/>
        </zeroOrMore>
        <optional>
        <choice>
        <ref name="presentation-animations"/>
        <ref name="animation-element"/>
        </choice>
        </optional>
        <optional>
        <ref name="presentation-notes"/>
        </optional>
        </element>
        </define>
         *
        The attributes that may be associated with the <draw:page> element are:
        • Page name
        • Page style
        • Master page
        • Presentation page layout
        • Header declaration
        • Footer declaration
        • Date and time declaration
        • ID
         *
        The elements that my be included in the <draw:page> element are:
        • Forms
        • Shapes
        • Animations
        • Presentation notes
         */
        for (IXMLElement node : elem.getChildren()) {
            if (node instanceof IXMLElement) {
                IXMLElement child = (IXMLElement) node;
                readElement(child);
            }
        }
    }
    /**
     * Reads an ODG element.
     */
    private Figure readElement(IXMLElement elem)
    throws IOException {
        /*
        Drawing Shapes
        This section describes drawing shapes that might occur within all kind of applications.
        <define name="shape">
        <choice>
        <ref name="draw-rect"/>
        <ref name="draw-line"/>
        <ref name="draw-polyline"/>
        <ref name="draw-polygon"/>
        <ref name="draw-regular-polygon"/>
        <ref name="draw-path"/>
        <ref name="draw-circle"/>
        <ref name="draw-ellipse"/>
        <ref name="draw-g"/>
        <ref name="draw-page-thumbnail"/>
        <ref name="draw-frame"/>
        <ref name="draw-measure"/>
        <ref name="draw-caption"/>
        <ref name="draw-connector"/>
        <ref name="draw-control"/>
        <ref name="dr3d-scene"/>
        <ref name="draw-custom-shape"/>
        </choice>
        </define>
         */
        Figure f = null;
        if (elem.getNamespace() == null ||
                elem.getNamespace().equals(DRAWING_NAMESPACE)) {
            String name = elem.getName();
            if (name.equals("caption")) {
                f = readCaptionElement(elem);
            } else if (name.equals("circle")) {
                f = readCircleElement(elem);
            } else if (name.equals("connector")) {
                f = readCircleElement(elem);
            } else if (name.equals("customShape")) {
                f = readCustomShapeElement(elem);
            } else if (name.equals("ellipse")) {
                f = readEllipseElement(elem);
            } else if (name.equals("g")) {
                f = readGElement(elem);
            } else if (name.equals("line")) {
                f = readLineElement(elem);
            } else if (name.equals("measure")) {
                f = readMeasureElement(elem);
            } else if (name.equals("path")) {
                f = readPathElement(elem);
            } else if (name.equals("polygon")) {
                f = readPolygonElement(elem);
            } else if (name.equals("polyline")) {
                f = readPolylineElement(elem);
            } else if (name.equals("rect")) {
                f = readRectElement(elem);
            } else if (name.equals("regularPolygon")) {
                f = readRegularPolygonElement(elem);
            } else {
                if (DEBUG) System.out.println("ODGInputFormat not implemented for <"+name+">");
            }
        }
        if (f instanceof ODGFigure) {
            if (((ODGFigure) f).isEmpty()) {
                // System.out.println("Empty figure "+f);
                return null;
            }
        } else if (f != null) {
            if (DEBUG) System.out.println("ODGInputFormat warning: not an ODGFigure "+f);
        }
        
        return f;
    }

    private Figure readEllipseElement(IXMLElement elem) 
    throws IOException {
        return null;
    }

    private Figure readCircleElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readCustomShapeElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    
    private Figure readGElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readLineElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readPathElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readPolygonElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readPolylineElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readRectElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
    private Figure readRegularPolygonElement(IXMLElement elem) 
    throws IOException {
        return null;
    }

    private Figure readMeasureElement(IXMLElement elem) 
    throws IOException {
        return null;
    }

    private Figure readCaptionElement(IXMLElement elem) 
    throws IOException {
        return null;
    }
}