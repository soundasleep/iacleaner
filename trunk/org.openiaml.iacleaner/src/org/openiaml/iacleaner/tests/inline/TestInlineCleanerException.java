/**
 * 
 */
package org.openiaml.iacleaner.tests.inline;

import junit.framework.TestCase;

import org.openiaml.iacleaner.CleanerException;
import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.tests.AllTests;

/**
 * Tests the generation of inline cleaner exceptions.
 * 
 * @author Jevon
 *
 */
public class TestInlineCleanerException extends TestCase {

	private final String HTML_SOURCE = "<html>\n" +
		"<body>\n" +
		"</body";
	
	/**
	 * Test to make sure when we throw a cleaner exception, we get the
	 * proper line count.
	 */
	public void testLineCount() {
		IACleaner cleaner = AllTests.getCleaner();
		try {
			cleaner.cleanScript(HTML_SOURCE);
			fail("Broken HTML script should have thrown an exception.");
		} catch (CleanerException e) {
			// expected
			assertContains("Could not read until end of HTML tag", e.getMessage());
			assertContains("Line 3", e.getMessage());
		}
	}

	/**
	 * Assert that the given string contains the given needle.
	 * 
	 * @param string
	 * @param needle
	 */
	protected void assertContains(String needle, String string) {
		if (!string.contains(needle)) {
			fail("String '" + string + "' did not contain '" + needle + "'");
		}
	}

	/**
	 * Assert that the given 'message' string matches the given
	 * regexp.
	 * 
	 * @param string
	 * @param message
	 */
	protected void assertMatch(String regexp, String message) {
		assertTrue("String '" + message + "' does not match regular expression '" + regexp + "'", message.matches(regexp));
	}
	
}
