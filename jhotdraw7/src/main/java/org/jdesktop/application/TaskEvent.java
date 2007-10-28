
package org.jdesktop.application;

import java.util.EventObject;


/**
 * An encapsulation of the value produced one of the {@code Task} execution
 * methods: {@code doInBackground()}, {@code process}, {@code done}. The source
 * of a {@code TaskEvent} is the {@code Task} that produced the value.
 * 
 * @see TaskListener
 * @see Task
 */
public class TaskEvent<T> extends EventObject {
    private final T value;

    /**
     * Returns the value this event represents. 
     *
     * @return the {@code value} constructor argument.
     */
    public final T getValue() { return value; }


    /**
     * Construct a {@code TaskEvent}.
     * 
     * @param source the {@code Task} that produced the value.
     * @param value the value, null if type {@code T} is {@code Void}.
     */
    public TaskEvent(Task source, T value) { 
	super(source);
	this.value = value; 
    }
}
