/*
 * @(#)MDI_DrawApplication.java 5.2
 *
 */
 
package CH.ifa.draw.contrib;

import javax.swing.*;
import javax.swing.event.*;
import CH.ifa.draw.application.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import java.awt.*;
import java.util.*;

/**
 * Many applications have the ability to deal with multiple internal windows.
 * MDI_DrawApplications provides the basic facilities to make use of MDI in
 * JHotDraw. Its main tasks are to create a content for DrawApplications, which
 * is embedded in internal frames, to maintain a list with all internal frames
 * and to manage the switching between them.
 *
 * @author  Wolfram Kaiser
 * @version JHotDraw 5.2    31.08.1999
 */
public class MDI_DrawApplication extends DrawApplication implements InternalFrameListener {

	/**
	 * Internal frame, which is currently activated. This frame receives all
	 * mouse input and displays the Drawing to be manipulated.
	 */
	private MDI_InternalFrame currentFrame;

	/**
	 * If an internal frame gets activated, the StandardDrawingView is backed
	 * up for later restorage.
	 */
	private StandardDrawingView backupDrawingView;

	/**
	 * This component acts as a desktop for the content.
	 */
	private JComponent desktop;

	/**
	 * List of listeners which adhere to the InternalFrameListener interface
	 */
	private Vector mdiListeners;
	
	/**
	* Constructs a drawing window with a default title.
	*/
	public MDI_DrawApplication() {
		this("JHotDraw");
	}

	/**
	* Constructs a drawing window with the given title.
	*/
	public MDI_DrawApplication(String title) {
		super(title);
		setDesktop(new JDesktopPane());
		getDesktop().setAlignmentX(LEFT_ALIGNMENT);
		mdiListeners = new Vector();
		addInternalFrameListener(this);
	}

    /**
     * Factory method which can be overriden by subclasses to
     * create an instance of their type.
     *
     * @return	newly created application
     */
	protected DrawApplication createApplication() {
		return new MDI_DrawApplication();
	}
	
	/**
	* Creates the contents component of the application
	* frame. By default the DrawingView is returned in
	* a JScrollPane.
	*/
	protected JComponent createContents(StandardDrawingView view) {
		JComponent contents = super.createContents(view);
		MDI_InternalFrame internalFrame = createInternalFrame();
		internalFrame.setDrawingView(view);
		internalFrame.setSize(200, 200);
		internalFrame.getContentPane().add(contents);
		if (currentFrame == null) {
			currentFrame = internalFrame;
			backupDrawingView = createDrawingView();
		}

		// all registered listeners to the new internal frame
		Enumeration enum = mdiListeners.elements();
		while (enum.hasMoreElements()) {
			internalFrame.addInternalFrameListener((InternalFrameListener)enum.nextElement());
		}

		getDesktop().add(internalFrame);
		try {
			internalFrame.setSelected(true);
		}
		catch (java.beans.PropertyVetoException e) {
			// ignore
		}
		internalFrame.setVisible(true);
		// return container in which the internal frame is embedded
		return getDesktop();
	}

	/**
	 * Factory method which creates an internal frame. Subclasses may override this
	 * method to provide their own implementations of MDI_InternalFrame
	 */
	protected MDI_InternalFrame createInternalFrame() {
		return new MDI_InternalFrame("untitled", true, true, true, true);
	}

    /**
     * Resets the drawing to a new empty drawing. If no internal frame
     * exists then open a new internal frame.
     */
    public void promptNew() {
		if (hasInternalFrames()) {
	    	super.promptNew();
    	}
    	else {
    		newWindow();
    	}
    }
    
	/**
	 * Method to create a new internal frame.  Applications that want
	 * to create a new internal drawing view should call this method.
	 */
    public void newWindow() {
        StandardDrawingView fView = createDrawingView();
        Drawing fDrawing = createDrawing();
        fView.setDrawing(fDrawing);
        createContents(fView);
        toolDone();
    }

    public void newView() {
    	String copyTitle = getDrawingTitle();
        StandardDrawingView fView = createDrawingView();
        fView.setDrawing(drawing());
        createContents(fView);
		setDrawingTitle(copyTitle + " (View)");
        toolDone();
    }
    
	/**
	* Set the component, in which the content is embedded. This component
	* acts as a desktop for the content.
	*/
	protected void setDesktop(JComponent newDesktop) {
		desktop = newDesktop;
	}

	/**
	* Get the component, in which the content is embedded. This component
	* acts as a desktop for the content.
	*/
	public JComponent getDesktop() {
		return desktop;
	}

	/**
	 * Add a new listener to the applications internal frames. If a new internal
	 * frame is created, all currently registered InternalFrameListeners are added.
	 *
	 * @param newMDIListener listener to be added
	 */ 
	public void addInternalFrameListener(InternalFrameListener newMDIListener) {
		mdiListeners.addElement(newMDIListener);
	}
	
	/**
	 * Remove a InternalFrameListeners from the application.
	 *
	 * @param oldMDIListener listener to be removed
	 */
	public void removeInternalFrameListener(InternalFrameListener oldMDIListener) {
		mdiListeners.removeElement(oldMDIListener);
	}
	
	/**
	* Activate an internal frame upon which the selected tools operate.
	* The currently activated DrawgingView is backed up for later restorage.
	*/
	public void activateFrame(MDI_InternalFrame newFrame) {
		if (currentFrame != newFrame) {
			// check, whether drawing has been already initialised
			if (newFrame.getDrawingView().drawing() != null) {
				newFrame.getDrawingView().unfreezeView();
			}
			backupDrawingView.setDrawing(view().drawing());
			currentFrame.replaceDrawingView(backupDrawingView);
			currentFrame.validate();
			currentFrame.getDrawingView().freezeView();
			setDrawing(newFrame.getDrawingView().drawing());
			backupDrawingView = newFrame.replaceDrawingView((StandardDrawingView)view());
			currentFrame = newFrame;
		}
	}
		
	/**
	* Notification method from InternalFrameListener, which is called
	* if a internal frame gets selected.
	*/
	public void internalFrameActivated(InternalFrameEvent e) {
		activateFrame((MDI_InternalFrame)e.getSource());
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame is opend.
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * before a internal frame is closed.
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame is closed.
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame gets iconified.
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame gets deiconified.
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
		activateFrame((MDI_InternalFrame)e.getSource());
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame gets deactivated.
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	/**
	 * Set the title for the drawing. The title also appears in the
	 * internal frame title bar. A name is assigned when a drawing
	 * saved or a saved drawing is loaded. The file name is the
	 * drawing title. If the drawing has not been saved before then
	 * the drawing title is "untitled".
	 */
    protected void setDrawingTitle(String newDrawingTitle) {
    	currentFrame.setTitle(newDrawingTitle);
    }

	/**
	 * Get the title for the drawing.
	 */    
    protected String getDrawingTitle() {
    	return currentFrame.getTitle();
    }

	public boolean hasInternalFrames() {
		return ((JDesktopPane)getDesktop()).getAllFrames().length > 0;
	}
}