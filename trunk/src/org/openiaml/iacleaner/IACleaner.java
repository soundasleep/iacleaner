/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple regular-expression based syntax formatter for Internet 
 * Applications: attempts to clean HTML, Javascript, CSS, PHP and other
 * languages.
 * 
 * @author Jevon
 *
 */
public class IACleaner {
	
	/**
	 * Format a web script using regular expressions.
	 * 
	 * @param script
	 * @return
	 */
	public String cleanScript(String script) {
		return "empty";
	}

	/**
	 * Format a file.
	 * 
	 * @see IACleaner#cleanScript(String)
	 * @param sourceFile
	 * @return
	 * @throws IOException if an IO exception occurs
	 */
	public String cleanScript(File sourceFile) throws IOException {
		int bufSize = 128;
		StringBuffer sb = new StringBuffer(bufSize);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile), bufSize);
				
		char[] chars = new char[bufSize];
		int numRead = 0;
		while ((numRead = reader.read(chars)) > -1) {
			sb.append(String.valueOf(chars).substring(0, numRead));	
		}
		
		reader.close();
		
		return cleanScript(sb.toString());
	}
	
}
