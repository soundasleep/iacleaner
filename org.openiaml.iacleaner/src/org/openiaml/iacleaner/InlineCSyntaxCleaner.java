/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;

/**
 * An abstract class for C syntax-based cleaners.
 * 
 * @author Jevon
 *
 */
public abstract class InlineCSyntaxCleaner {

	public IAInlineCleaner inline;
	
	public InlineCSyntaxCleaner(IAInlineCleaner inline) {
		this.inline = inline;
	}
	
	public IAInlineCleaner getInline() {
		return inline;
	}

	/**
	 * A list of all the reserved words.
	 * 
	 * @see #isPreviousWordReserved(InlineStringWriter)
	 * @return
	 */
	public String[] getReservedWords() {
		return new String[] {
				"if",
				"for",
				"foreach",
				"while",
				"=>"
		};
	}
	
	/**
	 * A list of all words that should be inlined when in a brace,
	 * e.g. "if { ... } else { ... }".
	 * 
	 * @see #isNextWordInlineBrace(InlineStringReader, InlineStringWriter)
	 * @return
	 */
	public String[] getInlineBraceWords() {
		return new String[] {
				"else",
				"catch"
		};
	}
	
	/**
	 * Was the previously written word (separated by whitespace) a reserved word?
	 * 
	 * @see #getReservedWords()
	 * @param reader
	 * @return True if the previously written word was reserved.
	 * @throws IOException 
	 */
	public boolean isPreviousWordReserved(InlineStringWriter writer) throws IOException {
		// get maximum number of chars to go backwards
		int backwards = getReservedWords()[0].length();
		for (String s : getReservedWords()) {
			if (s.length() > backwards)
				backwards = s.length();
		}
		String previous = writer.getLastWritten(backwards + 1);
		for (String s : getReservedWords()) {
			// must be at least 1 character longer than the word
			if (previous.length() > s.length()) {
				int prev = previous.charAt(backwards - s.length());
				if (previous.endsWith(s) && (Character.isWhitespace(prev) || prev == 0)) {
					// the last thing we wrote was this reserved word
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * We have just hit an inline comment '//'; skip through until the end of the comment
	 * 
	 * @param reader
	 * @param writer
	 * @param allowSwitchToPhpMode can we switch to PHP mode within this inline comment?
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void jumpOverInlineComment(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
		try {
			writer.enableWordwrap(false);	// don't wordwrap this comment!
			int cur = -1;
			while ((cur = reader.read()) != -1) {
				// allow switch to PHP mode on getting "<?php"?
				if (allowSwitchToPhpMode) {
					if (getInline().didSwitchToPhpMode(reader, writer, cur)) {
						// resume
						continue;
					}
				}
				
				if (cur == '\r') {
					// ignore
					continue;
				}
				if (cur == '\n') {
					// at the end of the string
					writer.newLine();
					return;
				}
				
				// write the character as normal
				writer.write(cur);
			}
			// it's ok if this is the end of the file
		} finally {
			writer.enableWordwrap(true);
		}
	}

	/**
	 * Should we ignore whitespace after the given character? Called from PHP mode.
	 * 
	 * @param c
	 * @return
	 */
	public boolean shouldIgnoreWhitespaceAfter(int c) {
		return c == '{' || c == '(' || c == ')' || c == '}' || c == ';' || c == '"' || c == '\'' || c == '.';
	}
	
	/**
	 * Do we require whitespace after for the given character?
	 * 
	 * @param c
	 * @return
	 */
	public boolean needsWhitespaceCharacterAfter(int c) {
		return c == ',';
	}

	/**
	 * We have just hit an inline comment '/*'; skip through until the end of the comment
	 * 
	 * @param reader
	 * @param writer
	 * @param allowSwitchToPhpMode can we switch to PHP mode within this comment?
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void jumpOverBlockComment(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
		try {
			writer.enableWordwrap(false);	// don't wordwrap this comment!
			int cur = -1;
			boolean isBlankLine = false;
			boolean isJavadoc = (reader.readAhead() == '*');
			while ((cur = reader.read()) != -1) {
				// allow switch to PHP mode on getting "<?php"?
				if (allowSwitchToPhpMode) {
					if (getInline().didSwitchToPhpMode(reader, writer, cur)) {
						// resume
						continue;
					}
				}
				
				if (cur == '*' && reader.readAhead() == '/') {
					// if this is a javadoc character, and it is the first one, and this is a
					// javadoc comment, write an extra space to properly pad out the comment
					if (cur == '*' && isBlankLine && isJavadoc) {
						writer.write(' ');
					}
					
					// at end of comment
					writer.write(cur);	// write '*'
					writer.write(reader.read());	// write '/'
					return;
				}
				// ignore \rs
				if (cur == '\r') {
					continue;
				}
				
				if (Character.isWhitespace(cur) && isBlankLine) {
					// don't write extra padding for comments indented
					// ignore
				} else {
					// if this is a javadoc character, and it is the first one, and this is a
					// javadoc comment, write an extra space to properly pad out the comment
					if (cur == '*' && isBlankLine && isJavadoc) {
						writer.write(' ');
					}
					
					// write the character as normal
					writer.write(cur);
	
					if (cur == '\n') {
						isBlankLine = true;
					} else {
						isBlankLine = false;
					}
	
				}
	
			}
			// its NOT ok if this is end of file
			throw new InlineCleanerException("At end of file before found end of PHP block comment", reader);
		} finally {
			writer.enableWordwrap(true);
		}
	}

	/**
	 * The last character was a '}' indicating end-of-block. Should we
	 * add a new line after this brace? Or is the next term part of an
	 * inline statement (e.g. if/else/elseif)?
	 * 
	 * @see #getInlineBraceWords()
	 * @param reader
	 * @param writer
	 * @return True if we shouldn't output a new line
	 * @throws IOException 
	 */
	public boolean isNextWordInlineBrace(InlineStringReader reader,
			InlineStringWriter writer) throws IOException {
		// get maximum number of chars to go backwards
		int max = getInlineBraceWords()[0].length();
		for (String s : getInlineBraceWords()) {
			if (s.length() > max)
				max = s.length();
		}
		String next = (char) reader.getLastChar() + reader.readAheadSkipWhitespace(max + 1);
		for (String s : getInlineBraceWords()) {
			// must be at least 1 character longer than the word
			if (next.length() > s.length() + 1) {
				// does it start with the given reserved word?
				if (s.equals( next.substring(0, s.length()) )) {
					// the word matches... the next character cannot
					// be a alphanumeric character
					if (!Character.isLetterOrDigit(next.charAt(s.length()))) {
						// the next whole word is the reserved word;
						// don't add a new line
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * The last character we read in PHP mode was '"'; skip through the string
	 * until we find the end of the string.
	 * 
	 * @param reader
	 * @param writer
	 * @param allowSwitchToPhpMode can we switch to PHP mode in this string?
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void jumpOverString(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
		try {
			writer.enableIndent(false);		// we don't want to indent the strings by accident
			writer.enableWordwrap(false);	// no wordwrap!
			int cur = -1;
			while ((cur = reader.read()) != -1) {
				// allow switch to PHP mode on getting "<?php"?
				if (allowSwitchToPhpMode) {
					if (getInline().didSwitchToPhpMode(reader, writer, cur)) {
						// resume
						continue;
					}
				}

				if (cur == '"') {
					// at the end of the string
					writer.write(cur);
					return;
				}
				
				// write the character as normal
				writer.write(cur);
	
				if (cur == '\\' && reader.readAhead() == '\\') {
					// skip \ escapes
					writer.write(reader.read());
				} else if (cur == '\\' && reader.readAhead() == '"') {
					// escaping the next string character
					writer.write(reader.read());
				}
			}
			throw new InlineCleanerException("PHP string did not terminate", reader);
		} finally {
			writer.enableIndent(true);
			writer.enableWordwrap(true);
		}
	}

	/**
	 * The last character we read in PHP mode was "'"; skip through the string
	 * until we find the end of the string.
	 * 
	 * @param reader
	 * @param writer
	 * @param allowSwitchToPhpMode can we switch to a new PHP block within this string?
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void jumpOverSingleString(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
		try {
			writer.enableIndent(false);		// we don't want to indent the strings by accident
			writer.enableWordwrap(false);
			int cur = -1;
			while ((cur = reader.read()) != -1) {
				// allow switch to PHP mode on getting "<?php"?
				if (allowSwitchToPhpMode) {
					if (getInline().didSwitchToPhpMode(reader, writer, cur)) {
						// resume
						continue;
					}
				}
				
				if (cur == '\'') {
					// at the end of the string
					writer.write(cur);
					return;
				}
				
				// write the character as normal
				writer.write(cur);
				
				if (cur == '\\' && reader.readAhead() == '\\') {
					// skip \ escapes
					writer.write(reader.read());
				} else if (cur == '\\' && reader.readAhead() == '\'') {
					// escaping the next string character
					writer.write(reader.read());
				}
			}
			throw new InlineCleanerException("PHP single-quoted string did not terminate", reader);
		} finally {
			writer.enableIndent(true);
			writer.enableWordwrap(true);
		}
	}
	
	/**
	 * Do these two characters combine into a two-character operator?
	 * e.g. <code>++</code>, <code>--</code>...
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isTwoCharacterOperator(int a, int b) {
		return (a == '+' && b == '+') || (a == '-' && b == '-') || (a == '!' && b == '!');
	}

	/**
	 * Is the given character a single-character operator?
	 * e.g. <code>+</code>, <code>-</code>, ...
	 * 
	 * @param a
	 * @return
	 */
	public boolean isOperator(int a) {
		return a == '+' || a == '-' || a == '*' || a == '/' || a == '^' || a == '>' || a == '<' || a == '=' || a == '!' || a == '&' || a == '|';
	}


}
