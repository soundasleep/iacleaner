/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.IACleaner.CleanerException;

/**
 * Manually test the cleaner. 
 * TODO In the future we should add this to automated tests, perhaps using
 * JWebUnit or other to ensure the formatted code continues to work
 * as expected. 
 * 
 * The source file ("test.php") was generated automatically by the IAML
 * project.
 * 
 * @see test.php
 * @author Jevon
 *
 */
public class ManualTest extends TestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/test.php");
	
	public void testManual() throws IOException, CleanerException {
		IACleaner clean = new IACleaner();
		String output = clean.cleanScript(sourceFile);
		
		System.out.println(output);
	}
}
