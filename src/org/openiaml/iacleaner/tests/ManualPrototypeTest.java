/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.IACleaner.CleanerException;

/**
 * @see prototype.js
 * @author Jevon
 *
 */
public class ManualPrototypeTest extends TestCase {
	
	public File sourceFile = new File("src/org/openiaml/iacleaner/tests/prototype.js");
	public File targetFile = new File("src/org/openiaml/iacleaner/tests/prototype.out.js");
	
	public void testManual() throws IOException, CleanerException {
		IACleaner clean = new IACleaner();
		String output = clean.cleanScript(sourceFile);
		
		System.out.println(output);
		
		// write it to out.php
		FileWriter fw = new FileWriter(targetFile);
		fw.write(output);
		fw.close();
		
		// print out any warnings
		for (String warning : clean.getWarnings()) {
			System.err.println(warning);
		}
	}
}
