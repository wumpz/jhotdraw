/*
 * @(#)OffsetConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.standard;

import java.awt.*;
import org.jhotdraw.framework.*;
import org.jhotdraw.figures.*;
import org.jhotdraw.util.*;

/**
 * An OffsetConnector locates connection points with the help of an
 * OffsetLocator.
 * <p>
 * It allows the dynamic creation of connection points for new LineConnections.
 * <p>
 * It dynamically adjusts connection points when connection handles are
 * dragged.
 * <p>
 * This class is not thread safe
 * 
 * <hr>
 * <b>Design Patterns </b>
 * <P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o "><b><a
 * href=../pattlets/sld036.htm>Proxy </a> </b> <br>
 * <b><a href=../pattlets/sld036.htm>Prototype </a> </b> <br>
 * Tracking connectors are Proxy Objects that are provided by the
 * trackConnector method when new connectors need to be created. The tracking
 * connectors permit deferral of OffsetConnector creation until the
 * finalizeConnector() method is called. New connectors are then created by
 * copying the tracking connectors (as in the Prototype pattern).
 * <hr>
 * 
 * @see OffsetLocator
 * @see Connector
 */
public class OffsetConnector extends LocatorConnector {

	// Static trackingConnectors are used to minimize object creation.
	// A static trackingPoint minimizes Point creation.
	static public OffsetConnector trackingConnector1 = new OffsetConnector();
	static public OffsetConnector trackingConnector2 = new OffsetConnector();
	static private Point trackingPoint = new Point();

	// variables used in trackConnector() to control what trackingConnector to
	// use
	static private OffsetConnector firstConnector;
	static private OffsetConnector lastConnector;

	// need GridConstraint of view
	static private DrawingView view;

	// The variable fOwnerBox preserves the owner's previous display box;
	// it is used to maintain line orientations during resizing and to
	// minimize Rectangle creation.
	private transient Rectangle fOwnerBox;

	/**
	 * Called when a ConnectionTool starts a new connection. (ConnectionTool
	 * MouseDown event).
	 * 
	 * @param drawingView -
	 *            the current DrawingView; needed for it's GridConstrainer
	 */
	public void reset(DrawingView drawingView) {
		if (this == trackingConnector1) {
			view = drawingView;
			firstConnector = null;
			lastConnector = null;
			trackingConnector1.fOwner = null;
			trackingConnector2.fOwner = null;
		}
	}

	/**
	 * Use this method to create new OffsetConnectors.
	 * 
	 * <p>
	 * Returns a tracking Connector initialized to the required owner and
	 * location. The trackingConnector will create a new connector when it's
	 * method finalizeConnector() is called.
	 * 
	 * <p>
	 * This method depends on trackingConnector1.reset() resetting the
	 * trackingConnectors
	 * 
	 * @see finalizeConnector(boolean start)
	 * 
	 * @param owner - the owning figure
	 * @param x - x co-ordinate
	 * @param y - y co-ordinate
	 * @return - either trackingConnector1 or trackingConnector2 (if 1 is in use)
	 */
	static public OffsetConnector trackConnector(Figure owner, int x, int y) {
		OffsetConnector trackingConnector = trackingConnector1;
		//This method depends on reset() nullifying firstConnector and
		//lastConnector. It also depends on the tracking connector's owner
		//being set to null in reset(). Otherwise fOwner and fOwnerBox would
		// have
		// to be set unconditionally and would then create a Rectangle on every
		// call.
		if (firstConnector != null && owner != trackingConnector1.owner()) {
			trackingConnector = trackingConnector2;
		}

		if (trackingConnector.fOwner != owner) {
			trackingConnector.fOwner = owner;
			trackingConnector.fOwnerBox = owner.displayBox();
		}
		if (firstConnector == null) {
			firstConnector = trackingConnector;
		}
		lastConnector = trackingConnector;
		return trackingConnector.calculateFigureConstrainedOffsets(x, y);
	}

	/**
	 * Constructs a connector that has no owner. It is used internally to
	 * resurrect a connector from a StorableOutput and to create the static
	 * tracking connectors.
	 */
	public OffsetConnector() {
		OffsetLocator loc = new OffsetLocator(RelativeLocator.northWest());
		myLocator = loc;
		fOwner = null;
		fOwnerBox = new Rectangle();
		//System.out.println("OffsetConnector()-Tracking Only");
	}

