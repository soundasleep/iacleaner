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
		assertTrue("File '" + resultFile + "' doesn't exist", f.exists());
		
		return f;
	}
	
	/**
	 * Another input, with most whitespace removed.
	 * 
	 * @return
	 */
	protected File getWhitespaceInputFile() {
		String inputFile = getInputFilename();
		String resultFile = inputFile.substring(0, inputFile.lastIndexOf(".")) + ".compact." + inputFile.substring(inputFile.lastIndexOf(".") + 1);
		
		File f = new File(ROOT + resultFile);
		assertTrue("File '" + resultFile + "' doesn't exist", f.exists());
		
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
		String result = c.cleanScript(getInputFile());
		assertStringEquals(outputText, result);
		
		// no warnings
		assertNoWarnings(c);
		
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
		String result = c.cleanScript(getExpectedFile());
		assertStringEquals(inputText, result);

		// no warnings
		assertNoWarnings(c);

	}
	
	/**
	 * This is the same file, except all whitespace has been removed. 
	 * iacleaner should properly indent the code as well.
	 * 
	 * @throws Exception
	 */
	public void testWhitespace() throws Exception {
		
		String inputText = DefaultIACleaner.readFile(getWhitespaceInputFile());
		assertNotNull(inputText);
		String outputText = DefaultIACleaner.readFile(getExpectedFile());
		assertNotNull(outputText);
		// replace \r\n with \n
		outputText = outputText.replace("\r\n", "\n");

		IACleaner c = AllTests.getCleaner();
		String result = c.cleanScript(getWhitespaceInputFile());
		assertStringEquals(outputText, result);

		// no warnings
		assertNoWarnings(c);

	}
	
	/**
	 * The given IACleaner should not contain any warnings.
	 * 
	 * @param c
	 */
	protected void assertNoWarnings(IACleaner c) {
		if (c.hasWarnings()) {
			fail("IACleaner should not contain warnings, contains " + c.getWarnings().size() + " warnings. First: '" + c.getWarnings().get(0) + "'");
		}
	}

	/**
	 * This is like {@link #assertEquals(String, String)}, except we ignore any
	 * differences in line endings (\r, \n)
	 * 
	 * @param expected
	 * @param actual
	 */
	public static void assertStringEquals(String expected, String actual) {
		assertEquals(expected.replace("\r", ""), actual.replace("\r", ""));
	}
	
	/**
	 * Expands on the standard {@link #assertEquals(String, String)} method by also
	 * printing out the individual characters.
	 * 
	 * @param expected
	 * @param actual
	 */
	public static void assertEquals(String expected, String actual) {
		try {
			TestCase.assertEquals(expected, actual);
		} catch (AssertionFailedError e) {
			System.out.println("result: " + Arrays.toString(actual.toCharArray()));
			System.out.println("wanted: " + Arrays.toString(expected.toCharArray()));
			throw e;
		}
	}
	
}
