
package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;


public class TaskService extends AbstractBean {
    private final String name;
    private final ExecutorService executorService;
    private final List<Task> tasks;
    private final PropertyChangeListener taskPCL;

    public TaskService(String name, ExecutorService executorService) {
	if (name == null) {
	    throw new IllegalArgumentException("null name");
	}
	if (executorService == null) {
	    throw new IllegalArgumentException("null executorService");
	}
	this.name = name;
	this.executorService = executorService;
	this.tasks = new ArrayList<Task>();
	this.taskPCL = new TaskPCL();
    }

    public TaskService(String name) {
	this(name, new ThreadPoolExecutor(
            3,   // corePool size
            10,  // maximumPool size
	    1L,  TimeUnit.SECONDS,  // non-core threads time to live
            new LinkedBlockingQueue<Runnable>())); 
    }

    public final String getName() {
	return name;
    }

    private List<Task> copyTasksList() {
	synchronized(tasks) {
	    if (tasks.isEmpty()) {
		return Collections.emptyList();
	    }
	    else {
		return new ArrayList<Task>(tasks);
	    }
	}
    }

    private class TaskPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ("done".equals(propertyName)) {
		Task task = (Task)(e.getSource());
		if (task.isDone()) {
		    List<Task> oldTaskList, newTaskList;
		    synchronized(tasks) {
			oldTaskList = copyTasksList();
			tasks.remove(task); 
			task.removePropertyChangeListener(taskPCL);
			newTaskList = copyTasksList();
		    }
		    firePropertyChange("tasks", oldTaskList, newTaskList);
                    Task.InputBlocker inputBlocker = task.getInputBlocker();
                    if (inputBlocker != null) {
                        inputBlocker.unblock();
                    }
		}
	    }
	}
    }

    private void maybeBlockTask(Task task) {
        final Task.InputBlocker inputBlocker = task.getInputBlocker();
        if (inputBlocker == null) {
            return;
        }
        if (inputBlocker.getScope() != Task.BlockingScope.NONE) {
            if (SwingUtilities.isEventDispatchThread()) {
		inputBlocker.block();
	    } 
	    else {
		Runnable doBlockTask = new Runnable() {
		    public void run() {
                        inputBlocker.block();
		    }
		};
		SwingUtilities.invokeLater(doBlockTask);
	    }
        }
    }

    public void execute(Task task) {
	if (task == null) {
	    throw new IllegalArgumentException("null task");
	}
	if (!task.isPending() || (task.getTaskService() != null)) {
	    throw new IllegalArgumentException("task has already been executed");
	}
	task.setTaskService(this);
	// TBD: what if task has already been submitted?
	List<Task> oldTaskList, newTaskList;
	synchronized(tasks) {
	    oldTaskList = copyTasksList();
	    tasks.add(task);
	    newTaskList = copyTasksList();
	    task.addPropertyChangeListener(taskPCL);
	}
	firePropertyChange("tasks", oldTaskList, newTaskList);
        maybeBlockTask(task);
	executorService.execute(task);
    }

    public List<Task> getTasks() {
	return copyTasksList();
    }

    public final void shutdown() {
	executorService.shutdown();	
    }

    public final List<Runnable> shutdownNow() {
	return executorService.shutdownNow();
    }

    public final boolean isShutdown() {
	return executorService.isShutdown();
    }

    public final boolean isTerminated() {
	return executorService.isTerminated();
    }

    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
	return executorService.awaitTermination(timeout, unit);
    }
}
