/*
 * @(#)Iconkit.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Hashtable;

/**
 * The Iconkit class supports the sharing of images. It maintains
 * a map of image names and their corresponding images.
 *
 * Iconkit also supports to load a collection of images in
 * synchronized way.
 * The resolution of a path name to an image is delegated to the DrawingEditor.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld031.htm>Singleton</a></b><br>
 * The Iconkit is a singleton.
 * <hr>
 *
 * @version <$CURRENT_VERSION$>
 */
public class Iconkit {
	/* Holds URLs as keys, and Images as values */
	private Map                 fMap;
	/* Holds URLs */
	private Set                 fRegisteredImages;
	private Component           fComponent;
	private final static int    ID = 123;
	private static Iconkit      fgIconkit = null;

	/**
	 * Constructs an Iconkit that uses the given editor to
	 * resolve image path names.
	 */
	public Iconkit(Component component) {
		fMap = new Hashtable(53);
		fRegisteredImages = CollectionsFactory.current().createSet();
		fComponent = component;
		fgIconkit = this;
	}

	/**
	 * Gets the single instance
	 */
	public static Iconkit instance() {
		return fgIconkit;
	}

	/**
	 * Loads all registered images.
	 * If component is null, the component supplied in the
	 * constructor will be used.
	 * @see #registerImage
	 */
	public void loadRegisteredImages(Component component) {
		if (fRegisteredImages.size() == 0)
			return;

		if (component == null) {
			component = fComponent;
		}

		MediaTracker tracker = new MediaTracker(component);
		// register images with MediaTracker
		Iterator iter = fRegisteredImages.iterator();
		while (iter.hasNext()) {
			URL url = (URL)iter.next();
			if (basicGetImageURL(url) == null) {
				tracker.addImage(loadImageURL(url), ID);
			}
		}
		fRegisteredImages.clear();

		// block until all images are loaded
		try {
			tracker.waitForAll();
		}
		catch (Exception e) {
			// ignore: do nothing
		}
	}

	/**
	 * Registers a URL that is then loaded together with
	 * the other registered images by loadRegisteredImages.
	 * @see #loadRegisteredImages
	 */
	public void registerImageURL(URL url) {
		fRegisteredImages.add(url);
	}
	
	/**
	 * Registers the URL for the image resource
	 * @see #registerImageURL
	 */
	public void registerImage(String fileName) {
		registerImageURL(getResourceURL(fileName));
	}

	/**
	 * Loads an image URL with the given name.
	 */
	public Image loadImageURL(URL url) {
		if (fMap.containsKey(url)) {
			return (Image) fMap.get(url);
		}
		Image image = loadImageResourceURL(url);
		if (image != null) {
			fMap.put(url, image);
		}
		return image;
	}

	/**
	 * Loads an image with the given name.
	 */
	public Image loadImage(String fileName) {
		return loadImageURL(getResourceURL(fileName));
	}

	public Image loadImageResourceURL(URL url) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			return toolkit.createImage((ImageProducer) url.getContent());
		}
		catch (Exception ex) {
			return null;
		}
	}

	public Image loadImageResource(String fileName) {
		return loadImageResourceURL(getResourceURL(fileName));
	}

	/**
	 * Registers and loads an image.
	 * If component is null, the component supplied in the
	 * constructor will be used.
	 */
	public Image registerAndLoadImageURL(Component component, URL url) {
		registerImageURL(url);
		loadRegisteredImages(component);
		return getImageURL(url);
	}
	/**
	 * Registers and loads an image.
	 * If component is null, the component supplied in the
	 * constructor will be used.
	 */
	public Image registerAndLoadImage(Component component, String fileName) {
		return registerAndLoadImageURL(component, getResourceURL(fileName));
	}

	public Image loadImageURL(URL url, boolean waitForLoad) {
		Image image = loadImageURL(url);
		if (image!=null && waitForLoad) {
			ImageIcon icon = new ImageIcon(image);
			image = icon.getImage(); //this forces the wait to happen
		}
		return image;
	}

	public Image loadImage(String fileName, boolean waitForLoad) {
		return loadImageURL(getResourceURL(fileName), waitForLoad);
	}
	
	/**
	 * Gets the image with the given URL. If the image can't be
	 * found it tries it again after registering the image and
	 * loading all the registered images.
	 */
	public Image getImageURL(URL url) {
		Image image = basicGetImageURL(url);
		if (image != null) {
			return image;
		}
		registerImageURL(url);
		// load registered images and try again
		loadRegisteredImages(fComponent);
		// try again
		return basicGetImageURL(url);
	}
	/**
	 * Gets the image with the given fileName. If the image can't
	 * be found it tries it again after registering the image and
	 * loading all the registered images.
	 */
	public Image getImage(String fileName) {
		return getImageURL(getResourceURL(fileName));
	}

	private URL getResourceURL(String resourceName) {
		return getClass().getResource(resourceName);
	}

	private Image basicGetImageURL(URL url) {
		if (fMap.containsKey(url)) {
			return (Image) fMap.get(url);
		}
		return null;
	}

}
