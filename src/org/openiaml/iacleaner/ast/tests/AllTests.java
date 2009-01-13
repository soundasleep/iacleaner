/**
 * 
 */
package org.openiaml.iacleaner.ast.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author jmwright
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for test2.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(LoadingTest.class);
		//$JUnit-END$
		return suite;
	}

}
