package CH.ifa.draw.test;

import junit.framework.TestCase;
import CH.ifa.draw.samples.javadraw.JavaDrawApp;

public class JHDTestCase extends TestCase {
	public JavaDrawApp myDrawingEditor;
	
	public JHDTestCase(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		myDrawingEditor = new JavaDrawApp("TestApplication");
	}
	
	protected void tearDown() throws Exception {
		myDrawingEditor.setVisible(false);
		myDrawingEditor = null;
	}
	
	public JavaDrawApp getDrawingEditor() {
		return myDrawingEditor;
	}
	
	/**
	 * Some tests might want start from scratch with a new DrawingEditor
	 * (to avoid side-effects from previous test)
	 */
	public JavaDrawApp createNewDrawingEditor() {
		return new JavaDrawApp("TestApplication");
	}
}