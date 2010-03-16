/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
		return readFile(new FileReader(sourceFile));
	}
	
	/**
	 * Read in an InputStream into a string.
	 * 
	 * @throws IOException if an IO exception occurs
	 */
	public static String readFile(InputStream stream) throws IOException {
		return readFile(new InputStreamReader(stream));
	}
	
	/**
	 * Read in a Reader into a string.
	 * 
	 * @throws IOException if an IO exception occurs
	 */
	public static String readFile(Reader sourceReader) throws IOException {
		int bufSize = 128;
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(sourceReader, bufSize);
		
		char[] chars = new char[bufSize];
		int numRead = 0;
		while ((numRead = reader.read(chars)) != -1) {
			sb.append(chars, 0, numRead);
		}

		reader.close();
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.io.File)
	 */
	public String cleanScript(File sourceFile) throws IOException, CleanerException {
		return cleanScript(sourceFile, getExtension(sourceFile));
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.io.File)
	 */
	public String cleanScript(File sourceFile, String extension) throws IOException, CleanerException {
		return cleanScript( new BufferedInputStream(new FileInputStream(sourceFile)), extension);
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.io.InputStream)
	 */
	public String cleanScript(InputStream stream) throws CleanerException {
		return cleanScript(stream, "php");
	}

	/**
	 * Get the case-sensitive file extension (separated by a period ".")
	 * of the given file (even if the file doesn't exist).
	 * 
	 * @param f
	 * @return "html", "php" etc, or "" if none is found
	 */
	public static String getExtension(File f) {
		if (f.getAbsolutePath().indexOf('.') != -1) {
			int pos = f.getAbsolutePath().lastIndexOf('.');
			return f.getAbsolutePath().substring(pos + 1);
		}
		return "";
	}
	
}