	/**
	 * Constructs an OffsetConnector with the given owner and given location.
	 * It is called only by the finalizeConnector method;
	 * 
	 * @param owner
	 * @param offsetX
	 * @param offsetY
	 */
	private OffsetConnector(Figure owner, int offsetX, int offsetY) {
		super(owner, null);
		OffsetLocator loc = new OffsetLocator(RelativeLocator.northWest(), offsetX, offsetY);
		myLocator = loc;
		fOwnerBox = owner.displayBox();
		//System.out.println("OffsetConnector(" + owner.toString()+","+offsetX
		// +","+offsetY+")");
	}

	/**
	 * Returns a newly created OffsetConnector for tracking connectors. The
	 * tracking connector's owner and offsets are copied to the new connector.
	 * <p>
	 * Existing non-tracking connectors are returned unchanged without side
	 * effects.
	 * <p>
	 * This method is called by the connectStart(Connector) and the
	 * connectEnd(Connector) methods of the LineConnection object
	 * 
	 * @see LineConnection
	 * 
	 * @param start -
	 *            a boolean indicating whether the receiver is a start or end
	 *            Connector
	 * @return - the receiver unchanged if it is not a tracking connector; a
	 *         new Offset connector if this is a tracking connector.
	 */
	public Connector finalizeConnector(boolean start) {
		if ((this != OffsetConnector.trackingConnector1)
				&& (this != OffsetConnector.trackingConnector2)) {
			return this;
		}
		OffsetLocator l = (OffsetLocator) myLocator;
		OffsetConnector o = new OffsetConnector(owner(), l.fOffsetX, l.fOffsetY);

		// an adjustment to the end connector that helps draw vertical or
		// horizontal lines.
		// This adjustment applies only to the initial rendering of the line
		// and has no
		// subsequent effect
		// N.B. trackingConnector2 is used iff 2 connectors needed ... new line
		// connection
		if (this == OffsetConnector.trackingConnector2) {
			int p1X = trackingConnector1.locateX();
			int p1Y = trackingConnector1.locateY();
			int p2X = locateX();
			int p2Y = locateY();
			if (Math.abs(p1X - p2X) <= 8) p2X = p1X;
			if (Math.abs(p1Y - p2Y) <= 8) p2Y = p1Y;
			l = (OffsetLocator) o.myLocator;
			l.fOffsetX = Geom.range(0, fOwnerBox.width, p2X - fOwnerBox.x);
			l.fOffsetY = Geom.range(0, fOwnerBox.height, p2Y - fOwnerBox.y);
		}
		return o;
	}

	/**
	 * Resets offsets for an existing OffsetConnector. Called when dragging a
	 * ChangeConnectionHandle.
	 * 
	 * @see org.jhotdraw.standard.ChangeConnectionHandle
	 * 
	 * @param x -
	 *            x coordinate of point moved to
	 * @param y -
	 *            y coordinate of point moved to
	 * @see org.jhotdraw.framework.Connector#connectorMovedTo(int, int)
	 */
	public Point connectorMovedTo(int x, int y) {
		calculateFigureConstrainedOffsets(x, y);

		// adjustment to make it easier for user to position point
		// will use x or y parameters under certain conditions overriding
		// calculated point
		// only applies to sides of figure & the adjusted point will still lie
		// on appropriate side
		int px = locateX();
		int py = locateY();
		OffsetLocator l = (OffsetLocator) myLocator;
		if (owner() instanceof RectangleFigure) {
			if (Math.abs(py - y) <= 3) {
				if (l.fOffsetX == 0 || l.fOffsetX == fOwnerBox.width) {
					// can use y
					l.fOffsetY = Geom.range(0, fOwnerBox.height, y - fOwnerBox.y);
				}
			}
			if (Math.abs(px - x) <= 3) {
				if (l.fOffsetY == 0 || l.fOffsetY == fOwnerBox.height) {
					// can use x
					l.fOffsetX = Geom.range(0, fOwnerBox.width, x - fOwnerBox.x);
				}
			}
		}
		return new Point(locateX(), locateY());
	}

