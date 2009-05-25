package org.openiaml.iacleaner.tests.expected;

import java.io.File;

import junit.framework.TestCase;

import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.IARegexpCleaner;
import org.openiaml.iacleaner.tests.AllTests;

/**
 * For testing the expected results of different input files, we extend this abstract class.
 * 
 * @author Jevon
 *
 */
public abstract class ExpectedTestCase extends TestCase {

	/**
	 * Location of expected resources.
	 */
	public static final String ROOT = "src/org/openiaml/iacleaner/tests/expected/resources/";
	
	/**
	 * Get the current class.
	 */
	public abstract Class<?> getTestCaseClass();
	
	protected String getInputFilename() {
		assertTrue("Test case '" + getTestCaseClass() + "' needs to have an underscore in its name.", getTestCaseClass().getSimpleName().contains("_"));
		return getTestCaseClass().getSimpleName().replace("_", ".").toLowerCase();
	}
	
	/**
	 * Input file.
	 * @return
	 */
	protected File getInputFile() {
		String inputFile = getInputFilename();
		
		File f = new File(ROOT + inputFile);
		assertTrue("File '" + inputFile + "' doesn't exist", f.exists());
		
		return f;
	}
	
	/**
	 * Expected output.
	 * @return
	 */
	protected File getExpectedFile() {
		String inputFile = getInputFilename();
		String resultFile = inputFile.substring(0, inputFile.lastIndexOf(".")) + ".expected." + inputFile.substring(inputFile.lastIndexOf(".") + 1);
		
		File f = new File(ROOT + resultFile);
		assertTrue("File '" + inputFile + "' doesn't exist", f.exists());
		
		return f;
	}
	
	/**
	 * Test that [testcase_ext], after cleaning, equals [testcase_expected_ext] exactly.
	 * 
	 * @throws Exception
	 */
	public void testExpected() throws Exception {
	
		String inputText = IARegexpCleaner.readFile(getInputFile());
		assertNotNull(inputText);
		String outputText = IARegexpCleaner.readFile(getExpectedFile());
		assertNotNull(outputText);

		IACleaner c = AllTests.getCleaner();
		assertEquals(outputText, c.cleanScript(inputText));

	}
	
	/**
	 * If we load the expected file, it does not change between executions.
	 * 
	 * @throws Exception
	 */
	public void testStable() throws Exception {
		
		String inputText = IARegexpCleaner.readFile(getExpectedFile());
		assertNotNull(inputText);

		IACleaner c = AllTests.getCleaner();
		assertEquals(inputText, c.cleanScript(inputText));
		
	}
	
}
