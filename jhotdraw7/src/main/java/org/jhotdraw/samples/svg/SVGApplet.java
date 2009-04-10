/*
 * @(#)SVGApplet.java  1.1  2008-05-22
 *
 * Copyright (c) 2006-2008 by the original authors of AnyWikiDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the AnyWikiDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg;

import org.jhotdraw.io.BoundedRangeInputStream;
import java.applet.AppletContext;
import org.jhotdraw.draw.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.samples.svg.io.*;
import org.jhotdraw.samples.svg.gui.*;

/**
 * This is the base class for concrete implementations of SVG drawing
 * applets.
 * <p>
 * The base class loads and saves drawings asynchronously and handles
 * errors. The actual data transmission and the editing of drawings
 * is the responsibility of the subclasses.
 * <p>
 * FIXME - Applet must offer to save file locally, if uploading to server
 * failed.
 * <p>
 * FIXME - Applet must save changes locally and reload them, if the user
 * navigated out of the page and back again, without saving the changes.
 * 
 * @author Werner Randelshofer
 * @version 1.1 2008-05-22 Added Locale parameter.
 * <br>1.0.1 2008-03-26 Fixed ClassCastException when attempting to
 * display an error message in method save(). 
 * <br>1.0 2006-07-08 Created.
 */
public abstract class SVGApplet extends JApplet {

    private DrawingComponent drawingComponent;
    /**
     * Lazily initialized in method getVersion();
     */
    private String version;
    private long start;

    public SVGApplet() {
        setBackground(Color.WHITE);
        start = System.currentTimeMillis();
        //ResourceBundleUtil.setVerbose(true);
    }

