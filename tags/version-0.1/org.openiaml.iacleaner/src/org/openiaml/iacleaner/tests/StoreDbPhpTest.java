/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;

/**
 * Test a mostly server-side generated PHP file. File was generated by
 * IAML.
 * 
 * @author Jevon
 *
 */
public class StoreDbPhpTest extends IACleanerTestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/store_db.php");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/store_db.php.out");
	private String output = null;
	
	public void setUp() throws Exception {
		output = clean(sourceFile, targetFile);
		assertNotNull(output);
	}
	
	// any other tests go here
	
}
