/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.test;

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
		TestSuite suite = new TestSuite("Test for CH.ifa.draw.test");
		//$JUnit-BEGIN$
		
		suite.addTest(CH.ifa.draw.test.contrib.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.figures.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.framework.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.samples.javadraw.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.samples.minimap.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.samples.net.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.samples.nothing.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.samples.pert.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.standard.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.util.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.util.collections.jdk11.AllTests.suite());
		suite.addTest(CH.ifa.draw.test.util.collections.jdk12.AllTests.suite());
		

		//$JUnit-END$
		return suite;
	}
}
