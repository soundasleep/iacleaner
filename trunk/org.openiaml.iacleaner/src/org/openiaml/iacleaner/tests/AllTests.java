/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.IAInlineCleaner;
import org.openiaml.iacleaner.tests.expected.AllExpectedTests;
import org.openiaml.iacleaner.tests.inline.AllInlineTests;

/**
 * Run all automated tests.
 * 
 * @author Jevon
 *
 */
public class AllTests {

	public static Test suite() throws IOException {
		TestSuite suite = new TimedTestSuite(
				"All tests",
				"timed.log") {
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(DefaultCssTest.class);
		suite.addTestSuite(PrototypeJsTest.class);
		suite.addTestSuite(StoreDbPhpTest.class);
		suite.addTestSuite(ClearSessionPhpTest.class);
		suite.addTestSuite(IndexTest.class);
		suite.addTestSuite(ComplexPhpTest.class);
		suite.addTestSuite(SitemapHtmlTest.class);
		suite.addTest(AllExpectedTests.suite());
		suite.addTest(AllInlineTests.suite());
		//$JUnit-END$
		return suite;
	}
	
	/**
	 * Get the iacleaner to test with.
	 */
	public static IACleaner getCleaner() {
		return new IAInlineCleaner();
	}

}
