package CH.ifa.draw.test.samples.pert;

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
		TestSuite suite = new TestSuite("Test for CH.ifa.draw.test.samples.pert");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(PertAppletTest.class));
		suite.addTest(new TestSuite(PertApplicationTest.class));
		suite.addTest(new TestSuite(PertDependencyTest.class));
		suite.addTest(new TestSuite(PertFigureCreationToolTest.class));
		suite.addTest(new TestSuite(PertFigureTest.class));
		//$JUnit-END$
		return suite;
	}
}
