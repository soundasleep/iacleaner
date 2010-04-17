/**
 * 
 */
package org.openiaml.iacleaner.inline;

import java.io.Reader;

import org.openiaml.iacleaner.IACleaner;

/**
 * Extends InlineStringReader to throw warnings to a given
 * IACleaner instance.
 * 
 * @see org.openiaml.iacleaner.IACleaner#throwWarning(String, String)
 * @author Jevon
 *
 */
public class IACleanerStringReader extends InlineStringReader {

	private IACleaner cleaner;
	
	public IACleanerStringReader(String script, IACleaner cleaner) {
		super(script);
		this.cleaner = cleaner;
	}
	
	public IACleanerStringReader(Reader reader,
			IACleaner cleaner) {
		super(reader);
		this.cleaner = cleaner;
	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.inline.InlineStringWriter#throwWarning(java.lang.String, java.lang.String)
	 */
	@Override
	protected void throwWarning(String message, String buffer) {
		cleaner.throwWarning(message, buffer);
	}

}
