/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of IACleaner, which implements some of the 
 * warning methods.
 * 
 * @author Jevon
 *
 */
public abstract class DefaultIACleaner implements IACleaner {

	protected List<String> warnings = new ArrayList<String>();
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#throwWarning(java.lang.String, java.lang.String)
	 */
	public void throwWarning(String string, String context) {
		System.err.println("Warning: " + string);
		System.err.println("Context:");
		System.err.println(context);
		warnings.add(string);
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#hasWarnings()
	 */
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#getWarnings()
	 */
	public List<String> getWarnings() {
		return warnings;
	}

	/**
	 * Read in a file into a string.
	 * 
	 * @throws IOException if an IO exception occurs
	 */
	public static String readFile(File sourceFile) throws IOException {
		int bufSize = 128;
		StringBuffer sb = new StringBuffer(bufSize);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile), bufSize);
				
		char[] chars = new char[bufSize];
		int numRead = 0;
		while ((numRead = reader.read(chars)) > -1) {
			sb.append(String.valueOf(chars).substring(0, numRead));	
		}
		
		reader.close();
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.io.File)
	 */
	public String cleanScript(File sourceFile) throws IOException, CleanerException {
		return cleanScript(readFile(sourceFile));
	}
	
}
