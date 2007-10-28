
package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;


/**
 * An encapsulation of the PropertyChangeSupport methods based on 
 * java.beans.PropertyChangeSupport.  PropertyChangeListeners are fired
 * on the event dispatching thread.
 * 
 * <p>
 * Note: this class is only public because the so-called "fix"
 * for javadoc bug 
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4780441">4780441</a>
 * still fails to correctly document public methods inherited from a package
 * private class.  
 */
public class AbstractBean {
    private final PropertyChangeSupport pcs;

    public AbstractBean() {
	pcs = new EDTPropertyChangeSupport(this);
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties and its 
     * {@code propertyChange} method will run on the event dispatching
     * thread.
     * <p>
     * If {@code listener} is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener the PropertyChangeListener to be added.
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#addPropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
  
    /**
     * Remove a PropertyChangeListener from the listener list.
     * <p>
     * If {@code listener} is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener the PropertyChangeListener to be removed.
     * @see #addPropertyChangeListener
     * @see java.beans.PropertyChangeSupport#removePropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  the PropertyChangeListener to be added
     * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If <code>propertyName</code> is null,  no exception is thrown and no
     * action is taken.
     * If <code>listener</code> is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * An array of all of the {@code PropertyChangeListeners} added so far.
     * 
     * @return all of the {@code PropertyChangeListeners} added so far.
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }
  
    /**
     * Called whenever the value of a bound property is set.
     * <p>
     * If oldValue is not equal to newValue, invoke the {@code
     * propertyChange} method on all of the {@code
     * PropertyChangeListeners} added so far, on the event
     * dispatching thread.
     * 
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#firePropertyChange(String, Object, Object)
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    return;
        }
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire an existing PropertyChangeEvent 
     * <p>
     * If the event's oldValue property is not equal to newValue, 
     * invoke the {@code propertyChange} method on all of the {@code
     * PropertyChangeListeners} added so far, on the event
     * dispatching thread.
     * 
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#firePropertyChange(PropertyChangeEvent e)
     */
    protected void firePropertyChange(PropertyChangeEvent e) {
	pcs.firePropertyChange(e);
    }

    private static class EDTPropertyChangeSupport extends PropertyChangeSupport {
	EDTPropertyChangeSupport(Object source) {
	    super(source);
	}
	public void firePropertyChange(final PropertyChangeEvent e) {
	    if (SwingUtilities.isEventDispatchThread()) {
		super.firePropertyChange(e);
	    } 
	    else {
		Runnable doFirePropertyChange = new Runnable() {
		    public void run() {
			firePropertyChange(e);
		    }
		};
		SwingUtilities.invokeLater(doFirePropertyChange);
	    }
	}
    }
}
