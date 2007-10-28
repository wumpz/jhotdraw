/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.util.List;


/**
 * Listener used for observing {@code Task} execution.  
 * A {@code TaskListener} is particularly 
 * useful for monitoring the the intermediate results 
 * {@link Task#publish published} by a Task in situations
 * where it's not practical to override the Task's 
 * {@link Task#process process} method.  Note that if 
 * what you really want to do is monitor a Task's state 
 * and progress, a PropertyChangeListener is probably more
 * appropriate.
 * <p>
 * The Task class runs all TaskListener methods on the event dispatching
 * thread and the source of all TaskEvents is the Task object.
 * 
 * @see Task#addTaskListener
 * @see Task#removeTaskListener
 * @see Task#addPropertyChangeListener
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public interface TaskListener<T, V> {

    /**
     * Called just before the Task's {@link Task#doInBackground
     * doInBackground} method is called, i.e. just before the task
     * begins running.  The {@code event's} source is the Task and its
     * value is null.
     * 
     * @param event a TaskEvent whose source is the {@code Task} object, value is null
     * @see Task#doInBackground
     * @see TaskEvent#getSource
     */
    void doInBackground(TaskEvent<Void> event);

    /**
     * Called each time the Task's {@link Task#process process} method is called.
     * The value of the event is the list of values passed to the process method.  
     * 
     * @param event a TaskEvent whose source is the {@code Task} object and whose
     *     value is a list of the values passed to the {@code Task.process()} method
     * @see Task#doInBackground
     * @see Task#process
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void process(TaskEvent<List<V>> event);

    /**
     * Called after the Task's {@link Task#succeeded succeeded}
     * completion method is called.  The event's value is the value
     * returned by the Task's {@code get} method, i.e. the value that
     * is computed by {@link Task#doInBackground}.
     *
     * @param event a TaskEvent whose source is the {@code Task} object, and 
     *     whose value is the value returned by {@code Task.get()}.
     * @see Task#succeeded
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void succeeded(TaskEvent<T> event);

    /**
     * Called after the Task's {@link Task#failed failed} completion
     * method is called.  The event's value is the Throwable passed to
     * {@code Task.failed()}.
     *
     * @param event a TaskEvent whose source is the {@code Task} object, and 
     *     whose value is the Throwable passed to {@code Task.failed()}.
     * @see Task#failed
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void failed(TaskEvent<Throwable> event);

    /**
     * Called after the Task's {@link Task#cancelled cancelled} method
     * is called.  The {@code event's} source is the Task and its
     * value is null.
     *
     * @param event a TaskEvent whose source is the {@code Task} object, value is null
     * @see Task#cancelled
     * @see Task#get
     * @see TaskEvent#getSource
     */
    void cancelled(TaskEvent<Void> event);

    /**
     * Called after the Task's {@link Task#interrupted interrupted} method is called.
     * The {@code event's} source is the Task and its value is
     * the InterruptedException passed to {@code Task.interrupted()}.
     *
     * @param event a TaskEvent whose source is the {@code Task} object, and 
     *     whose value is the InterruptedException passed to {@code Task.interrupted()}.
     * @see Task#interrupted
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void interrupted(TaskEvent<InterruptedException> event);

    /**
     * Called after the Task's {@link Task#finished finished} method is called.
     * The {@code event's} source is the Task and its value is null.
     *
     * @param event a TaskEvent whose source is the {@code Task} object, value is null.
     * @see Task#interrupted
     * @see TaskEvent#getSource
     */
    void finished(TaskEvent<Void> event);

    /** 
     * Convenience class that stubs all of the TaskListener interface
     * methods.  Using TaskListener.Adapter can simplify building 
     * TaskListeners:
     * <pre>
     * </pre>
     */
    class Adapter<T, V> implements TaskListener<T, V> {
	public void doInBackground(TaskEvent<Void> event) {}
	public void process(TaskEvent<List<V>> event) {}
	public void succeeded(TaskEvent<T> event) {}
	public void failed(TaskEvent<Throwable> event) {}
	public void cancelled(TaskEvent<Void> event) {}
	public void interrupted(TaskEvent<InterruptedException> event) {}
	public void finished(TaskEvent<Void> event) {}
    }
}
