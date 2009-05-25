package org.openiaml.iacleaner;

/**
 * An exception to throw if something has gone wrong while trying to
 * format the code.
 * 
 * @author Jevon
 *
 */
public class CleanerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String source = null;

	public CleanerException(String message) {
		super(message);
	}
	
	public CleanerException(String message, String context) {
		super(message + " [context=\"" + context + "\"]");
	}

	/**
	 * @param message
	 * @param context
	 * @param source
	 */
	public CleanerException(String message, String context, String source) {
		this(message, context);
		this.source = source;
	}
	
	public CleanerException(Throwable e, String source) {
		super(e.getMessage(), e);
		this.source = source;
	}

	/**
	 * Get the source of the cleaned page, or null if it has
	 * not been set.
	 * 
	 * @return the source of the cleaned page, or null
	 */
	public String getSource() {
		return source;
	}
}