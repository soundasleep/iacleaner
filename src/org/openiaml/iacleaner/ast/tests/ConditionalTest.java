/**
 * 
 */
package org.openiaml.iacleaner.ast.tests;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.openiaml.iacleaner.ast.InternetApplication;
import org.openiaml.iacleaner.ast.ParseException;
import org.openiaml.iacleaner.ast.SimpleNode;

/**
 * Load a really simple file.
 * 
 * @author jmwright
 *
 */
public class ConditionalTest extends TestCase {

	public void testLoad() throws FileNotFoundException, ParseException {
		File file = new File("src/test2/tests/conditional.php");
		assertTrue("File '" + file.getAbsolutePath() + "' exists", file.exists());
		SimpleNode node = InternetApplication.loadFile(file);

		assertNotNull(node);
	}
	
}
