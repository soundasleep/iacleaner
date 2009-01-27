/**
 * 
 */
package org.openiaml.iacleaner.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Run all automated tests.
 * 
 * @author Jevon
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TimedTestSuite(
				"Test for org.openiaml.iacleaner.tests") {
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(DefaultCssTest.class);
		suite.addTestSuite(PrototypeJsTest.class);
		suite.addTestSuite(StoreDbPhpTest.class);
		suite.addTestSuite(ClearSessionPhpTest.class);
		suite.addTestSuite(IndexTest.class);
		suite.addTestSuite(ComplexPhpTest.class);
		//$JUnit-END$
		return suite;
	}

}
