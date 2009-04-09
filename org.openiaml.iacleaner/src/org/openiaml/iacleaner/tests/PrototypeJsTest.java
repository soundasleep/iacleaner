/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;

/**
 * Test the Prototype Javascript framework.
 * 
 * @author Jevon
 *
 */
public class PrototypeJsTest extends IACleanerTestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/prototype.js");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/prototype.js.out");
	private String output = null;
	
	public void setUp() throws Exception {
		output = clean(sourceFile, targetFile);
		assertNotNull(output);
	}
	
	// any other tests go here
	
}
