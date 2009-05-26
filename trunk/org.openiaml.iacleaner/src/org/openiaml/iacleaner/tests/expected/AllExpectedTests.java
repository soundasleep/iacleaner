/**
 * 
 */
package org.openiaml.iacleaner.tests.expected;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jevon
 *
 */
public class AllExpectedTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Expected output tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(Basic_Html.class);
		suite.addTestSuite(Inline2_Html.class);
		suite.addTestSuite(Comments_Html.class);
		suite.addTestSuite(PhpBlock_Php.class);
		suite.addTestSuite(Inline_Html.class);
		suite.addTestSuite(Basic_Php.class);
		suite.addTestSuite(Inline3_Html.class);
		//$JUnit-END$
		return suite;
	}

}
