/*
 * @(#)MDI_DrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.application.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import javax.swing.*;
import javax.swing.event.*;
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
 * @version <$CURRENT_VERSION$>
 */
public class MDI_DrawApplication extends DrawApplication implements InternalFrameListener {

	/**
	 * Internal frame, which is currently activated. This frame receives all
	 * mouse input and displays the Drawing to be manipulated.
	 */
	private MDI_InternalFrame currentFrame;

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
		setDesktop(new MDIDesktopPane());
		getDesktop().setAlignmentX(JComponent.LEFT_ALIGNMENT);
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
	 * Creates the tools. By default only the selection tool is added.
	 * Override this method to add additional tools.
	 * Call the inherited method to include the selection tool.
	 * @param palette the palette where the tools are added.
	 */
	protected void createTools(JToolBar palette) {
		super.createTools(palette);
		Tool tool = new DragNDropTool( this );
		ToolButton tb = createToolButton(IMAGES+"SEL", "Drag N Drop Tool", tool);
		palette.add( tb );
	}
	/**
	* Creates the contents component of the application
	* frame. By default the DrawingView is returned in
	* a JScrollPane.
	*/
	protected JComponent createContents(DrawingView view) {
		if (view.isInteractive()) {
			MDI_InternalFrame internalFrame = createInternalFrame(view);
			JComponent contents = super.createContents(view);
			internalFrame.getContentPane().add(contents);
			getDesktop().add(internalFrame);
			internalFrame.setVisible(true);
			try {
				internalFrame.setSelected(true);
			}
			catch (java.beans.PropertyVetoException e) {
				// ignore
			}
		}
		// return container in which the internal frame is embedded
		return getDesktop();

	}

	/**
	 * Factory method which creates an internal frame. Subclasses may override this
	 * method to provide their own implementations of MDI_InternalFrame
	 */
	protected MDI_InternalFrame createInternalFrame(DrawingView view) {
		String applicationTitle = null;
		if ((view == null) || (view.drawing() == null) || (view.drawing().getTitle() == null)) {
			applicationTitle =  getApplicationName() + " - " + getDefaultDrawingTitle();
		}
		else {
			applicationTitle =  getApplicationName() + " - " + view.drawing().getTitle();
		}
		MDI_InternalFrame internalFrame = new MDI_InternalFrame(applicationTitle, true, true, true, true);
		internalFrame.setDrawingView(view);
		internalFrame.setSize(200, 200);

		// all registered listeners to the new internal frame
		Enumeration enum = mdiListeners.elements();
		while (enum.hasMoreElements()) {
			internalFrame.addInternalFrameListener((InternalFrameListener)enum.nextElement());
		}

		fireViewCreatedEvent(view); // frame now has connection all the way to heavyweight component

		// return container in which the internal frame is embedded
		return internalFrame;
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
			newWindow(createDrawing());
		}
	}

	/**
	 * Method to create a new internal frame.  Applications that want
	 * to create a new internal drawing view should call this method.
	 */
	public void newWindow(Drawing newDrawing) {
		DrawingView newView = createDrawingView();
		newView.setDrawing(newDrawing);
		createContents(newView);
		toolDone();
	}

	protected DrawingView createInitialDrawingView() {
		return NullDrawingView.getManagedDrawingView(this);
	}

	public void newView() {
		if (!view().isInteractive()) {
			return;
		}
		String copyTitle = view().drawing().getTitle();
		DrawingView fView = createDrawingView();
		fView.setDrawing( view().drawing() );
		createContents(fView);
		if(copyTitle != null ) {
			setDrawingTitle(copyTitle + " (View)");
		}
		else {
			setDrawingTitle( getDefaultDrawingTitle() + " (View)");
		}
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
	 * frame is created, all currently registered InternalFrameListeners are
	 * added as listeners to that internal frame as well.
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
			if(currentFrame != null )
			{
				currentFrame.getDrawingView().freezeView();
				currentFrame.getDrawingView().clearSelection();
			}
			currentFrame = newFrame;
		}
		setView( currentFrame.getDrawingView() );
	}
	/**
	 * If the frame we are deactivating is the current frame, set the
	 * currentFrame to null
	 */
	public void deactivateFrame(MDI_InternalFrame frame) {
		if( currentFrame == frame ) {
			currentFrame = null;
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
		MDI_InternalFrame mdf = (MDI_InternalFrame)e.getSource();
		DrawingView dv = mdf.getDrawingView();
		fireViewDestroyingEvent( dv );
		if( mdf == currentFrame) {
			currentFrame = null;
			setView(NullDrawingView.getManagedDrawingView(this));
		}
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
		//activateFrame((MDI_InternalFrame)e.getSource());
	}

	/**
	 * Notification method from InternalFrameListener, which is called
	 * if a internal frame gets deactivated.
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		deactivateFrame((MDI_InternalFrame)e.getSource());
	}

	/**
	 * Set the title for the drawing. The title also appears in the
	 * internal frame title bar. A name is assigned when a drawing
	 * saved or a saved drawing is loaded. The file name is the
	 * drawing title. If the drawing has not been saved before then
	 * the drawing title is "untitled".
	 */
	protected void setDrawingTitle(String newDrawingTitle) {
		currentFrame.setTitle( getApplicationName() + " - " + newDrawingTitle );
	}

	/**
	 * Get the title for the drawing.
	 */
	protected String getDrawingTitle() {
		return currentFrame.getDrawing().getTitle();
	}

	public boolean hasInternalFrames() {
		return ((JDesktopPane)getDesktop()).getAllFrames().length > 0;
	}

	/**
	 * Returns all the views in the application
	 */
	public DrawingView[] views() {
		DrawingView[] views;
		ArrayList frames = new ArrayList();

		JInternalFrame[] ifs = ((JDesktopPane)getDesktop()).getAllFrames();
		for(int x=0; x < ifs.length ; x++) {
			if( MDI_InternalFrame.class.isInstance( ifs[x] ) ) {
				DrawingView dv = ((MDI_InternalFrame)ifs[x]).getDrawingView();
				if( DrawingView.class.isInstance( dv ) ) {
					frames.add( dv );
				}
			}
	   }
		views = new DrawingView[ frames.size() ];
		frames.toArray( views );

		return views;
	}
}
