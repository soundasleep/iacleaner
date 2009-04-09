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
public class SitemapHtmlTest extends IACleanerTestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/sitemap.html");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/sitemap.html.out");
	private String output = null;
	
	public void setUp() throws Exception {
		output = clean(sourceFile, targetFile);
		assertNotNull(output);
	}
	
	// any other tests go here
	
}
