/**
 * 
 */
package org.openiaml.iacleaner.ast.tests;

import java.io.File;

import junit.framework.TestCase;

import org.openiaml.iacleaner.ast.InternetApplication;
import org.openiaml.iacleaner.ast.SimpleNode;

/**
 * A series of tests to load different source files.
 * 
 * @author jmwright
 *
 */
public class LoadingTest extends TestCase {

	public SimpleNode loadFile(String relative) throws Exception {
		File file = new File("src/org/openiaml/iacleaner/ast/tests/" + relative);
		assertTrue("File '" + file.getAbsolutePath() + "' exists", file.exists());
		SimpleNode node = InternetApplication.loadFile(file);

		assertNotNull(node);
		
		return node;
	}
	
	public void testHtmlAttributes() throws Exception {
		loadFile("attributes.html");
	}

	public void testPhpClass() throws Exception {
		loadFile("class.php");
	}

	public void testHtmlComments() throws Exception {
		loadFile("comments.html");
	}

	public void testPhpComments() throws Exception {
		loadFile("comments.php");
	}

	public void testPhpComplex() throws Exception {
		loadFile("complex.php");
	}

	public void testPhpConditional() throws Exception {
		loadFile("conditional.php");
	}

	public void testPhpFunctionComplex() throws Exception {
		loadFile("function_complex.php");
	}

	public void testPhpFunctionDefinition() throws Exception {
		loadFile("function_definition.php");
	}

	public void testPhpFunction() throws Exception {
		loadFile("function.php");
	}

	public void testHtmlIndex() throws Exception {
		loadFile("index.html");
	}

	public void testPhpMultiple() throws Exception {
		loadFile("multiple.php");
	}

	public void testHtmlSimple() throws Exception {
		loadFile("simple.html");
	}
	
	public void testPhpSimple() throws Exception {
		loadFile("simple.php");
	}
	
	public void testPhpVariables() throws Exception {
		loadFile("variable.php");
	}
	
	public void testPhpSwitching() throws Exception {
		loadFile("switching.php");
	}
	
}
