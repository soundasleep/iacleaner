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
		suite.addTestSuite(FunctionDefinitionTest.class);
		suite.addTestSuite(CommentsTest.class);
		suite.addTestSuite(VariableTest.class);
		suite.addTestSuite(SimpleTest.class);
		suite.addTestSuite(MultipleLines.class);
		suite.addTestSuite(SimpleHtmlTest.class);
		suite.addTestSuite(HtmlTest.class);
		suite.addTestSuite(ConditionalTest.class);
		suite.addTestSuite(FunctionComplexTest.class);
		suite.addTestSuite(FunctionTest.class);
		suite.addTestSuite(HtmlAttributesTest.class);
		suite.addTestSuite(ClassTest.class);
		//$JUnit-END$
		return suite;
	}

}
