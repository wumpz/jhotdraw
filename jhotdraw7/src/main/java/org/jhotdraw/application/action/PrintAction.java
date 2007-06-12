/*
 * @(#)PrintAction.java  1.0  January 1, 2007
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

package org.jhotdraw.application.action;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.Arrays;
import javax.print.DocPrintJob;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import org.jhotdraw.application.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;
/**
 * Presents a printer dialog to the user and then prints the DocumentView to the
 * chosen printer.
 * <p>
 * This action requires that the documentView has the following additional methods:
 * <pre>
 * public Pageable createPageable();
 * </pre>
 * <p>
 * The PrintAction invokes this method using Java Reflection. Thus there is
 * no Java Interface that the DocumentView needs to implement.
 * 
 * @author Werner Randelshofer
 * @version 1.0 January 1, 2007 Created.
 * @see org.jhotdraw.draw.DrawingPageable
 */
public class PrintAction extends AbstractDocumentViewAction {
    public final static String ID = "File.print";
    
    /** Creates a new instance. */
    public PrintAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        DocumentView documentView = getCurrentView();
        documentView.setEnabled(false);
        if (System.getProperty("apple.awt.graphics.UseQuartz","false").equals("true")) {
            printQuartz();
        } else {
            printJava2D();
        }
        documentView.setEnabled(true);
    }
    /*
     * This prints at 72 DPI only. We might need this for some JVM versions on
     * Mac OS X.*/
    public void printJava2D() {
        Pageable pageable = (Pageable) Methods.invokeGetter(getCurrentView(), "createPageable", null);
        if (pageable == null) {
            throw new InternalError("Project does not have a method named java.awt.Pageable createPageable()");
        }
        
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            job.setPageable(pageable);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
                    JSheet.showMessageSheet(getCurrentView().getComponent(),
                            labels.getFormatted("couldntPrint", e)
                            );
                }
            } else {
                System.out.println("JOB ABORTED!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    /*
     * This prints at 72 DPI only. We might need this for some JVM versions on
     * Mac OS X.*/
    public void printJava2DAlternative() {
        Pageable pageable = (Pageable) Methods.invokeGetter(getCurrentView(), "createPageable", null);
        if (pageable == null) {
            throw new InternalError("Project does not have a method named java.awt.Pageable createPageable()");
        }
        
        try {
            final PrinterJob job = PrinterJob.getPrinterJob();
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            job.setPageable(pageable);
            if (job.printDialog(attr)) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
                    JSheet.showMessageSheet(getCurrentView().getComponent(),
                            labels.getFormatted("couldntPrint", e)
                            );
                }
            } else {
                System.out.println("JOB ABORTED!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * On Mac OS X with the Quartz rendering engine, the following code achieves
     * the best results.
     */
    public void printQuartz() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(getCurrentView().getComponent());
        final Pageable pageable = (Pageable) Methods.invokeGetter(getCurrentView(), "createPageable", null);
        final double resolution = 300d;
        JobAttributes jobAttr = new JobAttributes();
        PageAttributes pageAttr = new PageAttributes();
        // FIXME - Media type should be retrieved from Locale
        pageAttr.setMedia(PageAttributes.MediaType.A4);
        pageAttr.setPrinterResolution((int) resolution);
        final PrintJob pj = frame.getToolkit().getPrintJob(
                frame,
                "Job Title",
                jobAttr,
                pageAttr
                );
        
        getCurrentView().setEnabled(false);
        new Worker() {
            public Object construct() {
                
                // Compute page format from settings of the print job
                Paper paper = new Paper();
                paper.setSize(
                        pj.getPageDimension().width / resolution * 72d,
                        pj.getPageDimension().height / resolution * 72d);
                paper.setImageableArea(64d,32d,paper.getWidth() - 96d, paper.getHeight() - 64);
                PageFormat pageFormat = new PageFormat();
                pageFormat.setPaper(paper);
                
                // Print the job
                try {
                    for (int i=0, n=pageable.getNumberOfPages(); i < n; i++) {
                        PageFormat pf = pageable.getPageFormat(i);
                        pf = pageFormat;
                        Graphics g = pj.getGraphics();
                        if (g instanceof Graphics2D) {
                            pageable.getPrintable(i).print(g, pf, i);
                        } else {
                            BufferedImage buf = new BufferedImage(
                                    (int) (pf.getImageableWidth() * resolution/ 72d),
                                    (int) (pf.getImageableHeight() * resolution / 72d),
                                    BufferedImage.TYPE_INT_RGB
                                    );
                            Graphics2D bufG = buf.createGraphics();
                            
                            
                            bufG.setBackground(Color.WHITE);
                            bufG.fillRect(0,0,buf.getWidth(),buf.getHeight());
                            bufG.scale(resolution / 72d, resolution / 72d);
                            bufG.translate(-pf.getImageableX(), -pf.getImageableY());
                            pageable.getPrintable(i).print(bufG, pf, i);
                            bufG.dispose();
                            g.drawImage(buf,
                                    (int) (pf.getImageableX() * resolution / 72d),
                                    (int) (pf.getImageableY() * resolution / 72d),
                                    null);
                            buf.flush();
                        }
                        g.dispose();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    pj.end();
                }
                return null;
            }
            public void finished(Object value) {
                getCurrentView().setEnabled(true);
            }
        }.start();
    }
    
}