	/**
	 * Gets the connection point. If the owner is resized the connection points
	 * are (visually) preserved provided they lie on the box of the resized
	 * figure.
	 * 
	 * @see org.jhotdraw.standard.AbstractConnector#findPoint(org.jhotdraw.framework.ConnectionFigure)
	 */
	protected Point findPoint(ConnectionFigure connection) {
		Rectangle r = owner().displayBox();
		if (fOwnerBox.width == 0 && fOwnerBox.height == 0) {
			// for deSerialization
			fOwnerBox = r;
		}
		OffsetLocator l = (OffsetLocator) myLocator;
		Point p1 = locate(connection);

		// if not resized then no adjustments needed
		if (fOwnerBox.width == r.width && fOwnerBox.height == r.height) {
			fOwnerBox = r;
			//System.out.println("findPoint - " +this.toString() +":"+
			// p1.toString());
			return p1;
		}

		// ????
		if (owner() instanceof EllipseFigure) {
			calculateFigureConstrainedOffsets(p1.x, p1.y);
			fOwnerBox = r;
			return p1;
		}
		//get the point (use previous box with offsets)
		p1.x = fOwnerBox.x + l.fOffsetX;
		p1.y = fOwnerBox.y + l.fOffsetY;

		if (l.fOffsetX == 0) {
			p1.x = r.x;
		}
		else if (l.fOffsetX == fOwnerBox.width) {
			p1.x = r.x + r.width;
		}

		if (l.fOffsetY == 0) {
			p1.y = r.y;
		}
		else if (l.fOffsetY == fOwnerBox.height) {
			p1.y = r.y + r.height;
		}

		if (view != null && view.getConstrainer() != null) {
			p1 = view.getConstrainer().constrainPoint(p1);
		}
		l.fOffsetX = Geom.range(0, r.width, p1.x - r.x);
		l.fOffsetY = Geom.range(0, r.height, p1.y - r.y);
		fOwnerBox = r;
		//System.out.println("findPoint(x) - " +this.toString() +":"+
		// p1.toString());
		return p1;
	}

	/**
	 * @return the connector Point
	 */
	protected Point locate(ConnectionFigure connection) {
		return myLocator.locate(owner());
	}

	/**
	 * @return the x-coordinate of this connector
	 */
	public int locateX() {
		OffsetLocator l = (OffsetLocator) myLocator;
		return fOwnerBox.x + l.fOffsetX;
	}

	/**
	 * @return the y-coordinate of this connector
	 */
	public int locateY() {
		OffsetLocator l = (OffsetLocator) myLocator;
		return fOwnerBox.y + l.fOffsetY;
	}

	/**
	 * Constrains the point (x,y) to the figure and calculates the offsets for
	 * the resulting constrained point.
	 * 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 */
	public OffsetConnector calculateFigureConstrainedOffsets(int x, int y) {
		// minimize Point, Rectangle & other object creation
		// as this method is called by the trackConnector() method.
		trackingPoint = calculateFigureConstrainedTrackingPoint(x, y);
		OffsetLocator l = (OffsetLocator) myLocator;
		l.fOffsetX = trackingPoint.x - fOwnerBox.x;
		l.fOffsetY = trackingPoint.y - fOwnerBox.y;
		return this;
	}

	/**
	 * Constrains the point (x,y) to the figure and returns a constrained
	 * point. This method can be overridden for different figure types or
	 * different constraining policies.
	 * <p>
	 * For efficiency reasons the <em>same</em> point object is returned from
	 * every call. Be careful not to publicly expose this internal tracking
	 * point when overriding.
	 * <p>
	 * This method is NOT thread safe.
	 * 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return internal tracking point containing the constrained coordinates
	 */
	protected Point calculateFigureConstrainedTrackingPoint(int x, int y) {
		// minimize Point, Rectangle & other object creation
		trackingPoint.x = x;
		trackingPoint.y = y;
		if (view != null && view.getConstrainer() != null) {
			trackingPoint = view.getConstrainer().constrainPoint(trackingPoint);
		}

		if (!(owner() instanceof EllipseFigure)) {
			trackingPoint = Geom.angleToPoint(fOwnerBox, Geom
					.pointToAngle(fOwnerBox, trackingPoint));
		}
		else {
			trackingPoint = Geom.ovalAngleToPoint(fOwnerBox, Geom.pointToAngle(fOwnerBox,
					trackingPoint));
		}
		return trackingPoint;
	}
}
