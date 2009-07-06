/**
 * 
 */
package org.openiaml.iacleaner.tests.inline;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the generation of inline cleaner exceptions.
 * 
 * @author Jevon
 *
 */
public class AllInlineTests extends TestCase {
	
	public static Test suite() throws IOException {
		TestSuite suite = new TestSuite("Inline iacleaner tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestInlineCleanerException.class);
		//$JUnit-END$
		return suite;
	}
}
