package CH.ifa.draw.test.samples.net;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:mtnygard@charter.net">Michael T. Nygard</a>
 * @version $Revision$
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for CH.ifa.draw.test.samples.net");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(NetAppTest.class));
		suite.addTest(new TestSuite(NodeFigureTest.class));
		//$JUnit-END$
		return suite;
	}
}
