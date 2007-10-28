
package org.jdesktop.application;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.swingworker.SwingWorker.StateValue;


/**
 * This class is intended to serve as the model for GUI components,
 * like status bars, that display the state of an application's
 * background tasks.  {@code TaskMonitor} provides an overview of all
 * the ApplicationContext's Tasks, as well as the state of a single
 * {@code foreground} Task.  
 * 
 * <p>
 * The value of {@link #getTasks getTasks()} is a list of all of the
 * {@code Tasks} whose state is not {@link
 * Task#isDone DONE} for all of the
 * ApplicationContext's {@code TaskServices}.  In other words: all of
 * the ApplicationContext's background tasks that haven't finished
 * executing.  Each time a new TaskService Task is executed it's added
 * to the list; when the Task finishes it's removed.  Each time the
 * list changes {@code PropertyChangeListeners} are fired.
 * Applications that wish to create a detailed visualization of all
 * Tasks should monitor the TaskMonitor {@code "tasks"} property.
 * 
 * <p>
 * Users are often only interested in the status of a single
 * <i>foreground</i> task, typically the one associated with GUI
 * element they're working with, or with the most recent command
 * they've issued.  The TaskMonitor's PropertyChangeListener is
 * notified each time a property of the {@link #setForegroundTask
 * foregroundTask} changes.  Additionally the TaskMonitor fires
 * synthetic PropertyChangeEvents for properties named "pending", 
 * "started", and "done" when the corresponding Task {@code state}
 * property changes occur.
 * 
 * <p>
 * TaskMonitor manages a queue of new Tasks.  The
 * foregroundTask is automatically set to the first new Task, and when
 * that Task finishes, the next Task in the queue, and so on.
 * Applications can set the foregroundTask explicitly, to better
 * reflect what the user is doing.  For example, a tabbed browsing GUI
 * that launched one Task per tab might set the foreground Task each
 * time the user selected a tab.  To prevent the foregroundTask
 * property from (ever) being reset automatically, one must set {@link
 * #setAutoUpdateForegroundTask autoUpdateForegroundTask} to false.
 * 
 * <p>
 * This class is not thread-safe.  All of its methods must be called 
 * on the event dispatching thread (EDT) and all of its listeners will 
 * run on the EDT.
 * 
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ApplicationContext#getTaskServices
 * @see TaskService#getTasks
 * @see TaskService#execute
 */

public class TaskMonitor extends AbstractBean {
    private final PropertyChangeListener applicationPCL;
    private final PropertyChangeListener taskServicePCL;
    private final PropertyChangeListener taskPCL;
    private final LinkedList<Task> taskQueue;
    private boolean autoUpdateForegroundTask = true;
    private Task foregroundTask = null;

    /**
     * Construct a TaskMonitor.
     */
    public TaskMonitor(ApplicationContext context) {
	applicationPCL = new ApplicationPCL();
	taskServicePCL = new TaskServicePCL();
	taskPCL = new TaskPCL();
	taskQueue = new LinkedList<Task>();
	context.addPropertyChangeListener(applicationPCL);
	for(TaskService taskService : context.getTaskServices()) {
	    taskService.addPropertyChangeListener(taskServicePCL);
	}
    }

    /**
     * The TaskMonitor's PropertyChangeListeners are fired each time 
     * any property of the the {@code foregroundTask} changes.  By
     * default this property is set to the first Task to be executed
     * and then, when that Task finishes, reset to the next most
     * recently executed Task.  If the {@code
     * autoUpdateForegroundTask} is false, then the foregroundTask
     * property is not reset automatically.
     * 
     * @param foregroundTask the task whose properties are reflected by this class
     * @see #setAutoUpdateForegroundTask
     * @see #getForegroundTask
     */
    public void setForegroundTask(Task foregroundTask) {
	final Task oldTask = this.foregroundTask;
	if (oldTask != null) {
	    oldTask.removePropertyChangeListener(taskPCL);
	}
	this.foregroundTask = foregroundTask;
	Task newTask = this.foregroundTask;
	if (newTask != null) {
	    newTask.addPropertyChangeListener(taskPCL);
	}
	firePropertyChange("foregroundTask", oldTask, newTask);
    }

    /**
     * Indicates the {@code Task} whose status the ApplicationContext's GUI wants 
     * to be displayed, typically in the main window's status bar.
     * 
     * 
     * @return the value of the foregroundTask property.
     * @see #setForegroundTask
     */
    public Task getForegroundTask() {
	return foregroundTask;
    }


    /**
     * True if the {@code foregroundTask} property should be automatically
     * reset to the oldest Task in the queue when it finishes running.
     * <p>
     * This property is true by default.
     * 
     * @return true if the foregroundTask should be set automatically.
     * @see #setAutoUpdateForegroundTask
     * @see #setForegroundTask
     */
    public boolean getAutoUpdateForegroundTask() { 
	return autoUpdateForegroundTask;
    }

