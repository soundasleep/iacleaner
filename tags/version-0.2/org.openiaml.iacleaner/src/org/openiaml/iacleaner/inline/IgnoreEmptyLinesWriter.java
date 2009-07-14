/**
 * 
 */
package org.openiaml.iacleaner.inline;

import java.io.StringWriter;

/**
 * <p>
 * This extends a StringWriter so that if we try to write something like 
 * '<code>s a \n s s s \n s b</code>' (s = space), we actually write
 * '<code>s a \n \n s b</code>' (so we don't write lines of just spaces).
 * </p>
 * 
 * <p>
 * Any spaces at the end of the file will also be trimmed (but not newlines).
 * </p>
 *  
 * @author Jevon
 *
 */
public class IgnoreEmptyLinesWriter extends StringWriter {

	/**
	 * The longest buffer we will store in until we will be forced
	 * to flush it all to the writer.
	 */
	private static final int BUFFER_SIZE = 4096;
	
	private char[] buffer = new char[BUFFER_SIZE];
	private int pointer = 0;
	private boolean startBuffer = false;
	
	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(int)
	 */
	@Override
	public void write(int c) {
		if (c == '\n') {
			// ignore any buffer, just print a newline
			super.write(c);
			startBuffer = true;
			pointer = 0;
		} else if (c == ' ' && startBuffer) {
			// add to buffer
			buffer[pointer] = (char) c;
			pointer++;
		} else {
			// write out any buffer
			super.write(buffer, 0, pointer);
			startBuffer = false;
			pointer = 0;
			// write the actual character
			super.write(c);
		}
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
		write(str.toCharArray(), off, len);
	}

	/* (non-Javadoc)
	 * @see java.io.StringWriter#write(java.lang.String)
	 */
	@Override
	public void write(String str) {
		// TODO optimize
		write(str.toCharArray(), 0, str.length());
	}
	
}