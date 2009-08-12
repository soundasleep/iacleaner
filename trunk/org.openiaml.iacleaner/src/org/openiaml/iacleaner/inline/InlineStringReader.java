/**
 * 
 */
package org.openiaml.iacleaner.inline;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

/**
 * <p>
 * We extend {@link PushbackReader} to allow us to "read ahead",
 * but still allow us to "unread" characters after reading them.
 * </p>
 * 
 * <p>
 * We also add a few convenience
 * methods, in particular, {@link #getLastChar()}, 
 * {@link #getLine} and {@link #readAheadSkipWhitespace(int)}.
 * </p>
 * 
 * @see PushbackReader#unread(int)
 * @author Jevon
 *
 */
public abstract class InlineStringReader extends PushbackReader {

	private static final int PUSHBACK_BUFFER_SIZE = 1024;
	private int lastChar = -1;
	
	private long lineNumber = 1;		/* keep track of newlines found */
	
	public InlineStringReader(String s) {
		super(new StringReader(s), PUSHBACK_BUFFER_SIZE);
	}

	/**
	 * What line number are we currently on?
	 * 
	 * @return
	 */
	public long getLine() {
		return lineNumber;
	}

	/**
	 * Override to support line numbers.
	 * 
	 * @see java.io.PushbackReader#unread(char[], int, int)
	 */
	@Override
	public void unread(char[] cbuf, int off, int len) throws IOException {
		super.unread(cbuf, off, len);
		for (int i = 0; i < len; i++) {
			if (cbuf[i+off] == '\n') {
				lineNumber--;
			}
		}
	}

	/**
	 * Override to support line numbers.
	 * 
	 * @see java.io.PushbackReader#unread(char[])
	 */
	@Override
	public void unread(char[] cbuf) throws IOException {
		super.unread(cbuf);
		for (int i = 0; i < cbuf.length; i++) {
			if (cbuf[i] == '\n') {
				lineNumber--;
			}
		}
	}

	/**
	 * Override to support line numbers.
	 * 
	 * @see java.io.PushbackReader#unread(int)
	 */
	@Override
	public void unread(int c) throws IOException {
		super.unread(c);
		if (c == '\n')
			lineNumber--;
	}

	/**
	 * Read ahead for the next <i>i</i> characters, excluding <b>leading</b>
	 * whitespace. Reads up to PUSHBACK_BUFFER_SIZE characters.
	 * 
	 * @param i number of characters to read ahead after leading whitespace
	 * @return
	 * @throws IOException 
	 */
	public String readAheadSkipWhitespace(int i) throws IOException {
		char[] buf = new char[PUSHBACK_BUFFER_SIZE];
		char[] result = new char[i];
		boolean startCounting = false;
		int j, k;
		for (j = 0, k = 0; j < buf.length; j++) {
			buf[j] = (char) read();
			if (!startCounting && !Character.isWhitespace(buf[j])) {
				startCounting = true;
			}
			if (startCounting) {
				result[k] = buf[j];
				k++;
				if (k == i) {
					// we found it
					// put back what we read
					unread(buf, 0, j + 1);
					return String.valueOf(result);
				}
			}
		}
		// return back what we had read back so far
		unread(buf, 0, j);
		// return whatever we found
		return String.valueOf(buf, 0, k);
	}

	/**
	 * Set the last character to a given character.
	 * This should ONLY be called if an external method implements readAhead manually.
	 * 
	 * @param oldLast
	 */
	public void setLastChar(int oldLast) {
		lastChar = oldLast;
	}

	/**
	 * Read the given number of characters into a String. Will return
	 * a shorter string if EOF is found.
	 * 
	 * @param i
	 * @return
	 * @throws IOException 
	 */
	public String read(int i) throws IOException {
		char[] result = new char[i];
		int read = read(result);
		return String.valueOf(result, 0, read);
	}

	/**
	 * What was the last character we read?
	 * 
	 * @return e.g. a newline, or 'a' etc
	 */
	public int getLastChar() {
		return lastChar;
	}

	/**
	 * "Read ahead" up to N characters, so we can see what is coming up.
	 * If EOF is reached, returns the remaining read-ahead.
	 * 
	 * @param n number of characters to read ahead
	 * @return the characters read, or null if we are EOF
	 * @throws IOException
	 */
	public String readAhead(int n) throws IOException {
		// don't modify the line number
		long oldLineNumber = lineNumber;
		try {
			int oldLast = lastChar;
			char[] c = new char[n];
			int found = read(c);
			if (found != -1) {				
				unread(c, 0, found); // push these characters back on
			}
			 
			lastChar = oldLast; // reset
			if (found == -1) {
				return null;
			} else {
				return String.valueOf(c).substring(0, found);
			}
		} finally {
			lineNumber = oldLineNumber;
		}
	}
	
	/**
	 * "Read ahead" 1 character, so we can see what is coming up.
	 * 
	 * @return the next character, or -1 if EOF
	 * @throws IOException
	 */
	public int readAhead() throws IOException {
		// don't modify the line number
		long oldLineNumber = lineNumber;
		try {
			int oldLast = lastChar;
			int found = read();
			if (found != -1)
				unread(found);
			lastChar = oldLast;	// reset
			return found;
		} finally {
			lineNumber = oldLineNumber;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.PushbackReader#read()
	 */
	@Override
	public int read() throws IOException {
		int c = super.read();
		lastChar = c;
		if (c == '\n')
			lineNumber++;
		return c;
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
	 * Read ahead only one character, skipping whitespace.
	 * 
	 * @see #readAheadSkipWhitespace(int)
	 * @return
	 * @throws IOException 
	 */
	public int readAheadSkipWhitespace() throws IOException {
		return readAheadSkipWhitespace(1).charAt(0);
	}

	/**
	 * Read ahead a number of characters, skipping ALL whitespace
	 * even inbetween the characters read.
	 * 
	 * @see #readAheadSkipWhitespace(int)
	 * @param i
	 * @return the string found, may be the empty string ""
	 * @throws IOException 
	 */
	public String readAheadSkipAllWhitespace(int i) throws IOException {
		// don't modify the line number
		long oldLineNumber = lineNumber;
		try {
			char[] buf = new char[PUSHBACK_BUFFER_SIZE];
			char[] result = new char[i];
			int j, k;
			for (j = 0, k = 0; j < buf.length; j++) {
				buf[j] = (char) read();
				if (!Character.isWhitespace(buf[j])) {
					result[k] = buf[j];
					k++;
					if (k == i) {
						// we found it
						// put back what we read
						unread(buf, 0, j + 1);
						return String.valueOf(result);
					}
				}
			}
			// return back what we had read back so far
			unread(buf, 0, j);
			// return whatever we found
			return String.valueOf(buf, 0, k);
		} finally {
			lineNumber = oldLineNumber;
		}
 	}

	/**
	 * Read 'i' characters, ignoring all whitespace, until we have
	 * read 'i' non-whitespace characters, or we have hit EOF.
	 * 
	 * @param i
	 * @throws IOException 
	 */
	public void skipAllWhitespace(int i) throws IOException {
		int cur;
		int nonws = 0;
		while ((cur = read()) != -1) {
			if (!Character.isWhitespace(cur)) {
				nonws++;				
				if (nonws == i)
					return;		// finished
			}
		}
	}

}
