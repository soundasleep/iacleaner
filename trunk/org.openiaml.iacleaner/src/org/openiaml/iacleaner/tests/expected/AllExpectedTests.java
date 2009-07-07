/**
 * 
 */
package org.openiaml.iacleaner.tests.expected;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * These cover all the expected output tests for the IACleaner
 * interface.
 * 
 * @author Jevon
 *
 */
public class AllExpectedTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Expected output tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(JsFunctions_Html.class);
		suite.addTestSuite(PhpBlock_Php.class);
		suite.addTestSuite(Basic_Php.class);
		suite.addTestSuite(Comments_Php.class);
		suite.addTestSuite(JsComments_Html.class);
		suite.addTestSuite(Attributes_Html.class);
		suite.addTestSuite(Basic_Html.class);
		suite.addTestSuite(Inline2_Html.class);
		suite.addTestSuite(Regexps_Html.class);
		suite.addTestSuite(Inline_Html.class);
		suite.addTestSuite(Comments_Html.class);
		suite.addTestSuite(Strings_Php.class);
		suite.addTestSuite(Example1_Php.class);
		suite.addTestSuite(Functions_Php.class);
		suite.addTestSuite(PhpBrace_Php.class);
		suite.addTestSuite(Example4Js_Html.class);
		suite.addTestSuite(AttributesLong_Html.class);
		suite.addTestSuite(Example3_Php.class);
		suite.addTestSuite(PhpInJsStrings_Php.class);
		suite.addTestSuite(Example2_Php.class);
		suite.addTestSuite(JsLanguage_Html.class);
		suite.addTestSuite(Inline3_Html.class);
		suite.addTestSuite(JsInlinePhp_Php.class);
		suite.addTestSuite(BasicJs_Html.class);
		suite.addTestSuite(PhpInJsComments_Php.class);
		suite.addTestSuite(PhpStrings_Php.class);
		suite.addTestSuite(JsStrings_Html.class);
		suite.addTestSuite(JsStrings_Js.class);
		suite.addTestSuite(JsFunctions2_Html.class);
		suite.addTestSuite(Css_Html.class);
		suite.addTestSuite(Css_Css.class);
		suite.addTestSuite(PhpOperations_Php.class);
		suite.addTestSuite(JsOperations_Html.class);
		suite.addTestSuite(PhpInlineHtml_Php.class);
		//$JUnit-END$
		return suite;
	}

}
