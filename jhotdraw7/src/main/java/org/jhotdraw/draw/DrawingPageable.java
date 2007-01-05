/*
 * @(#)DrawingPageable.java  1.0  January 1, 2007
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
/**
 * DrawingPageable can be used to print a Drawing using the
 * java.awt.print API.
 * <p>
 * Usage:
 * <pre>
 * Pageable pageable = new DrawingPageable(aDrawing);
 * PrinterJob job = PrinterJob.getPrinterJob();
 * job.setPageable(pageable);
 * if (job.printDialog()) {
 *     try {
 *         job.print();
 *      } catch (PrinterException e) {
 *          ...inform the user that we couldn't print...
 *      }
 * }
 * </pre>
 * 
 * @author Werner Randelshofer
 * @version 1.0 January 1, 2007 Created.
 * @see org.jhotdraw.app.action.PrintAction
 */
public class DrawingPageable implements Pageable {
    private Drawing drawing;
    private PageFormat pageFormat;
    private boolean isAutorotate = false;
    
    /** Creates a new instance. */
    public DrawingPageable(Drawing drawing) {
        this.drawing = drawing;
        Paper paper = new Paper();
        pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
    }
    
    public int getNumberOfPages() {
        return 1;
    }
    
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pageFormat;
    }
    
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        if (pageIndex < 0 || pageIndex >= getNumberOfPages()) {
            throw new IndexOutOfBoundsException("Invalid page index:"+pageIndex);
        }
        return new Printable() {
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                return printPage(graphics, pageFormat, pageIndex);
            }
        };
    }
    
    public int printPage(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex < 0 || pageIndex >= getNumberOfPages()) {
            return Printable.NO_SUCH_PAGE;
        }
        if (drawing.getFigureCount() > 0) {
            
            Graphics2D g = (Graphics2D) graphics;
            setRenderingHints(g);
            
            // Determine the draw bounds of the drawing
            Rectangle2D.Double drawBounds = null;
            for (Figure f : drawing.getFigures()) {
                if (drawBounds == null) {
                    drawBounds = f.getDrawingArea();
                } else {
                    drawBounds.add(f.getDrawingArea());
                }
            }
            
            // Setup a transformation for the drawing
            AffineTransform tx = new AffineTransform();
            tx.translate(
                    pageFormat.getImageableX(),
                    pageFormat.getImageableY()
                    );
            
            // Maybe rotate drawing
            if (isAutorotate &&
                    drawBounds.width > drawBounds.height &&
                    pageFormat.getImageableWidth() < pageFormat.getImageableHeight()) {
                
                double scaleFactor = Math.min(
                        pageFormat.getImageableWidth() / drawBounds.height,
                        pageFormat.getImageableHeight() / drawBounds.width
                        );
                tx.scale(scaleFactor, scaleFactor);
                tx.translate(drawBounds.height, 0d);
                tx.rotate(Math.PI / 2d, 0, 0);
                tx.translate(-drawBounds.x, -drawBounds.y);
            } else {
                double scaleFactor = Math.min(
                        pageFormat.getImageableWidth() / drawBounds.width,
                        pageFormat.getImageableHeight() / drawBounds.height
                        );
                tx.scale(scaleFactor, scaleFactor);
                tx.translate(-drawBounds.x, -drawBounds.y);
            }
            g.transform(tx);
            
            // Draw the drawing
            drawing.draw(g);
        }
        return Printable.PAGE_EXISTS;
    }
    protected void setRenderingHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

