/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;

/**
 * Common methods for both the PHP and Javascript inline cleaners.
 * 
 * @author Jevon
 *
 */
public class CommonPhpJavascriptCleaner {

	public IAInlineCleaner inline;
	
	public CommonPhpJavascriptCleaner(IAInlineCleaner inline) {
		this.inline = inline;
	}
	
	public IAInlineCleaner getInline() {
		return inline;
	}

	private String[] inlineBraceWordsPhp = new String[] {
		"else",
		"catch"
	};
	
	private String[] reservedWordsPhp = new String[] {
		"if",
		"for",
		"foreach",
		"while",
		"=>",
	};
	
	/**
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	protected boolean previousWordIsReservedWordPhp(InlineStringWriter writer) throws IOException {
		// get maximum number of chars to go backwards
		int backwards = reservedWordsPhp[0].length();
		for (String s : reservedWordsPhp) {
			if (s.length() > backwards)
				backwards = s.length();
		}
		String previous = writer.getLastWritten(backwards + 1);
		for (String s : reservedWordsPhp) {
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
	protected void jumpOverPhpInlineComment(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
	 * @param prev
	 * @return
	 */
	protected boolean ignoreWhitespaceAfterPhp(int prev) {
		return prev == '{' || prev == '(' || prev == ')' || prev == '}' || prev == ';' || prev == '"' || prev == '\'' || prev == '.';
	}
	
	/**
	 * Do we require whitespace after for the given character?
	 * 
	 * @param prev
	 * @return
	 */
	protected boolean needsWhitespaceCharacterPhp(int prev) {
		return prev == ',';
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
	protected void jumpOverPhpBlockComment(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
	 * @param reader
	 * @param writer
	 * @return True if we shouldn't output a new line
	 * @throws IOException 
	 */
	protected boolean isInlinePhpReservedWordAfterBrace(InlineStringReader reader,
			InlineStringWriter writer) throws IOException {
		// get maximum number of chars to go backwards
		int max = inlineBraceWordsPhp[0].length();
		for (String s : inlineBraceWordsPhp) {
			if (s.length() > max)
				max = s.length();
		}
		String next = (char) reader.getLastChar() + reader.readAheadSkipWhitespace(max + 1);
		for (String s : inlineBraceWordsPhp) {
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
	protected void jumpOverPhpString(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
	protected void jumpOverPhpSingleString(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
	 * Are these two characters a two-character Javascript operator?
	 * 
	 * isJavascriptOperator(a) == true
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean isJavascriptTwoCharacterOperator(int a, int b) {
		return (a == '+' && b == '+') || (a == '-' && b == '-') || (a == '!' && b == '!');
	}

	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	protected boolean isJavascriptOperator(int a) {
		return a == '+' || a == '-' || a == '*' || a == '/' || a == '^' || a == '>' || a == '<' || a == '=' || a == '!' || a == '&' || a == '|';
	}


}
