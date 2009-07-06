/**
 * 
 */
package org.openiaml.iacleaner.inline;

import java.io.IOException;

/**
 * @author Jevon
 *
 */
public abstract class InlineStringWriter extends DontWriteLinesOfJustSpaces {

	private int indent = 0;
	private static final String indentString = "  ";
	private boolean canWordWrap = true;
	private int col = 0;
	
	private static final int WRITE_BUFFER_SIZE = 1024;
	private int[] writeBuffer = new int[WRITE_BUFFER_SIZE];
	private int writeBufferPos = -1;	// last position written
	
	private boolean indentEnabled = true;		// turn off indenting
	
	private long lineCount = 1;		// number of newlines written so far
	
	/**
	 * Columns after this long will be wordwrapped when {@link #canWordWrap} is true.
	 */
	private static final int wordWrapCol = 79;	
	
    public InlineStringWriter() {
		super();
	}

	/**
	 * Is indentation currently enabled?
	 * 
	 * @return
	 */
	public boolean getIndentEnabled() {
		return indentEnabled;
	}

	/**
	 * Get the last written character, or \0 if no characters have been written yet
	 * 
	 * @return
	 */
	public int previous() {
		if (writeBufferPos == -1)
			return 0;
		return writeBuffer[writeBufferPos];
	}

	/**
	 * Enable/disable wordwrap where appropriate.
	 * 
	 * @param b
	 */
	public void enableWordwrap(boolean b) {
		canWordWrap = b;
	}

	public void enableIndent(boolean enabled) {
		indentEnabled = enabled;
	}

	/**
	 * Get the last i written characters. Up to WRITE_BUFFER_SIZE written 
	 * characters are stored in writeBuffer. If i characters haven't been written
	 * yet, will prefix the string with \0
	 * 
	 * @param i
	 * @return
	 * @throws IOException 
	 */
	public String getLastWritten(int i) throws IOException {
		if (i > WRITE_BUFFER_SIZE) {
			throw new IOException("Cannot go back further than WRITE_BUFFER_SIZE=" + WRITE_BUFFER_SIZE + " bytes");
		}
		char[] result = new char[i];
		int p = writeBufferPos - i + 1;
		if (p < 0) {
			p += WRITE_BUFFER_SIZE; // wrap around
		}
		for (int j = 0; j < i; j++) {
			result[j] = (char) writeBuffer[p];
			p++;
			if (p == WRITE_BUFFER_SIZE) {
				p = 0;		// go back to the start
			}
		}
		// return the result
		return String.valueOf(result);
	}

	/**
	 * Can this writer do word wrap automatically?
	 * Should be turned off when outputting strings, etc.
	 * 
	 * @return
	 */
	public boolean canWordWrap() {
		return canWordWrap;
	}

	/**
	 * Only write a new line if the previous line wasn't one.
	 */
	public void newLineMaybe() {
		if (previousChar != '\n' && !wordwrapOnNext)
			newLine();
	}

	/**
	 * Write a newline. 
	 */
	public void newLine() {
		write('\n');
	}

	/**
	 * Increase output indentation.
	 */
	public void indentIncrease() {
		indent++;
	}

	/**
	 * Decrease output indentation.
	 */
	public void indentDecrease() {
		indent--;
		if (indent < 0) {
			// we went too far!
			throwWarning("Fell out of indent after " + getLineCount() + " lines", getBuffer().toString());
			indent = 0;
		}
	}
	
	/**
	 * If we need to throw a warning, we need some way to report it
	 * back to whatever is using this InlineStringWriter.
	 * 
	 * @param message the warning message
	 * @param buffer the current writer buffer
	 */
	protected abstract void throwWarning(String message, String buffer);

	/**
	 * Get the number of lines printed already
	 * 
	 * @return
	 */
	public long getLineCount() {
		return lineCount;
	}

	int previousChar = -1;
	boolean wordwrapOnNext = false;

	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(int)
	 */
	@Override
	public void write(int c) {
		// increase line count for newlines
		if (c == '\n') {
			lineCount++;
		}
		
		if (canWordWrap() && wordwrapOnNext && c != ' ') {
			// need to do wordwrap indent now!
			wordwrapOnNext = false;
			newLine();	// will also do indent
			previousChar = -1;	// don't double indent
			write(getIndent());	// will update col
			// continue like normal
		}
		if (previousChar == '\n') {
			// indent?		
			previousChar = c;
			write(getIndent());	// will update col
		}
		if (canWordWrap()) {
			if (c == ' ' && col >= wordWrapCol) {				
				// start wordwrap
				wordwrapOnNext = true;	// write the indent next time
				previousChar = c;
				// don't write ' '
				return;
			}
		} 
		
		super.write(c);
		col++;
		previousChar = c;
		if (c == '\n') {
			col = 0;	// reset column
		}
		// save to written buffer
		writeBufferPos++;
		if (writeBufferPos >= WRITE_BUFFER_SIZE)
			writeBufferPos = 0;		// wrap
		writeBuffer[writeBufferPos] = c;
	}
	
	/**
	 * Get the previously written character.
	 * 
	 * @return
	 */
	public int getPrevious() {
		return previousChar;
	}

	/**
	 * Create the indent text, and return it.
	 * 
	 * Returns an empty string if {@link #indentEnabled} is false.
	 * 
	 * @return
	 */
	private String getIndent() {
		if (!indentEnabled) 
			return "";
				
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			buf.append(indentString);
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) {
		// TODO optimize
		for (int i = 0; i < len; i++) {
			write(cbuf[i + off]);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(java.lang.String, int, int)
	 */
	@Override
	public void write(String str, int off, int len) {
		// TODO optimize
		for (int i = 0; i < len; i++) {
			write(str.charAt(i + off));
		}
	}

	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(java.lang.String)
	 */
	@Override
	public void write(String str) {
		// TODO optimize
		write(str, 0, str.length());
	}
	
}
