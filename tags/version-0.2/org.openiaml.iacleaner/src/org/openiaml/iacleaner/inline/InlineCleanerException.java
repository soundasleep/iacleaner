/**
 * 
 */
package org.openiaml.iacleaner.inline;

import java.io.IOException;

import org.openiaml.iacleaner.CleanerException;

/**
 * Try to add additional information about the state based on
 * the custom classes we use.
 * 
 * In particular, this exception includes line information from
 * the reader.
 * 
 * @see InlineStringReader#getLine()
 * @author Jevon
 *
 */
public class InlineCleanerException extends CleanerException {

	private static final long serialVersionUID = 1L;

	/**
	 * Try to add additional knowledge to the exception from the 
	 * given reader.
	 * 
	 * @param string
	 * @param reader
	 * @throws IOException 
	 */
	public InlineCleanerException(String string, InlineStringReader reader) throws IOException {
		super("Line " + reader.getLine() + ": " + string + " [last='" + (char) reader.getLastChar() + "' following='" + reader.readAhead(32) + "']");
	}

}