package CH.ifa.draw.test;

import CH.ifa.draw.samples.javadraw.JavaDrawApp;
import CH.ifa.draw.framework.DrawingEditor;
import junit.framework.TestCase;

public class JHDTestCase extends TestCase {
	public static DrawingEditor myDrawingEditor = new JavaDrawApp("TestApplication");
	
	public JHDTestCase(String name) {
		super(name);
	}
	
	public DrawingEditor getDrawingEditor() {
		return myDrawingEditor;
	}
	
	/**
	 * Some tests might want start from scratch with a new DrawingEditor
	 * (to avoid side-effects from previous test)
	 */
	public DrawingEditor createNewDrawingEditor() {
		return new JavaDrawApp("TestApplication");
	}
}