package CH.ifa.draw.contrib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.filechooser.FileFilter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.util.StorageFormat;


/**
 * The SVGStorageFormat can save drawings in SVG 1.0.  At this time, it cannot load
 * SVG drawings.
 *
 * TODO: Refactor this and the other storage formats.  There is too much duplication.
 * 
 * @version <$CURRENT_VERSION$>
 * @author mtnygard
 */
public class SVGStorageFormat implements StorageFormat {
	/**
	 * FileFilter for a javax.swing.JFileChooser which recognizes files with the
	 * extension "svg"
	 */
	private FileFilter filter;
	
	/**
	 * File extension
	 */
	private String extension = "svg";

	/**
	 * Description of the file type when displaying the FileFilter
	 */
	private static String description = "SVG Drawing (SVG)";
	

  /**
   * Return the filter that JFileChooser will use to identify SVG files.
   * 
   * @return the filter that JFileChooser will use to identify SVG files
   * @see CH.ifa.draw.util.StorageFormat#getFileFilter()
   * 
   * TODO: Refactor together with similar code from StandardStorageFormat
   */
  public FileFilter getFileFilter() {
  	if(filter == null) {
  		filter = createFileFilter();
  	}
    return filter;
  }

	protected FileFilter createFileFilter() {
		return new FileFilter() {
      public boolean accept(File f) {
        return f.getName().endsWith(extension);
      }
      public String getDescription() {
        return description;
      }
			
		};
	}

  /**
	 * Store a Drawing as SVG under a given name.
	 *
	 * @param fileName file name of the Drawing under which it should be stored
	 * @param saveDrawing drawing to be saved
	 * @return file name with correct file extension
   * @see CH.ifa.draw.util.StorageFormat#store(java.lang.String, CH.ifa.draw.framework.Drawing)
   */
  public String store(String fileName, Drawing saveDrawing) throws IOException {
		// Get a DOMImplementation
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document
		Document document = domImpl.createDocument(null, "svg", null);
	
		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
	
		// Ask the test to render into the SVG Graphics2D implementation
		saveDrawing.draw(svgGenerator);
	
		// Finally, stream out SVG to the standard output using UTF-8
		// character to byte encoding
		fileName = adjustFileName(fileName);
		FileOutputStream fos = new FileOutputStream(fileName);
		Writer out = new OutputStreamWriter(fos, "UTF-8");
		
		
		svgGenerator.stream(out, true);
		return fileName;
  }

  /**
   * @see CH.ifa.draw.util.StorageFormat#restore(java.lang.String)
   */
  public Drawing restore(String fileName) throws IOException {
    throw new IOException("Not implemented");
  }


	/**
	 * Adjust a file name to have the correct file extension.
	 *
	 * @param testFileName file name to be tested for a correct file extension
	 * @return testFileName + file extension if necessary
	 * 
	 * TODO: Refactor this with the same code from StandardStorageFormat.
	 */	
	protected String adjustFileName(String testFileName) {
		if (!hasCorrectFileExtension(testFileName)) {
			return testFileName + "." + getExtension();
		}
		else {
			return testFileName;
		}
	}

	/**
	 * Test whether the file name has the correct file extension
	 *
	 * @return true, if the file has the correct extension, false otherwise
	 */
	protected boolean hasCorrectFileExtension(String testFileName) {
		return testFileName.endsWith("." + getExtension());
	}
	
  /**
   * Returns the extension.
   * @return String
   */
  public String getExtension() {
    return extension;
  }

}
