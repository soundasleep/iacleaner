package org.openiaml.iacleaner.tests.expected;

import java.io.File;
import java.util.Arrays;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.openiaml.iacleaner.DefaultIACleaner;
import org.openiaml.iacleaner.IACleaner;
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
	
		String inputText = DefaultIACleaner.readFile(getInputFile());
		assertNotNull(inputText);
		String outputText = DefaultIACleaner.readFile(getExpectedFile());
		assertNotNull(outputText);
		// replace \r\n with \n
		outputText = outputText.replace("\r\n", "\n");

		IACleaner c = AllTests.getCleaner();
		String result = c.cleanScript(inputText);
		assertEquals(outputText, result);

	}
	
	/**
	 * If we load the expected file, it does not change between executions.
	 * 
	 * @throws Exception
	 */
	public void testStable() throws Exception {
		
		String inputText = DefaultIACleaner.readFile(getExpectedFile());
		assertNotNull(inputText);
		// replace \r\n with \n
		inputText = inputText.replace("\r\n", "\n");

		IACleaner c = AllTests.getCleaner();
		String result = c.cleanScript(inputText);
		assertEquals(inputText, result);
		
	}
	
	/**
	 * Expands on the standard {@link #assertEquals(String, String)} method by also
	 * printing out the individual characters.
	 * 
	 * @param a
	 * @param b
	 */
	public static void assertEquals(String a, String b) {
		try {
			TestCase.assertEquals(a, b);
		} catch (AssertionFailedError e) {
			System.out.println("a: " + Arrays.toString(a.toCharArray()));
			System.out.println("b: " + Arrays.toString(b.toCharArray()));
			throw e;
		}
	}
	
}