    /**
     * Same as <code>Applet.getParameter()</code> but doesn't throw a
     * NullPointerException when used without an Applet context.
     */
    public String getParameter(String name) {
        try {
            return super.getParameter(name);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Same as <code>Applet.getParameter()</code> but doesn't throw a
     * NullPointerException when used without an Applet context.
     */
    public String getParameter(String name, String defaultValue) {
        try {
            String value = super.getParameter(name);
            return (value == null) ? defaultValue : value;
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Displays a progress indicator and then invokes <code>loadDrawing</code>
     * on a worker thread. Displays the drawing panel when finished successfully.
     * Displays an error message when finished unsuccessfully.
     *
     * @see #loadDrawing
     */
    @Override
    public final void init() {
        // set the language of the applet
        if (getParameter("Locale") != null) {
            Locale.setDefault(new Locale(getParameter("Locale")));
        }


        final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

        // Set look and feel
        // -----------------
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            // Do nothing.
            // If we can't set the desired look and feel, UIManager does
            // automaticaly the right thing for us.
        }

        // Display a progress indicator while we are loading the drawing
        // ----------------------------------------------------------
        Container c = getContentPane();
        final ProgressIndicator progress = new ProgressIndicator(
                getName(), labels.getString("progressInitializing"));
        c.add(progress);
        progress.revalidate();

        // Load the drawing using a worker thread
        // --------------------------------------
        new Worker() {

            public Object construct() {
                try {
                    Thread t = new Thread() {

                        public void run() {
                            drawingComponent = createDrawingComponent();
                        }
                    };
                    t.start();
                    progress.setNote(labels.getString("progressLoading"));
                    Object drawing = loadDrawing(progress);
                    progress.setNote(labels.getString("progressOpeningEditor"));
                    progress.setIndeterminate(true);
                    t.join();
                    return drawing;
                } catch (Throwable t) {
                    return t;
                }
            }

            public void finished(Object result) {
                Container c = getContentPane();
                c.setLayout(new BorderLayout());
                c.removeAll();
                if (result instanceof Throwable) {
                    Throwable error = (Throwable) result;
                    error.printStackTrace();
                    String message = (error.getMessage() == null) ? error.toString() : error.getMessage();
                    MessagePanel mp = new MessagePanel(
                            UIManager.getIcon("OptionPane.errorIcon"),
                            labels.getFormatted("messageLoadFailed",  htmlencode(getParameter("DrawingURL")), htmlencode(message)));
                    c.add(mp);
                    mp.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getActionCommand().equals("close")) {
                                close();
                            }
                        }
                    });
                } else {
                    c.add(drawingComponent.getComponent());
                    drawingComponent.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getActionCommand().equals("save")) {
                                save();
                            } else if (evt.getActionCommand().equals("cancel")) {
                                cancel();
                            }
                        }
                    });

                    initComponents();
                    if (result != null) {
                        if (result instanceof Drawing) {
                            setDrawing((Drawing) result);
                        } else if (result instanceof Throwable) {
                            setDrawing(createDrawing());
                            getDrawing().add(new SVGTextFigure(result.toString()));
                            ((Throwable) result).printStackTrace();
                        }
                    }
                }
                c.validate();
                long end = System.currentTimeMillis();
                System.out.println("AbstractDrawingApplet startup latency:" + (end - start));
            }
        }.start();
    }

    /**
     * Sets the drawing on the drawing panel.
     */
    private void setDrawing(Drawing d) {
        drawingComponent.setDrawing(d);
    }

    /**
     * Gets the drawing from the drawing panel.
     */
    private Drawing getDrawing() {
        return drawingComponent.getDrawing();
    }

    /**
     * Gets the version of the applet.
     */
    public String getVersion() {
        if (version == null) {
            BufferedReader r = null;
            try {
                InputStream resource = SVGApplet.class.getResourceAsStream("version.txt");
                r = new BufferedReader(new InputStreamReader(resource, "UTF-8"));
                version = r.readLine();
            } catch (IOException e) {
                version = "unknown";
            } catch (NullPointerException e) {
                version = "unknown";
            } finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (IOException e) {
                        // suppress
                    }
                }
            }
        }
        return version;
    }

    /**
     * Returns information about the applet.
     */
    public String getAppletInfo() {
        return getName() +
                "\nVersion " + getVersion() +
                "\n\nCopyright (c) by the authors of AnyWikiDraw.org" +
                "\nThis software is licensed under LGPL or" +
                "\nCreative Commons 2.5 BY";
    }

    /**
     * Creates the drawing.
     */
    abstract protected Drawing createDrawing();

    /**
     * Creates the drawing component.
     */
    abstract protected DrawingComponent createDrawingComponent();

    /**
     * Returns the drawing component.
     */
    protected DrawingComponent getDrawingComponent() {
        return drawingComponent;
    }

    /**
     * Displays a progress indicator and then invokes <code>saveDrawing</code>
     * on a worker thread. Closes the applet when finished successfully.
     * Displays an error message when finished unsuccessfully.
     *
     * @see #loadDrawing
     */
    final public void save() {
        final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

        Container c = getContentPane();
        c.removeAll();
        final ProgressIndicator progress = new ProgressIndicator(
                "JHotDraw", labels.getString("progressSaving"));
        c.add(progress);
        c.validate();

        // We save the data using a worker thread
        // --------------------------------------
        new Worker() {

            public Object construct() {
                try {
                    saveDrawing(drawingComponent.getDrawing(), progress);
                    return null;
                } catch (Throwable t) {
                    return t;
                }
            }

            public void finished(Object result) {
                if (result instanceof ServerAuthenticationException) {
                    if (showAuthenticationDialog() == JOptionPane.OK_OPTION) {
                        save();
                    } else {
                        Container c = getContentPane();
                        c.removeAll();
                        c.add(drawingComponent.getComponent());
                        c.validate();
                        c.repaint();
                    }
                } else if (result instanceof Throwable) {
                    Throwable error = ((Throwable) result);
                    error.printStackTrace();
                    Container c = getContentPane();
                    c.setLayout(new BorderLayout());
                    c.removeAll();
                    String message = error.getMessage() == null ? error.toString() : error.getMessage();
                    MessagePanel mp = new MessagePanel(
                            UIManager.getIcon("OptionPane.errorIcon"),
                            labels.getFormatted("messageSaveFailed",  htmlencode(getParameter("UploadURL")), htmlencode(message)));
                    c.add(mp);
                    mp.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getActionCommand().equals("close")) {
                                close();
                            }
                        }
                    });
                    c.validate();
                } else {
                    close();
                }
            }
        }.start();
    }

    /**
     * Cancels the applet. Displays a dialog when the drawing contains
     * unsaved changes.
     */
    protected void cancel() {
        // XXX - Display a dialog when the drawing contains unsaved changes.
        close();
    }

    public String[][] getParameterInfo() {
        return new String[][]{
            {"data", "String", "the data to be displayed by this applet."},
            {"datafile", "URL", "an URL to a file containing the data to be displayed by this applet."}};
    }


    /**
     * Loads the drawing.
     * By convention this method is invoked on a worker thread.
     *
     * @param progress A ProgressIndicator to inform the user about the progress
     * of the operation.
     * @return The Drawing that was loaded.
     */
    protected Drawing loadDrawing(ProgressIndicator progress) throws IOException {
        Drawing drawing = createDrawing();
        InputStream in = null;
        try {
            if (getParameter("DrawingURL") != null) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                URL url = new URL(getDocumentBase(), getParameter("DrawingURL"));
                URLConnection uc = url.openConnection();

                // Disable caching. This ensures that we always request the 
                // newest version of the drawing from the server.
                // (Note: The server still needs to set the proper HTTP caching
                // properties to prevent proxies from caching the drawing).
                if (uc instanceof HttpURLConnection) {
                    ((HttpURLConnection) uc).setUseCaches(false);
                }

                // Read the data into a buffer
                int contentLength = uc.getContentLength();
                in = uc.getInputStream();
                if (contentLength != -1) {
                    in = new BoundedRangeInputStream(in);
                    ((BoundedRangeInputStream) in).setMaximum(contentLength + 1);
                    progress.setProgressModel((BoundedRangeModel) in);
                    progress.setIndeterminate(false);
                }
                BufferedInputStream bin = new BufferedInputStream(in);
                bin.mark(512);

                // Read the data using all supported input formats
                // until we succeed
                final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
                IOException formatException = null;
                for (InputFormat format : drawing.getInputFormats()) {
                    try {
                        bin.reset();
                    } catch (IOException e) {
                        uc = url.openConnection();
                        in = uc.getInputStream();
                        in = new BoundedRangeInputStream(in);
                        ((BoundedRangeInputStream) in).setMaximum(contentLength + 1);
                        progress.setProgressModel((BoundedRangeModel) in);
                        bin = new BufferedInputStream(in);
                        bin.mark(512);
                    }
                    try {
                        bin.reset();
                        format.read(bin, drawing, true);
                        formatException = null;
                        break;
                    } catch (IOException e) {
                        formatException = e;
                    }
                }
                if (formatException != null) {
                    throw formatException;
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return drawing;
    }

    /**
     * Saves the drawing.
     * By convention this method is invoked on a worker thread.
     *
     * @param drawing The Drawing to be saved.
     * @param progress A ProgressIndicator to inform the user about the progress
     * of the operation.
     * @throw IOException when an communication error occured
     * @throw ServerAuthenticationException when we couldn't save, because
     * we failed to authenticate. On this exception, the applet should open
     * an authentication dialog, and give the user a second chance to save
     * the drawing.
     */
    protected void saveDrawing(Drawing drawing,
            ProgressIndicator progress) throws IOException, ServerAuthenticationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Determine rendering size
        Dimension renderedSize = new Dimension(-1, -1);
        try {
            renderedSize.width = Integer.parseInt(getParameter("DrawingWidth"));
        } catch (Exception e) {
        }
        try {
            renderedSize.height = Integer.parseInt(getParameter("DrawingHeight"));
        } catch (Exception e) {
        }
        if (renderedSize.width == -1 || renderedSize.height == -1) {
            Rectangle2D.Double drawBounds = null;
            for (Figure f : drawing.getChildren()) {
                if (drawBounds == null) {
                    drawBounds = f.getDrawingArea();
                } else {
                    drawBounds.add(f.getDrawingArea());
                }
            }
            if (renderedSize.width == -1) {
                renderedSize.width = (int) (Math.abs(drawBounds.x) + drawBounds.getWidth());
            }
            if (renderedSize.height == -1) {
                renderedSize.height = (int) (Math.abs(drawBounds.y) + drawBounds.getHeight());
            }
        }
        
        // Write the drawing
        String imageExtension = getParameter("DrawingName");
        imageExtension = (imageExtension == null) ? "" : imageExtension.substring(imageExtension.lastIndexOf('.') + 1);
        if (imageExtension.equals("")) {
            imageExtension = "svg";
        }
        byte[] drawingData = null;
        for (OutputFormat format : drawing.getOutputFormats()) {
            if (imageExtension.equals(format.getFileExtension())) {
                if (format instanceof ImageOutputFormat) {
            ((ImageOutputFormat) format).write(out, drawing, new AffineTransform(), renderedSize);
                } else {
                format.write(out, drawing);
                }
                drawingData = out.toByteArray();
                break;
            }
        }
        if (drawingData == null) {
            throw new IOException("Unsupported file format.");
        }

        // Write a rendered version of the drawing for SVG images
        byte[] renderedData = null;
        byte[] imageMapData = null;
        if (imageExtension.startsWith("svg")) {
            out = new ByteArrayOutputStream();
            ImageOutputFormat imgOut = new ImageOutputFormat();
            imgOut.write(out, drawing, new AffineTransform(), renderedSize);
            renderedData = out.toByteArray();

            out = new ByteArrayOutputStream();
            ImageMapOutputFormat imgMapOut = new ImageMapOutputFormat();
            imgMapOut.write(out, drawing, new AffineTransform(), renderedSize);
            imageMapData = out.toByteArray();
        }

        // Post the data
        HttpURLConnection conn = null;
        BufferedReader response = null;
        try {
            URL url = new URL(getDocumentBase(), getParameter("UploadURL"));
            conn = (HttpURLConnection) url.openConnection();
            ClientHttpRequest request = new ClientHttpRequest(conn);
            request.setParameter("Action", getParameter("UploadAction", ""));
            request.setParameter("UploadSummary", getDrawingComponent().getSummary());
            request.setParameter("DrawingName", getParameter("DrawingName"));
            request.setParameter("DrawingRevision", getParameter("DrawingRevision", ""));
            request.setParameter("DrawingWidth", Integer.toString(renderedSize.width));
            request.setParameter("DrawingHeight", Integer.toString(renderedSize.height));
            request.setParameter("DrawingData", getParameter("DrawingName"),
                    new ByteArrayInputStream(drawingData));
            if (renderedData != null) {
                request.setParameter("RenderedImageData", getParameter("DrawingName") + ".png",
                        new ByteArrayInputStream(renderedData));
            }
            if (imageMapData != null) {
                request.setParameter("ImageMapData", getParameter("DrawingName") + ".map",
                        new ByteArrayInputStream(imageMapData));
            }
            request.post();

            // Read the response
            int responseCode = conn.getResponseCode();
            response = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder responseText = new StringBuilder();
            for (String line; null != (line = response.readLine());) {
                responseText.append(line);
            }
            response.close();
            response = null;
            conn = null;

        } catch (IOException e) {
            if (conn != null) {
                StringBuilder responseText = new StringBuilder();
                try {
                    InputStream errorStream = conn.getErrorStream();
                    if (errorStream == null) {
                        responseText.append(conn.getResponseMessage());
                    } else {
                        response = new BufferedReader(
                                new InputStreamReader(errorStream, "UTF-8"));
                        for (String line; null != (line = response.readLine());) {
                            responseText.append(line);
                        }
                    }
                } finally {
                    if (response != null) {
                        response.close();
                        response = null;
                    }

                }
                if (responseText.length() > 0) {
                    IOException e2 = new IOException(responseText.toString());
                    e2.initCause(e);
                    throw e2;
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Shows an authentication dialog.
     *
     * This is a stub method which always returns JOptionPane.CANCEL_OPTION.
     *
     * @return JOptionPane.OK_OPTION on success, JOptionPane.CANCEL_OPTION,
     * if the user canceled the dialog.
     */
    protected int showAuthenticationDialog() {
        return JOptionPane.CANCEL_OPTION;
    }

    /**
     * Closes the applet. This method can be implemented by invoking
     * <code>getAppletContext().showDocument(...)</code>.
     */
    protected void close() {
        AppletContext appletContext;
        try {
            appletContext = getAppletContext();
        } catch (Throwable e) {
            appletContext = null;
        }
        if (appletContext == null) {
            System.exit(0);
        } else {
            try {
                appletContext.showDocument(new URL(getDocumentBase(), getParameter("PageURL")));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Escapes all '<', '>' and '&' characters in a string.
     * @param str A String.
     * @return HTMlEncoded String.
     */
    private static String htmlencode(String str) {
        if (str == null) {
            return "";
            } else {
        StringBuilder buf = new StringBuilder();
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                case '&':
                    buf.append("&amp;");
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        }
        return buf.toString();
        }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
