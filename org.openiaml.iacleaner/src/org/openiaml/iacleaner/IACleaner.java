/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * The expected interface for IACleaners.
 * 
 * @author Jevon
 *
 */
public interface IACleaner {

	/**
	 * Format a web script using regular expressions. Because there
	 * is no way to know what the file extension of the given
	 * script is, it is assumed to be PHP.
	 * 
	 * @param script
	 * @return
	 * @throws CleanerException if an exception occurs
	 */
	public abstract String cleanScript(String script) throws CleanerException;

	/**
	 * Format a web script with extension 'extension' using regular
	 * expressions. If the extension is unrecognised, format
	 * using PHP.
	 * 
	 * @param script
	 * @param extension
	 * @return
	 * @throws CleanerException
	 */
	public abstract String cleanScript(String script, String extension) throws CleanerException;
	
	/**
	 * Throw a warning. Prints it out to stderr and adds it to
	 * {@link #getWarnings()}
	 * 
	 * @see #getWarnings()
	 * @param string
	 */
	public abstract void throwWarning(String string, String context);

	/**
	 * Format a file.
	 * 
	 * @see IARegexpCleaner#cleanScript(String)
	 * @param sourceFile
	 * @return
	 * @throws IOException if an IO exception occurs
	 * @throws CleanerException if a cleaner exception occurs
	 */
	public abstract String cleanScript(File sourceFile) throws IOException,
			CleanerException;

	/**
	 * Have any errors occured?
	 * 
	 * @see #getWarnings()
	 * @return
	 */
	public abstract boolean hasWarnings();

	/**
	 * Get all of the warnings thrown.
	 * 
	 * @return
	 */
	public abstract List<String> getWarnings();

}