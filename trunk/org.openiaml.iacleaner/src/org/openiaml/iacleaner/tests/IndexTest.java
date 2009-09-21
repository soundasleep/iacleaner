/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;

/**
 * Test a simple HTML file.
 * 
 * @author Jevon
 *
 */
public class IndexTest extends IACleanerTestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/index.html");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/index.html.out");
	private String output = null;
	
	@Override
	public void setUp() throws Exception {
		output = clean(sourceFile, targetFile);
		assertNotNull(output);
	}
	
	// any other tests go here
	
}
