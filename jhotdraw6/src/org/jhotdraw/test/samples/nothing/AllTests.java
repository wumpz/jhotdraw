package CH.ifa.draw.test.samples.nothing;

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
		TestSuite suite = new TestSuite("Test for CH.ifa.draw.test.samples.nothing");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(NothingAppTest.class));
		suite.addTest(new TestSuite(NothingAppletTest.class));
		//$JUnit-END$
		return suite;
	}
}
