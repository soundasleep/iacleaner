/**
 * 
 */
package org.openiaml.iacleaner.inline;

import org.openiaml.iacleaner.IACleaner;

/**
 * Extends InlineStringWriter to throw warnings to a given
 * IACleaner instance.
 * 
 * @see org.openiaml.iacleaner.IACleaner#throwWarning(String, String)
 * @author Jevon
 *
 */
public class IACleanerStringWriter extends InlineStringWriter {

	private IACleaner cleaner;
	
	public IACleanerStringWriter(IACleaner cleaner) {
		super();
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
