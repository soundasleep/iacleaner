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
		suite.addTestSuite(JsFunctions_Html.class);
		suite.addTestSuite(Comments_Html.class);
		suite.addTestSuite(PhpBlock_Php.class);
		suite.addTestSuite(Strings_Php.class);
		suite.addTestSuite(Example1_Php.class);
		suite.addTestSuite(Basic_Php.class);
		suite.addTestSuite(Comments_Php.class);
		suite.addTestSuite(Functions_Php.class);
		suite.addTestSuite(Example4Js_Html.class);
		suite.addTestSuite(Attributes_Html.class);
		suite.addTestSuite(JsComments_Html.class);
		suite.addTestSuite(Basic_Html.class);
		suite.addTestSuite(Inline2_Html.class);
		suite.addTestSuite(Example3_Php.class);
		suite.addTestSuite(Example2_Php.class);
		suite.addTestSuite(JsLanguage_Html.class);
		suite.addTestSuite(Inline_Html.class);
		suite.addTestSuite(Inline3_Html.class);
		suite.addTestSuite(JsInlinePhp_Php.class);
		suite.addTestSuite(BasicJs_Html.class);
		//$JUnit-END$
		return suite;
	}

}
