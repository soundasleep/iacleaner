/**
 * 
 */
package org.openiaml.iacleaner.tests.inline;

import java.io.File;

import junit.framework.TestCase;

import org.openiaml.iacleaner.DefaultIACleaner;

/**
 * Tests IACleaner.getFileExtension()
 * 
 * @author Jevon
 *
 */
public class TestGetExtension extends TestCase {

	public void testBasic() {
		File f = new File("test.html");
		assertEquals("html", DefaultIACleaner.getExtension(f));
	}

	public void testPath() {
		File f = new File("/dev/null/test.script");
		assertEquals("script", DefaultIACleaner.getExtension(f));
	}

	public void testNone() {
		File f = new File("/dev/null/test");
		assertEquals("", DefaultIACleaner.getExtension(f));
	}

	public void testPathWithDots() {
		File f = new File("/dev/null/test.html/test.php");
		assertEquals("php", DefaultIACleaner.getExtension(f));
	}

}
