/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * The expected interface for IACleaners.
 * 
 * @author Jevon
 *
 */
public interface IACleaner {

	/**
	 * Format a web script. Because there
	 * is no way to know what the file extension of the given
	 * script is, it is assumed to be PHP.
	 * 
	 * @param script The complete text of the web script to format
	 * @return The formatted web script
	 * @throws CleanerException if a formatting exception occurs
	 */
	public abstract String cleanScript(String script) throws CleanerException;

	/**
	 * Format a web script with extension 'extension'. 
	 * If the extension is unrecognised, format using PHP.
	 * 
	 * @param script The complete text of the web script to format
	 * @param extension The file extension of the web script, e.g. "php", "js"
	 * @return The formatted web script
	 * @throws CleanerException if a formatting exception occurs
	 */
	public abstract String cleanScript(String script, String extension) throws CleanerException;
	
	/**
	 * Throw a warning. Prints it out to stderr and adds it to
	 * {@link #getWarnings()}
	 * 
	 * @see #getWarnings()
	 * @param string the warning message
	 * @param context a reference to some context (for debugging)
	 */
	public abstract void throwWarning(String string, String context);

	/**
	 * Format the web script contained within a file. Does not
	 * actually modify the given file.
	 * 
	 * @see IARegexpCleaner#cleanScript(String)
	 * @param sourceFile the file to load from and format
	 * @return the formatted web script
	 * @throws IOException if an IO exception occurs while loading the file
	 * @throws CleanerException if a different formatting exception occurs
	 */
	public abstract String cleanScript(File sourceFile) throws IOException,
			CleanerException;

	/**
	 * Have any errors occured?
	 * 
	 * @see #getWarnings()
	 * @return true if there are any warnings stored in this instance
	 */
	public abstract boolean hasWarnings();

	/**
	 * Get all of the warnings thrown.
	 * 
	 * @return A list of all warnings thrown in the instance
	 */
	public abstract List<String> getWarnings();

	/**
	 * Format a web script from an InputStream. Because there
	 * is no way to know what the file extension of the given
	 * script is, it is assumed to be PHP.
	 * 
	 * @param script An input stream to format from
	 * @return The formatted web script
	 * @throws CleanerException if a formatting exception occurs
	 */
	public abstract String cleanScript(InputStream script) throws CleanerException;

	/**
	 * Format a web script from an InputStream with extension 'extension'. 
	 * If the extension is unrecognised, format using PHP.
	 * 
	 * @param script An input stream to format from
	 * @param extension The file extension of the web script, e.g. "php", "js"
	 * @return The formatted web script
	 * @throws CleanerException if a formatting exception occurs
	 */
	public abstract String cleanScript(InputStream script, String extension) throws CleanerException;
	
}