/*
 * @(#)StorageFormat.java 5.2
 *
 */

package CH.ifa.draw.util;

import javax.swing.filechooser.FileFilter;
import java.io.IOException;
import CH.ifa.draw.framework.Drawing;

/**
 * Interface to define a storage format. A StorageFormat is a strategy that knows how to store
 * and restore a Drawing according to a specific encoding. Typically it can be recognized by
 * a file extension. To identify a valid file format for a Drawing an appropriate FileFilter
 * for a javax.swing.JFileChooser component can be requested.
 *
 * @see Drawing
 * @see StorageFormatManager
 * @author Wolfram Kaiser
 */
public interface StorageFormat {

	/**
	 * Return a FileFilter that can be used to identify files which can be stored and restored
	 * with this Storage Format. Typically, each storage format has its own recognizable file
	 * extension.
	 *
	 * @return FileFilter to be used with a javax.swing.JFileChooser
	 */
	public FileFilter getFileFilter();
	
	/**
	 * Store a Drawing under a given name.
	 *
	 * @param fileName file name of the Drawing under which it should be stored
	 * @param saveDrawing drawing to be saved
	 * @return file name with correct file extension
	 */
	public String store(String fileName, Drawing saveDrawing) throws IOException;
	
	/**
	 * Restore a Drawing from a file with a given name. 
	 *
	 * @param name of the file in which the Drawing has been saved
	 * @return restored Drawing
	 */
	public Drawing restore(String fileName) throws IOException;
}
