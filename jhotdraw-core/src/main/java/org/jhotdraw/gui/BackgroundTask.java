/*
 * @(#)BackgroundTask.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.gui;

import edu.umd.cs.findbugs.annotations.Nullable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This is an abstract class that you can subclass to
 * perform GUI-related work in a dedicated event dispatcher.
 * <p>
 * This class is similar to SwingWorker but less complex.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class BackgroundTask implements Runnable {
    private Throwable error;  // see getError(), setError()

    /**
     * Calls #construct on the current thread and invokes
     * #done on the AWT event dispatcher thread.
     */
    @Override
    public final void run() {
        try {
            construct();
        } catch (Throwable e) {
            setError(e);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    failed(getError());
                    finished();
                }
            });
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    done();
                } finally {
                    finished();
                }
            }
        });
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    @Nullable
    protected abstract void construct() throws Exception;

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned without throwing
     * an error.
     * <p>
     * The default implementation does nothing. Subclasses may override this
     * method to perform done actions on the Event Dispatch Thread.
     */
    protected void done() {
    }

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has thrown an error.
     * <p>
     * The default implementation prints a stack trace. Subclasses may override
     * this method to perform failure actions on the Event Dispatch Thread.
     *
     * @param error The error thrown by construct.
     */
    protected void failed(Throwable error) {
        JOptionPane.showMessageDialog(null, error.getMessage()==null?error.toString():error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        error.printStackTrace();
    }

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has finished and after
     * done() or failed() has been invoked.
     * <p>
     * The default implementation does nothing. Subclasses may override this
     * method to perform completion actions on the Event Dispatch Thread.
     */
    protected void finished() {
    }

    /**
     * Get the error produced by the worker thread, or null if it
     * hasn't thrown one.
     */
    protected synchronized Throwable getError() {
        return error;
    }

    /**
     * Set the error thrown by constrct.
     */
    private synchronized void setError(Throwable x) {
        error = x;
    }

    /**
     * Starts the Worker on an internal worker thread.
     */
    public void start() {
        new Thread(this).start();
    }
}
