/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;

/**
 * Test a CSS file.
 * 
 * @author Jevon
 *
 */
public class DefaultCssTest extends IACleanerTestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/default.css");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/default.css.out");
	private String output = null;
	
	@Override
	public void setUp() throws Exception {
		output = clean(sourceFile, targetFile);
		assertNotNull(output);
	}
	
	// any other tests go here
	
}
