/*
 * @(#)MDIDesktopPane.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * An extension of WDesktopPane that supports often used MDI functionality. This
 * class also handles setting scroll bars for when windows move too far to the left or
 * bottom, providing the MDIDesktopPane is in a ScrollPane.
 *
 * @author Wolfram Kaiser (adapted from an article in JavaWorld)
 * @version <$CURRENT_VERSION$>
 */
public class MDIDesktopPane extends JDesktopPane {
	private static int FRAME_OFFSET=20;
	private MDIDesktopManager manager;

	public MDIDesktopPane() {
		manager=new MDIDesktopManager(this);
		setDesktopManager(manager);
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}

/*	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x,y,w,h);
		checkDesktopSize();
	}
*/
	public Component add(JInternalFrame frame) {
		JInternalFrame[] array = getAllFrames();
		Point p;
		int w;
		int h;

		Component retval = super.add(frame);
		checkDesktopSize();
		if (array.length > 0) {
			p = array[0].getLocation();
			p.x = p.x + FRAME_OFFSET;
			p.y = p.y + FRAME_OFFSET;
		}
		else {
			p = new Point(0, 0);
		}
		frame.setLocation(p.x, p.y);
		if (frame.isResizable()) {
			w = getWidth() - (getWidth() / 3);
			h = getHeight() - (getHeight() / 3);
			if (w < frame.getMinimumSize().getWidth()) {
				w = (int)frame.getMinimumSize().getWidth();
			}
			if (h < frame.getMinimumSize().getHeight()) {
				h = (int)frame.getMinimumSize().getHeight();
			}
			frame.setSize(w, h);
		}
		moveToFront(frame);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		}
		catch (PropertyVetoException e) {
			frame.toBack();
		}
		return retval;
	}

	public void remove(Component c) {
		super.remove(c);
		checkDesktopSize();
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames() {
		int x = 0;
		int y = 0;
		JInternalFrame allFrames[] = getAllFrames();

		manager.setNormalSize();
		int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
		int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--) {
			try {
				((JInternalFrame)allFrames[i]).setMaximum(false);
			}
			catch (PropertyVetoException e) {
				e.printStackTrace();
			}

			allFrames[i].setBounds(x, y, frameWidth, frameHeight);
			x = x + FRAME_OFFSET;
			y = y + FRAME_OFFSET;
		}

		checkDesktopSize();
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames() {
		java.awt.Component allFrames[] = getAllFrames();
		manager.setNormalSize();
		int frameHeight = getBounds().height/allFrames.length;

		int y = 0;
		for (int i = 0; i < allFrames.length; i++) {
			try {
				((JInternalFrame)allFrames[i]).setMaximum(false);
			}
			catch (PropertyVetoException e) {
				e.printStackTrace();
			}

			allFrames[i].setBounds(0, y, getBounds().width,frameHeight);
			y = y + frameHeight;
		}

		checkDesktopSize();
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred)
	 * to the given dimension.
	 */
	public void setAllSize(Dimension d){
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		setBounds(0, 0, d.width, d.height);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred)
	 * to the given width and height.
	 */
	public void setAllSize(int width, int height){
		setAllSize(new Dimension(width,height));
	}

	private void checkDesktopSize() {
		if (getParent()!=null&&isVisible()) manager.resizeDesktop();
	}
}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 */
class MDIDesktopManager extends DefaultDesktopManager {
	private MDIDesktopPane desktop;

	public MDIDesktopManager(MDIDesktopPane desktop) {
		this.desktop = desktop;
	}

	public void endResizingFrame(JComponent f) {
		super.endResizingFrame(f);
		resizeDesktop();
	}

	public void endDraggingFrame(JComponent f) {
		super.endDraggingFrame(f);
		resizeDesktop();
	}

	public void setNormalSize() {
		JScrollPane scrollPane = getScrollPane();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
			   d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
						 d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			desktop.setAllSize(d);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	private Insets getScrollPaneInsets() {
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane == null) {
			return new Insets(0, 0, 0, 0);
		}
		else return getScrollPane().getBorder().getBorderInsets(scrollPane);
	}

	public JScrollPane getScrollPane() {
		if (desktop.getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport)desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane)viewPort.getParent();
		}
		return null;
	}

	protected void resizeDesktop() {
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane();
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			JInternalFrame allFrames[] = desktop.getAllFrames();
			for (int i = 0; i < allFrames.length; i++) {
				if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
					x = allFrames[i].getX() + allFrames[i].getWidth();
				}
				if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
					y = allFrames[i].getY() + allFrames[i].getHeight();
				}
			}
			Dimension d=scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
			   d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
						 d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth()) {
				x = ((int)d.getWidth()) - 20;
			}
			if (y <= d.getHeight()) {
				y = ((int)d.getHeight()) - 20;
			}
			desktop.setAllSize(x,y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}
}
