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

	/*********************************************************************
	 *
	 * Basic methods for image/icon retrieval.
	 * 
	 ********************************************************************/

	/**
	 * Just gets the image pointed to by the URL and doesn't store
	 * it in cache.
	 */
	public Image loadImageUncachedURL(URL url) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			return toolkit.createImage((ImageProducer) url.getContent());
		}
		catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Just gets the file but doesn't store it in cache.
	 */
	public Image loadImageUncached(String fileName) {
		return loadImageUncachedURL(getResourceURL(fileName));
	}

	/**
 	 * Loads an image URL with the given name, caches it, and
 	 * optionally waits for it to finish loading.
	 */
	public Image loadImageURL(URL url, boolean waitForLoad) {
		if (fMap.containsKey(url)) {
			return (Image) fMap.get(url);
		}

		Image image = loadImageUncachedURL(url);

		if (image != null) {
			fMap.put(url, image);
			if (waitForLoad) {
				waitForLoadedImage(image);
			}
		}

		return image;
	}

	/**
 	 * Loads an image file with the given name, caches it, and
 	 * optionally waits for it to finish loading.
	 */
	public Image loadImage(String fileName, boolean waitForLoad) {
		return loadImageURL(getResourceURL(fileName), waitForLoad);
	}
	
	/**
	 * Loads an image URL with the given name and caches it
	 */
	public Image loadImageURL(URL url) {
		return loadImageURL(url, false);
	}

	/**
	 * Loads an image with the given fileName and caches it.
	 */
	public Image loadImage(String fileName) {
		return loadImageURL(getResourceURL(fileName), false);
	}

	/**
	 * Blocks while image loads and returns a completely loaded
	 * version of image.
	 */
	public Image waitForLoadedImage(Image image) {
		if (image!=null) {
			ImageIcon icon = new ImageIcon(image);
			// icon.getImage forces the wait to happen
			image = icon.getImage();
		}
		return image;
	}

	/**
	 * To translate between a resource and a URL
	 */
	private URL getResourceURL(String resourceName) {
		return getClass().getResource(resourceName);
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
	 * Loads all registered images.
	 * If component is null, the component supplied in the
	 * constructor will be used.
	 * @see #registerImage
	 * @see #registerImageURL
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
			if (! fMap.containsKey(url)) {
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

	/*********************************************************************
	 *
	 * Deprecated methods
	 * 
	 ********************************************************************/

	/**
	 * Gets the image with the given fileName. If the image can't
	 * be found it tries it again after registering the image and
	 * loading all the registered images.
	 * 
	 * @deprecated use loadImage instead
	 */
	public Image getImage(String fileName) {
		return loadImage(fileName, true);
	}

	/**
	 * Registers and loads an image.
	 * If component is null, the component supplied in the
	 * constructor will be used.
	 * 
	 * @deprecated use loadImage instead
	 */
	public Image registerAndLoadImage(Component component, String fileName) {
		registerImage(fileName);
		loadRegisteredImages(component);
		return loadImage(fileName, true);
	}

	/**
	 * Loads an image but does not put in in the cache.
	 * 
	 * @deprecated use loadImageUncached instead
	 */
	public Image loadImageResource(String fileName) {
		return loadImageUncached(fileName);
	}


}
