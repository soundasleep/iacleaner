/**
 * 
 */
package org.openiaml.iacleaner;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of IACleaner, which implements some of the 
 * warning methods.
 * 
 * @author Jevon
 *
 */
public abstract class IACleanerWithWarnings implements IACleaner {

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
}
