/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.openiaml.iacleaner.DefaultIACleaner;
import org.openiaml.iacleaner.IACleaner;

/**
 * Test cleaning a script using the InputStream methods (issue 11).
 * 
 * @author Jevon
 *
 */
public class InputStreamTest extends TestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/test.php");
	
	/**
	 * Whether using the File, InputStream or String methods, they
	 * should return exactly the same results.
	 */
	public void testEqualResults() throws Exception {
		IACleaner cleaner = AllTests.getCleaner();
		String inFile = cleaner.cleanScript(sourceFile);
		String inString = cleaner.cleanScript(DefaultIACleaner.readFile(sourceFile));
		String inStream = cleaner.cleanScript(new FileInputStream(sourceFile));
		
		assertTrue(inFile.equals(inString));
		assertTrue(inStream.equals(inFile));
	}
		
}
