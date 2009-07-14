/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.openiaml.iacleaner.CleanerException;
import org.openiaml.iacleaner.IACleaner;
import org.openiaml.iacleaner.IARegexpCleaner;

/**
 * Abstract class defining common tests and loading methods
 * for the automated test cases.
 * 
 * @author Jevon
 *
 */
public abstract class IACleanerTestCase extends TestCase {
	
	private IACleaner clean;
	
	/**
	 * Clean and format the source file into the target file.
	 * Designed to be used in setUp().
	 * 
	 * @param sourceFile source file
	 * @param targetFile target file
	 * @return the cleaned output
	 */
	public String clean(File sourceFile, File targetFile) throws IOException, CleanerException {
		clean = AllTests.getCleaner();
		String output = clean.cleanScript(sourceFile);
	
		// write it to out.php
		FileWriter fw = new FileWriter(targetFile);
		fw.write(output);
		fw.close();
		
		return output;
	}

	/**
	 * Ideally there won't be any warnings thrown.
	 */
	public void testWarnings() {
		for (String warning : clean.getWarnings()) {
			fail("Expected no warnings, got: " + warning + ", plus " + (clean.getWarnings().size()-1) + " others");
		}
	}
	
	/**
	 * We shouldn't have substituted any strings that are too long.
	 * This can often happen if there is a misplaced quote somewhere
	 * that is screwing up the string recognition routine (most
	 * often single quotes in words like 'don't').
	 */
	public void testSubstitutions() {
		if (clean instanceof IARegexpCleaner) {
			IARegexpCleaner rx = (IARegexpCleaner) clean;
			for (String key : rx.getStringSubstitutions().keySet()) {
				String value = rx.getStringSubstitutions().get(key);
				assertNotNull(value);	// sanity check
				
				int nlCount = value.split("\n").length;
				assertTrue("String substitution " + key + " was too long: " + value,
						nlCount < 10);
			}
		}
		
	}

	/**
	 * Get the cleaner used in {@link #clean(File, File)}.
	 * 
	 * @return the cleaner
	 */
	public IACleaner getCleaner() {
		return clean;
	}
}