    /**
     * True if the {@code foregroundTask} property should be automatically
     * reset to the oldest Task in the queue when it finishes running.  An 
     * application that wants explicit control over the Task being monitored
     * can set this property to false.
     * <p>
     * This property is true by default.
     * 
     * @param autoUpdateForegroundTask true if the foregroundTask should be set automatically
     * @see #getAutoUpdateForegroundTask
     */
    public void setAutoUpdateForegroundTask(boolean autoUpdateForegroundTask) {
	boolean oldValue = this.autoUpdateForegroundTask;
	this.autoUpdateForegroundTask = autoUpdateForegroundTask;
	firePropertyChange("autoUpdateForegroundTask", oldValue, this.autoUpdateForegroundTask);
    }

    private List<Task> copyTaskQueue() {
	synchronized(taskQueue) {
	    if (taskQueue.isEmpty()) {
		return Collections.emptyList();
	    }
	    else {
		return new ArrayList<Task>(taskQueue);
	    }
	}
    }

    /**
     * All of the Application Tasks whose {@code state} is not {@code DONE}.
     * <p>
     * Each time the list of Tasks changes, a PropertyChangeEvent for the
     * property named "tasks" is fired.  Applications that want to monitor all
     * background Tasks should monitor the tasks property.
     * 
     * @return a list of all Tasks that aren't {@code DONE}
     */
    public List<Task> getTasks() {
	return copyTaskQueue();
    }

    /* Called on the EDT, each time a TaskService's list of tasks changes,
     * i.e. each time a new Task is executed and each time a Task's
     * state changes to DONE.
     */
    private void updateTasks(List<Task> oldTasks, List<Task> newTasks) {
	boolean tasksChanged = false;  // has the "tasks" property changed?
	List<Task> oldTaskQueue = copyTaskQueue();
	// Remove each oldTask that's not in the newTasks list from taskQueue
	for(Task oldTask : oldTasks) {
	    if (!(newTasks.contains(oldTask))) {
		if (taskQueue.remove(oldTask)) {
		    tasksChanged = true;
		}
	    }
	}
	// Add each newTask that's not in the oldTasks list to the taskQueue
	for(Task newTask : newTasks) {
	    if (!(taskQueue.contains(newTask))) {
		taskQueue.addLast(newTask);
		tasksChanged = true;
	    }
	}
	// Remove any tasks that are DONE for the sake of tasksChanged
	Iterator<Task> tasks = taskQueue.iterator();
	while(tasks.hasNext()) {
	    Task task = tasks.next();
	    if (task.isDone()) {
		tasks.remove();
		tasksChanged = true;
	    }
	}
	// Maybe fire the "tasks" PCLs
	if (tasksChanged) {
	    List<Task> newTaskQueue = copyTaskQueue();
	    firePropertyChange("tasks", oldTaskQueue, newTaskQueue);
	}

	if (autoUpdateForegroundTask && (getForegroundTask() == null)) {
	    setForegroundTask(taskQueue.isEmpty() ? null : taskQueue.getLast());
	}
    }


    /* Each time an ApplicationContext TaskService is added or removed, we 
     * remove our taskServicePCL from the old ones, add it to the new
     * ones.  In a typical application, this will happen infrequently
     * and the number of TaskServices will be small, often just one.
     * This listener runs on the EDT.
     */
    private class ApplicationPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ("taskServices".equals(propertyName)) {
		List<TaskService> oldList = (List<TaskService>)e.getOldValue();
		List<TaskService> newList = (List<TaskService>)e.getNewValue();
		for(TaskService oldTaskService : oldList) {
		    oldTaskService.removePropertyChangeListener(taskServicePCL);
		}
		for(TaskService newTaskService : newList) {
		    newTaskService.addPropertyChangeListener(taskServicePCL);
		}
	    }
	}
    }

    /* Each time a TaskService's list of Tasks (the "tasks" property) changes,
     * update the taskQueue (the "tasks" property) and possibly the 
     * foregroundTask property.  See updateTasks().
     * This listener runs on the EDT.
     */
    private class TaskServicePCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ("tasks".equals(propertyName)) {
		List<Task> oldList = (List<Task>)e.getOldValue();
		List<Task> newList = (List<Task>)e.getNewValue();
		updateTasks(oldList, newList);
	    }
	}
    }

    /* Each time a property of the foregroundTask that's also a 
     * TaskMonitor property changes, update the TaskMonitor's state
     * and fire a TaskMonitor ProprtyChangeEvent.  
     * This listener runs on the EDT.
     */
    private class TaskPCL implements PropertyChangeListener {
	private void fireStateChange(Task task, String propertyName) {
	    firePropertyChange(new PropertyChangeEvent(task, propertyName, false, true));
	}
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    Task task = (Task)(e.getSource());
	    Object newValue = e.getNewValue();
	    if ((task != null) && (task == getForegroundTask())) {
		firePropertyChange(e);
		if ("state".equals(propertyName)) {
		    StateValue newState = (StateValue)(e.getNewValue());
		    switch(newState) {
		    case PENDING: fireStateChange(task, "pending"); break;
		    case STARTED: fireStateChange(task, "started"); break;
		    case DONE: 
			fireStateChange(task, "done");
			setForegroundTask(null);
		    }
		}
	    }
	}
    }
}
