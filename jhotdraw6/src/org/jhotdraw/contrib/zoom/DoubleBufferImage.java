/*
 * File:   DoubleBufferImage.java
 * Author: Andre Spiegel <spiegel@gnu.org>
 *
 * $Id$
 */

package CH.ifa.draw.contrib.zoom;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/**
 * A DoubleBufferImage is an image that scaling components, such as a
 * ZoomDrawingView, use for double buffering.  Drawing into this image
 * is scaled, but when the image is written to the screen, no more
 * scaling occurs.  This is ensured by the implementation here and
 * by the corresponding drawImage methods in ScalingGraphics.
 */
public class DoubleBufferImage extends java.awt.Image {

	private Image real;
	private double scale;

	public DoubleBufferImage(Image real, double scale) {
		this.real = real;
		this.scale = scale;
	}

	public Image getRealImage() {
		return real;
	}

	public void flush() {
		real.flush();
	}

	public Graphics getGraphics() {
		// Return an appropriate scaling graphics context,
		// so that all drawing operations into this image
		// are scaled.
		ScalingGraphics result = new ScalingGraphics(real.getGraphics());
		result.setScale(scale);
		return result;
	}

	public int getHeight(ImageObserver observer) {
		return real.getHeight(observer);
	}

	public Object getProperty(String name, ImageObserver observer) {
		return real.getProperty(name, observer);
	}

	public Image getScaledInstance(int width, int height, int hints) {
		return real.getScaledInstance(width, height, hints);
	}

	public ImageProducer getSource() {
		return real.getSource();
	}

	public int getWidth(ImageObserver observer) {
		return real.getWidth(observer);
	}

}
