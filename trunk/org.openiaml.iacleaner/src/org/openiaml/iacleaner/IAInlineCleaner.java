/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openiaml.iacleaner.inline.IACleanerStringReader;
import org.openiaml.iacleaner.inline.IACleanerStringWriter;
import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * <p>This cleaner implementation uses a much more efficient method of 
 * reading, parsing and writing the formatted output.</p> 
 * 
 * <p>Instead of keeping
 * the entire string in memory at once, two special buffered readers and
 * writers ({@link InlineStringReader} and {@link InlineStringWriter}) allow
 * the IACleaner to parse the string character-by-character in
 * real-time.</p>
 * 
 * <p>As a result, it is at least an order of magnitude faster than
 * {@link IARegexpCleaner}.</p>
 * 
 * @author Jevon
 *
 */
public class IAInlineCleaner extends DefaultIACleaner implements IACleaner {

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String, java.lang.String)
	 */
	public String cleanScript(String script, String extension) throws CleanerException {
		// put the script into a reader
		InlineStringReader reader = new IACleanerStringReader(script, this);
	
		return cleanScript(reader, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String, java.lang.String)
	 */
	public String cleanScript(InputStream stream, String extension) throws CleanerException {
		InlineStringReader reader = new IACleanerStringReader(new InputStreamReader(stream), this);

		return cleanScript(reader, extension);
	}
	
	/**
	 * Do the actual script read/write using our readers and writers.
	 * 
	 * @param reader
	 * @param writer
	 * @param extension the extension of the file; will be changed to lowercase for comparison
	 * @return the formatted script
	 * @throws CleanerException 
	 * @throws IOException 
	 */
	protected String cleanScript(InlineStringReader reader, String extension) throws CleanerException {
		// and it will output into the writer
		InlineStringWriter writer = new IACleanerStringWriter(this);
		
		// lowercase the extension
		extension = extension.toLowerCase();
		
		try {
			if (extension.equals("js")) {
				// straight to JS mode
				cleanHtmlJavascript(reader, writer, false);
			} else if (extension.equals("css")) {
				// straight to CSS mode
				cleanHtmlCss(reader, writer, false);
			} else {
				// default: PHP (which is also HTML)
				cleanHtmlBlock(reader, writer);
			}
		} catch (IOException e) {
			throw new CleanerException(e);
		}
		
		return writer.getBuffer().toString();
	}

	/**
	 * Clean up HTML code.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlBlock(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {

		while (removeHtmlTextSpacingUntil(reader, writer, '<') && reader.readAhead() != -1) {
			String next5 = reader.readAhead(5);
			if (next5.equals("<?xml")) {
				// xml mode!
				return;	// permanent change
			} else if (next5.equals("<?php")) {
				// php mode!
				cleanPhpBlock(reader, writer);
				// we may continue with html mode
			} else if (next5.substring(0, 4).equals("<!--")) {
				// comment mode!
				cleanHtmlComment(reader, writer);
				// we may continue with html mode
			} else {
				// tag mode!
				cleanHtmlTag(reader, writer).toLowerCase();
			}
		}
		
	}
	
	/**
	 * Should we ignore whitespace after the given character? Called from PHP mode.
	 * 
	 * @param prev
	 * @return
	 */
	private boolean ignoreWhitespaceAfterPhp(int prev) {
		return prev == '{' || prev == '(' || prev == ')' || prev == '}' || prev == ';' || prev == '"' || prev == '\'' || prev == '.';
	}
	
	/**
	 * Do we require whitespace after for the given character?
	 * 
	 * @param prev
	 * @return
	 */
	private boolean needsWhitespaceCharacterPhp(int prev) {
		return prev == ',';
	}
	
	/**
	 * Even though we've been told we need whitespace before this character,
	 * do we actually need it?
	 * @param writer 
	 * @param reader 
	 * 
	 * @param cur current character
	 * @return
	 * @throws IOException 
	 */
	private boolean doesntActuallyNeedWhitespaceBeforePhp(InlineStringReader reader, InlineStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '[' || cur == ']' || cur == ',' || cur == '+' || cur == '-' || writer.getPrevious() == '-' || writer.getPrevious() == '+' ||
			isPhpTwoCharacterOperator(cur, reader.readAhead()) ||
			(writer.getLastWritten(2).equals("::")) /* :: operator */ ||
			(writer.getLastWritten(2).equals("->")) /* -> operator */;
	}	
	
	/**
	 * Even though we've been told we need whitespace before this character,
	 * do we actually need it?
	 * @param writer 
	 * @param reader 
	 * 
	 * @param cur current character
	 * @return
	 * @throws IOException 
	 */
	private boolean doesntActuallyNeedWhitespaceBeforeJavascript(InlineStringReader reader, InlineStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '.' || cur == ',' || cur == '[' || cur == ']' || cur == '+' || cur == '-' || writer.getPrevious() == '-' || writer.getPrevious() == '+' ||
			isJavascriptTwoCharacterOperator(cur, reader.readAhead());
	}
	
	
	/**
	 * Even though we've been told we need whitespace before this character,
	 * do we actually need it?
	 * @param writer 
	 * @param reader 
	 * 
	 * @param cur current character
	 * @return
	 * @throws IOException 
	 */
	private boolean doesntActuallyNeedWhitespaceBeforeCss(InlineStringReader reader, InlineStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '.' || cur == ',' || cur == '[' || cur == ']' || cur == '+' || cur == '-' || cur == ':';
	}
	
	/**
	 * Do we need to add one piece of whitespace (' ') between characters
	 * 'a' and 'b' in PHP mode?
	 * 
	 * @param a previous character
	 * @param b current character
	 * @return
	 * @throws IOException 
	 */
	private boolean needsWhitespaceBetweenPhp(InlineStringReader reader, InlineStringWriter writer, int a, int b) throws IOException {
		return (a == ')' && b == '{') || (a == ',') || 
			(!isPhpOperator(a) && b == '=') || 
			(a == '=' && (b != '>' && b != '=')) || 
			(a == '.' && b != '=') || (b == '.') || (b == '?') || (a == '?') || 
			(b == '{') || (a != '(' && a != '!' && b == '!') || 
			(a != '+' && b == '+' && reader.readAhead() != '+') ||
			(a == '+' && b == '+' && reader.readAhead() == '+') ||
			(a != '-' && b == '-' && reader.readAhead() != '-' && reader.readAhead() != '>') ||
			(a == '-' && b == '-' && reader.readAhead() == '-') ||
			(a != '|' && b == '|') || 
			(a != '&' && b == '&') || 
			(b == '<' || (a != '-' && a != '=' && b == '>')) ||
			(isPhpOperator(a) && !isPhpOperator(b) && b != ')' && a != '!' && b != ';' && b != '$' && !writer.getLastWritten(2).equals("->") && !writer.getLastWritten(3).equals(", -") && !writer.getLastWritten(3).equals(", +")) ||
			(a == ')' && isPhpOperator(b)) ||
			(isPhpOperator(a) && a != '!' && b == '$') ||
			(b == '*') || (a == ')' && b == '-') ||
			(a == ']' && b == ':') || (a == ')' && b == ':') || /* between ): or ]: */
			(a == ':' && b != ':' && !writer.getLastWritten(2).equals("::") /* between :: */) ||
			(previousWordIsReservedWordPhp(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			(Character.isLetter(a) && b == '"' /* e.g. 'echo "...' */ ) ||
			(Character.isLetter(a) && b == '\'' /* e.g. 'echo '...' */ ) ||
			(Character.isLetter(a) && isPhpOperator(b) && !isPhpTwoCharacterOperator(b, reader.readAhead())) /* e.g. $f * $g */;
	}
	
	/**
	 * Do we need to add one piece of whitespace (' ') between characters
	 * 'a' and 'b' in PHP mode?
	 * 
	 * @param a previous character
	 * @param b current character
	 * @return
	 * @throws IOException 
	 */
	private boolean needsWhitespaceBetweenJavascript(InlineStringReader reader, InlineStringWriter writer, int a, int b) throws IOException {
		return (a == ')' && b == '{') || (a == ',') || 
			(!isJavascriptOperator(a) && b == '=') || 
			(a == '=' && (b != '>' && b != '=')) || 
			(b == '?') || (a == '?') || 
			(b == '{') || (a != '(' && a != '!' && b == '!') || 
			(a != '+' && b == '+' && reader.readAhead() != '+') ||
			(a == '+' && b == '+' && reader.readAhead() == '+') ||
			(a != '-' && b == '-' && reader.readAhead() != '-') ||
			(a == '-' && b == '-' && reader.readAhead() == '-') ||
			(a != '|' && b == '|') || 
			(a != '&' && b == '&') || 
			(b == '<' || b == '>') ||
			(isJavascriptOperator(a) && !isJavascriptOperator(b) && b != ')' && a != '!' && b != ';' && !writer.getLastWritten(3).equals(", -") && !writer.getLastWritten(3).equals(", +")) ||
			(a == ')' && isJavascriptOperator(b)) ||
			(b == '*') || (a == ')' && b == '-') ||
			(a == ']' && b == ':') || (a == ')' && b == ':') || /* between ): or ]: */
			(a == ':' && b != ':' && !writer.getLastWritten(2).equals("::") /* between :: */) ||
			(previousWordIsReservedWordPhp(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			((isJavascriptOperator(a) || Character.isLetter(a)) && b == '"' /* e.g. 'echo "...' */ ) ||
			((isJavascriptOperator(a) || Character.isLetter(a)) && b == '\'' /* e.g. 'echo '...' */ ) ||
			(Character.isLetter(a) && isJavascriptOperator(b) && !isJavascriptTwoCharacterOperator(b, reader.readAhead())) /* e.g. f * g */;
	}
	
	/**
	 * Are these two characters a two-character PHP operator?
	 * 
	 * isPhpOperator(a) == true
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isPhpTwoCharacterOperator(int a, int b) {
		return (a == '-' && b == '>') || isJavascriptTwoCharacterOperator(a, b);
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
	private boolean isJavascriptTwoCharacterOperator(int a, int b) {
		return (a == '+' && b == '+') || (a == '-' && b == '-') || (a == '!' && b == '!');
	}

	/**
	 * Do we need to add one piece of whitespace (' ') between characters
	 * 'a' and 'b' in CSS mode?
	 * 
	 * @param a previous character
	 * @param b current character
	 * @return
	 * @throws IOException 
	 */
	private boolean needsWhitespaceBetweenCss(InlineStringReader reader, InlineStringWriter writer, int a, int b) throws IOException {
		return (a == ',') || (a == '+') || (a == '<') || (b == '+') || (b == '<') || (a == '>') || (b == '>') || (b == '.') || (b == '{') || (a == ':');
	}
	
	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	private boolean isPhpOperator(int a) {
		return a == '.' || isJavascriptOperator(a);
	}
	
	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	private boolean isJavascriptOperator(int a) {
		return a == '+' || a == '-' || a == '*' || a == '/' || a == '^' || a == '>' || a == '<' || a == '=' || a == '!' || a == '&' || a == '|';
	}

	private String[] reservedWordsPhp = new String[] {
		"if",
		"for",
		"foreach",
		"while",
		"=>",
	};
	
	private String[] inlineBraceWordsPhp = new String[] {
		"else",
		"catch"
	};
	
	/**
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private boolean previousWordIsReservedWordPhp(InlineStringWriter writer) throws IOException {
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
	 * We need to read in a PHP script and output it as appropriate.
	 * Reader starts with "&lt;?php'. 
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanPhpBlock(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {
		// write in the php header as-is
		writer.write(reader.read(5));
		// add a space (unless we start with an inline comment)
		String next2 = reader.readAheadSkipWhitespace(2);
		
		boolean needsWhitespace = false;
		if (!next2.equals("//") && !next2.equals("/*")) {
			needsWhitespace = true;		// by default, we need whitespace
		}
		
		// following php code will be indented
		writer.indentIncrease();
		
		boolean needsLineBefore = false;
		boolean inInlineBrace = false;
		boolean inBracket = false;	// currently in a (...)?
		int charBeforeBlockComment = -1;	// any character before a block comment
		
		int cur = -1;
		int prev = ' ';
		int prevNonWhitespace = -1;
		boolean isOnBlankLine = false;	// is the current character the first character on a new line? the first line is "part" of <?php
		while ((cur = reader.read()) != -1) {
			if (cur == '?' && reader.readAhead() == '>') {
				// end of php mode
				// add a space
				writer.write(' ');
				writer.write(cur); // write '?'
				writer.write(reader.read()); // write '>'
				
				// handle trailing braces { }, which should still increase the indent etc
				if (prevNonWhitespace == '{') {
					// open a new block
					writer.newLineMaybe();
					writer.indentIncrease();
					needsWhitespace = false;
					inInlineBrace = false;
					prevNonWhitespace = -5;
				}
				
				writer.indentDecrease();	// end indent
				return;	// stop
			}
			
			if (cur == '/' && reader.readAhead() == '/') {
				// a single-line comment
				if (!isOnBlankLine && (prevNonWhitespace == ';')) {
					writer.write(' ');
					needsWhitespace = false;
				}
				if (prevNonWhitespace == '{') {
					// put this comment on a new line
					writer.newLine();
					// increase the indent because we're starting a new block
					// (and the code later won't be executed)
					writer.indentIncrease();
				} else if (prevNonWhitespace == -1) {
					// the first comment of the php block needs to be on a new line
					writer.newLine();
				} else if (prevNonWhitespace == '}') {
					// inline comment should be on a new line
					writer.newLine();
				} else if (isOnBlankLine) {
					// put this comment on a new line
					writer.newLineMaybe();
					isOnBlankLine = false;
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '/'
				jumpOverPhpInlineComment(reader, writer, false); 
				needsLineBefore = true; // we need a new line next line
				prevNonWhitespace = -3;	// reset to "did an inline comment"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '/' && reader.readAhead() == '*') {
				// a multi-line comment
				// write a whitespace before
				if (prevNonWhitespace == '{') {
					// put this comment on a new line
					writer.newLine();
					// increase the indent because we're starting a new block
					// (and the code later won't be executed)
					writer.indentIncrease();
				} else if (!isOnBlankLine && prevNonWhitespace != '(' && prevNonWhitespace != -1 && prevNonWhitespace != -3) {
					writer.write(' ');
				} else if (prevNonWhitespace == ';' || prevNonWhitespace == -1 || prevNonWhitespace == -2 || prevNonWhitespace == -1 || prevNonWhitespace == '}') {
					writer.newLine();
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '*'
				jumpOverPhpBlockComment(reader, writer, false);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '\n' || cur == '\r') {
				isOnBlankLine = true;
			}
			
			if (Character.isWhitespace(cur) && ignoreWhitespaceAfterPhp(prev)) {
				// print just a space if necessary
				if (needsWhitespaceCharacterPhp(prev)) {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur) && Character.isWhitespace(prev)) {
				// skip multiple whitespace
			} else if (Character.isWhitespace(cur) && !Character.isWhitespace(prev)) {
				// we _may_ actually need this whitespace
				if (prev != '[' && prev != '!') {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur)) {
				// ignore whitespace otherwise
			} else {
				// put a newline before?
				if (needsLineBefore) {
					writer.newLineMaybe();
					needsLineBefore = false;
				} 
				
				isOnBlankLine = false;
				if (prevNonWhitespace == ';') {
					if (inBracket) {
						// for (...; ...; ...)
						// just write a space
						writer.write(' ');
					} else {
						writer.newLine();
					}
				} else if (prevNonWhitespace == -2 && cur != ';' && cur != ',' && cur != ')' && cur != '{' && !Character.isWhitespace(cur) && charBeforeBlockComment != '(') {
					// previous statement was closing a */; new line
					// (but not when the next character is a ';')
					writer.newLineMaybe();
					needsWhitespace = false;
				} else if (prevNonWhitespace == '{') {
					// open a new block
					writer.newLineMaybe();
					writer.indentIncrease();
					needsWhitespace = false;
					inInlineBrace = false;
				} else if (prevNonWhitespace == '}') {
					// a new statement (like ;)
					if (cur == ',' || cur == ')') {
						// ignore 'function(){...}, x'
					} else if (!isInlinePhpReservedWordAfterBrace(reader, writer)) {
						// a normal ending brace
						writer.newLine();
					} else {
						// a term like 'else' after a brace
						writer.write(' ');
						inInlineBrace = true;
					}
				} else if (prevNonWhitespace == ']' && (cur == ')')) {
					// do nothing
				} else if (needsWhitespaceBetweenPhp(reader, writer, prevNonWhitespace, cur)) {
					writer.write(' ');
					needsWhitespace = false;
				} else if (needsWhitespace) {
					// needs whitespace from a previous separator character
					if (!doesntActuallyNeedWhitespaceBeforePhp(reader, writer, cur) && prevNonWhitespace != -3) {
						if (writer.getPrevious() != '\n') {
							// don't need to write whitespace when we just wrote
							// a new line
							writer.write(' ');
						}
					}
					needsWhitespace = false;
				} 
				
				if (cur == '}') {
					// close an existing block
					writer.indentDecrease();
					// only write a new line if we aren't starting the current block
					if (prevNonWhitespace != -1) {
						writer.newLineMaybe();
					} else {
						writer.write(' ');
					}
				}
				
				if (cur == '(' && inInlineBrace) {
					// in an inline brace like catch(...)
					writer.write(' ');
				}
				
				// write like normal
				writer.write(cur);
				
				if (cur == ';') {
					inInlineBrace = false;	// impossible to have inline braces later
				}
				if (cur == '(') {
					inBracket = true;
				} else if (cur == ')') {
					inBracket = false;
				}
				
				// switch into strings mode?
				if (cur == '"') {
					jumpOverPhpString(reader, writer, false);
				} else if (cur == '\'') {
					jumpOverPhpSingleString(reader, writer, false);
				}
				if (!Character.isWhitespace(cur)) {
					prevNonWhitespace = cur;
				}
				
			}
			prev = cur;
		}
		
		// handle trailing braces { }, which should still increase the indent etc
		if (prevNonWhitespace == '{') {
			// open a new block
			writer.newLineMaybe();
			writer.indentIncrease();
		}
		
		// it's ok to fall out of PHP mode
		writer.indentDecrease(); // end indent
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
	private boolean isInlinePhpReservedWordAfterBrace(InlineStringReader reader,
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
					if (didSwitchToPhpMode(reader, writer, cur)) {
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
					if (didSwitchToPhpMode(reader, writer, cur)) {
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
	 * The last character we read in Javascript mode was "/"; skip through the string
	 * until we find the end of the regexp "/". We also print out any
	 * regexp parameters that are attached to the regexp, e.g.
	 * "/regexp/ig".
	 * 
	 * @param reader
	 * @param writer
	 * @param allowSwitchToPhpMode can we switch to a new PHP block within this string?
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void jumpOverJavascriptRegexp(InlineStringReader reader, InlineStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
		try {
			writer.enableIndent(false);		// we don't want to indent the strings by accident
			int cur = -1;
			while ((cur = reader.read()) != -1) {
				// allow switch to PHP mode on getting "<?php"?
				if (allowSwitchToPhpMode) {
					if (didSwitchToPhpMode(reader, writer, cur)) {
						// resume
						continue;
					}
				}
				
				if (cur == '/') {
					// at the end of the regexp
					writer.write(cur);
					
					// any additional regexp parameters?
					while ((cur = reader.read()) != -1) {
						if (!Character.isLetter(cur)) 
							break;
						
						writer.write(cur); // yes it is
					}
					// put the last read character back on, since it's not part of the regexp
					reader.unread(cur);
					return;
				}
				
				// write the character as normal
				writer.write(cur);
	
				if (cur == '\\' && reader.readAhead() == '/') {
					// escaping the next regexp character
					writer.write(reader.read());
				}
			}
			throw new InlineCleanerException("PHP single-quoted string did not terminate", reader);
		} finally {
			writer.enableIndent(true);
		}
	}
	/**
	 * Try switching to PHP mode from the current point. 
	 * Returns true if the switch was successful, in which case
	 * the outside loop should issue 'continue;' to resume parsing.
	 * 
	 * @param reader
	 * @param writer
	 * @param cur
	 * @return
	 * @throws CleanerException 
	 * @throws IOException 
	 */
	private boolean didSwitchToPhpMode(InlineStringReader reader,
			InlineStringWriter writer, int cur) throws IOException, CleanerException {
		if (cur == '<' && reader.readAhead(4).equals("?php")) {
			// jump into php mode
			// we will assume we return successfully from it, otherwise
			// it's pretty much impossible to tell when script mode ends
			boolean oldIndent = writer.getIndentEnabled();
			writer.enableIndent(true);
			reader.unread('<');	// go backwards
			cleanPhpBlock(reader, writer);
			writer.enableIndent(oldIndent);
			
			// resume!
			return true;
		}
		
		// didn't do anything
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
					if (didSwitchToPhpMode(reader, writer, cur)) {
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
					if (didSwitchToPhpMode(reader, writer, cur)) {
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
	 * We need to read in a comment and output it as appropriate.
	 * Reader starts with '&lt;!--'.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlComment(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {
		// read until we find the end
		int cur = -1;
		boolean isNewline = false;
		boolean startedComment = false;
		boolean commentLine = false;
		while (true) {
			// is the last character we read before this non-whitespace?
			//writer.write(reader.getLastChar());
			//writer.write('!');
			if (!startedComment && Character.isWhitespace(reader.getLastChar())) {
				// this comment is on a new line				
				writer.newLineMaybe();
				commentLine = true;
			}
			startedComment = true;
			
			cur = reader.read();
			if (cur == -1)
				break;	// bail
			
			if (cur != '\n' && Character.isWhitespace(cur) && isNewline) {
				// don't double indent
			} else if (cur == '\r') {
				// ignore \rs
			} else if (cur == '\n') {
				// don't include newlines manually, allow indenting
				writer.newLine();
			} else {
				writer.write(cur);
				isNewline = false;
			}
			
			// do we now end a comment?
			if (reader.readAhead(3).equals("-->")) {
				// we've found the end of the comment
				writer.write(reader.read(3));
				// end the newline we started?
				if (commentLine)
					writer.newLine();
				return;
			}
			
			if (cur == '\n') {
				isNewline = true;
			}
		}
		throw new CleanerException("At end of file before found end of comment");
	}

	/**
	 * Should the given HTML tag be intented?
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagIndented(String tag) {
		if (tag.charAt(0) == '/' && tag.length() > 1)
			return htmlTagIndented(tag.substring(1));
		return tag.equals("html") || tag.equals("body") || tag.equals("ol") || tag.equals("ul") ||
			tag.equals("head") || tag.equals("p");
	}
	
	/**
	 * Does the given HTML tag need to be put on a new line?
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagNeedsNewLine(String tag) {
		return tag.equals("h1") || tag.equals("h2") || tag.equals("h3") ||
			tag.equals("h4") || tag.equals("h5") || tag.equals("h6") ||
			tag.equals("li") || tag.equals("title") || tag.equals("link") || 
			tag.equals("head") || tag.equals("body") || tag.equals("ol") || tag.equals("ul");
	}
	
	/**
	 * Does a new line need to be added at the end of this tag?
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagNeedsTrailingNewLine(String tag) {
		return tag.equals("/h1") || tag.equals("/li") || tag.equals("/title") || tag.equals("/link") || 
			tag.equals("/head") || tag.equals("/body") || tag.equals("/ol") || tag.equals("/ul") ||
			tag.equals("/script") || tag.startsWith("!");
	}

	/**
	 * Does the given HTML singleton tag (i.e. <tag />) 
	 * need to be appended with a new line?
	 * 
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagNeedsNewLineSingleton(String tag) {
		return htmlTagNeedsTrailingNewLine("/" + tag);
	}
	
	/**
	 * We have just started an &lt;htmlTag attr...&gt;. Clean the tag up, and return
	 * the 'htmlTag'.
	 * 
	 * @param reader
	 * @param writer
	 * @return
	 * @throws IOException 
	 * @throws CleanerException if we hit EOF unexpectedly
	 */
	protected String cleanHtmlTag(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {
		// get the first char <
		int first = reader.read();
		
		// first, find out what the tag name actually is, so we know whether to indent back
		// or not
		String tagName = findHtmlTagName(reader);
		if (tagName.isEmpty()) {
			// EOF
			throw new InlineCleanerException("Unexpectedly hit an invalid HTML tag while searching for HTML tags [first='" + (char)first + "']", reader);
		}
		
		if (tagName.charAt(0) == '/' && htmlTagIndented(tagName)) {
			// need to un-indent before
			writer.indentDecrease();
			writer.newLineMaybe();
		}

		// does this tag need to be placed on a new line?
		if (htmlTagNeedsNewLine(tagName)) {
			writer.newLineMaybe();
		}
		
		// write the first char < (AFTER sorting out the indent)
		writer.write(first);

		int cur;
		boolean doneTag = false;
		while ((cur = reader.read()) != -1) {
			if (cur == '>') {
				int prev = writer.getPrevious();
				
				// end of tag
				writer.write(cur);
				
				if (tagName.charAt(0) != '/' && htmlTagIndented(tagName)) {
					// need to indent afterwards
					writer.indentIncrease();
					writer.newLine();
				}
				
				// trailing newline?
				if (htmlTagNeedsTrailingNewLine(tagName)) {
					writer.newLine();
				} else if (prev == '/' && htmlTagNeedsNewLineSingleton(tagName)) {
					// it's a single tag <link />, do we need to
					// add a new line after here too?
					writer.newLine();
				}
				
				// should we step into any special modes?
				if (tagName.toLowerCase().equals("script")) {
					// javascript mode!
					cleanHtmlJavascript(reader, writer, true);
					// will continue when </script>
				} else if (tagName.toLowerCase().equals("style")) {
					// css mode!
					cleanHtmlCss(reader, writer, true);
					// will continue when </style>
				}
				
				return tagName;
			} else if (Character.isWhitespace(cur)) {
				// end of tag name or attribute
				if (!doneTag) {
					// ignore all initial whitespace
				} else {
					// we now want to clean attributes
					// put whitespace back on stack
					reader.unread(cur);
					cleanHtmlTagAttributes(reader, writer);
				}
			} else {
				// character data
				writer.write(cur);
				if (Character.isLetterOrDigit(cur) || cur == '_') {
					doneTag = true;		// we've done at least one character of the tag
				}
			}
		}
		
		// we never found the end of the tag >
		throwWarning("We never found the end of HTML tag", tagName.toString());
		return tagName.toString();
	}

	/**
	 * We are in HTML and we have just processed a tag
	 * '&lt;style ...&gt;'. We need to parse the inline CSS until
	 * we are about to hit '&lt;/style&gt;'.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlCss(InlineStringReader reader,
			InlineStringWriter writer, boolean withinHtml) throws IOException, CleanerException {
		// is it immediately </script>?
		if (withinHtml && readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/style")) {
			// we don't need to do anything
			return;
		}
		
		// following style code will be indented
		if (withinHtml) {
			writer.indentIncrease();
		}
		
		// always start with a newline (but let <script></script> remain as one line)
		// (only if we are in HTML mode)
		boolean needsNewLine = withinHtml;
		
		boolean needsLineBefore = false;
		int charBeforeBlockComment = -1;	// any character before a block comment
		
		int cur = -1;
		int prev = ' ';
		int prevNonWhitespace = -1;
		boolean needsWhitespace = false;
		boolean isOnBlankLine = false;	// is the current character the first character on a new line? the first line is "part" of <script>
		boolean doingDot = false;	// doing something like 'a.xxx'
		int braceCount = 0;	// currently in {...}
		while ((cur = reader.read()) != -1) {			
			if (cur == '/' && reader.readAhead() == '*') {
				// a multi-line comment
				// write a whitespace before
				if (prevNonWhitespace == '{') {
					// put this comment on a new line
					writer.newLine();
					// increase the indent because we're starting a new block
					// (and the code later won't be executed)
					writer.indentIncrease();
				} else if (!isOnBlankLine && prevNonWhitespace != '(' && prevNonWhitespace != -1 && prevNonWhitespace != -3) {
					writer.write(' ');
				} else if (prevNonWhitespace == -1 && !withinHtml) {
					// don't newline for initial out-of-html blocks
				} else if (prevNonWhitespace == ';' || prevNonWhitespace == -1 || prevNonWhitespace == -2 || prevNonWhitespace == -1 || prevNonWhitespace == '}') {
					writer.newLine();
					// this comment is on its own line
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '*'
				jumpOverPhpBlockComment(reader, writer, true);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				continue;
			}
			
			if (cur == '\n' || cur == '\r') {
				isOnBlankLine = true;
			}
			
			if (Character.isWhitespace(cur) && ignoreWhitespaceAfterPhp(prev)) {
				// print just a space if necessary
				if (needsWhitespaceCharacterPhp(prev)) {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur) && Character.isWhitespace(prev)) {
				// skip multiple whitespace
			} else if (Character.isWhitespace(cur) && !Character.isWhitespace(prev)) {
				// we _may_ actually need this whitespace
				if (prev != '[') {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur)) {
				// ignore whitespace otherwise
			} else {
				// put a newline before?
				if (needsLineBefore) {
					writer.newLineMaybe();
					needsLineBefore = false;
				} 
				
				if (needsNewLine) {
					writer.newLineMaybe();
					needsNewLine = false;
				}
				
				if (cur == '.' && isCssIdentifierCharacter(prevNonWhitespace) && !Character.isWhitespace(prev)) {
					// turn on "a.xxx" mode
					doingDot = true;
				}
				
				isOnBlankLine = false;
				if (prevNonWhitespace == ';') {
					writer.newLine();
				} else if (prevNonWhitespace == -2 && cur != ';' && cur != ',' && cur != ')' && cur != '{' && !Character.isWhitespace(cur) && charBeforeBlockComment != '(') {
					// previous statement was closing a */; new line
					// (but not when the next character is a ';')
					writer.newLineMaybe();
					needsWhitespace = false;
				} else if (prevNonWhitespace == '{') {
					// open a new block
					writer.newLineMaybe();
					writer.indentIncrease();
					needsWhitespace = false;
				} else if (prevNonWhitespace == '}') {
					// a new statement (like ;)
					writer.newLine();
				} else if (prevNonWhitespace == '.' && doingDot) {
					// we must not have whitespace between a.xxx
					doingDot = false;
				} else if (prevNonWhitespace == ':' && braceCount == 0) {
					// a colon outside of braces are part of "a:hover";, i.e.
					// must not have any whitespace
				} else if (!doingDot && needsWhitespaceBetweenCss(reader, writer, prevNonWhitespace, cur)) {
					writer.write(' ');
					needsWhitespace = false;
				} else if (needsWhitespace) {
					// needs whitespace from a previous separator character
					if (!doesntActuallyNeedWhitespaceBeforeCss(reader, writer, cur) && prevNonWhitespace != -3) {
						if (writer.getPrevious() != '\n') {
							// don't need to write whitespace when we just wrote
							// a new line
							writer.write(' ');
						}
					}
					needsWhitespace = false;
				} 
				
				if (cur == '}') {
					// close an existing block
					writer.indentDecrease();
					writer.newLineMaybe();
					braceCount--;
				} else if (cur == '{') {
					braceCount++;
				}
								
				// write like normal
				writer.write(cur);

				// switch into strings mode?
				if (cur == '"') {
					jumpOverPhpString(reader, writer, true);
				} else if (cur == '\'') {
					jumpOverPhpSingleString(reader, writer, true);
				}

				if (!Character.isWhitespace(cur)) {
					prevNonWhitespace = cur;
				}

			}
			prev = cur;
			
			// is the next tag </style>?
			if (withinHtml) {
				String nextTag = readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
				if (nextTag.toLowerCase().equals("/style")) {
					// we want to go back to html mode
					
					writer.indentDecrease(); // end indent
					// always end with a newline
					if (!needsNewLine) {
						// dont write a new line if we haven't actually written anything in here
						writer.newLineMaybe();
					}
					return;
				}
			}
			
			if (reader.readAhead(5) != null && reader.readAhead(5).equals("<?php")) {
				if (isOnBlankLine) {
					writer.newLineMaybe();
				}
				
				if (prevNonWhitespace == '{') {
					writer.indentIncrease();
				}
				
				// we need to switch into php mode!
				// we expect the php block will end with a ?>,
				// otherwise how will we know when the </script> ends?
				cleanPhpBlock(reader, writer);
				
				if (prevNonWhitespace == '{') {
					writer.indentDecrease();
				}
			}
		}
		
		if (withinHtml) {
			// it's NOT ok to fall out of script mode; this means we never
			// found a </script>.
			throw new InlineCleanerException("Unexpectedly terminated out of CSS mode", reader);
		} else {
			// in a JS file, its perfectly OK
			return;
		}
	}


	/**
	 * Is the given character part of a CSS identifier?
	 * 
	 * @param cur
	 * @return
	 */
	private boolean isCssIdentifierCharacter(int cur) {
		return Character.isLetterOrDigit(cur) || cur == '_' || cur == '-';
	}

	/**
	 * We are in HTML and we have just processed a tag
	 * '&lt;script ...&gt;'. We need to parse the inline Javascript until
	 * we are about to hit '&lt;/script&gt;'.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlJavascript(InlineStringReader reader,
			InlineStringWriter writer, boolean withinHtml) throws IOException, CleanerException {
		// is it immediately </script>?
		if (withinHtml && readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/script")) {
			// we don't need to do anything
			return;
		}
		
		// following script code will be indented
		if (withinHtml) {
			writer.indentIncrease();
		}
		
		// always start with a newline (but let <script></script> remain as one line)
		// (only if we are in HTML mode)
		boolean needsNewLine = withinHtml;
		
		boolean needsLineBefore = false;
		boolean inInlineBrace = false;
		boolean inBracket = false;	// currently in a (...)?
		int charBeforeBlockComment = -1;	// any character before a block comment
		
		int cur = -1;
		int prev = ' ';
		int prevNonWhitespace = -1;
		boolean needsWhitespace = false;
		boolean isOnBlankLine = false;	// is the current character the first character on a new line? the first line is "part" of <script>
		boolean hadCdataBlock = false;
		while ((cur = reader.read()) != -1) {
			if (cur == '<' && "![CDATA[".equals(reader.readAheadSkipAllWhitespace(8))) {
				// CDATA block
				writer.newLineMaybe();
				writer.write("<![CDATA[");
				writer.newLine();
				reader.skipAllWhitespace(8);
				hadCdataBlock = true;
				needsNewLine = false;
				continue;
			}
			
			if (hadCdataBlock && cur == ']' && "]>".equals(reader.readAheadSkipAllWhitespace(2)) ) {
				// end of CDATA block
				writer.newLineMaybe();
				writer.write("]]>");
				reader.skipAllWhitespace(2);
				needsNewLine = false;
				hadCdataBlock = false;
				continue;
			}
			
			if (withinHtml && cur == '<') {
				// put < back on
				reader.unread(cur);
				
				if (readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/script")) {
					// we want to go back to html mode
					
					writer.indentDecrease(); // end indent
					// always end with a newline
					if (!needsNewLine) {
						// dont write a new line if we haven't actually written anything in here
						writer.newLineMaybe();
					}
					
					return;
				}
				
				reader.read();
			}
			
			if (cur == '/' && reader.readAhead() == '/') {
				// a single-line comment
				if (!isOnBlankLine && (prevNonWhitespace == ';')) {
					writer.write(' ');
					needsWhitespace = false;
				}
				if (prevNonWhitespace == '{') {
					// put this comment on a new line
					writer.newLine();
					// increase the indent because we're starting a new block
					// (and the code later won't be executed)
					writer.indentIncrease();
				} else if (prevNonWhitespace == -1 && !withinHtml) {
					// don't newline for initial out-of-html blocks
				} else if (prevNonWhitespace == -1) {
					// the first comment of the php block needs to be on a new line
					writer.newLine();
				} else if (prevNonWhitespace == '}') {
					// inline comment should be on a new line
					writer.newLine();
				} else if (isOnBlankLine) {
					// put this comment on a new line
					writer.newLineMaybe();
					isOnBlankLine = false;
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '/'
				jumpOverPhpInlineComment(reader, writer, true); 
				needsLineBefore = true; // we need a new line next line
				prevNonWhitespace = -3;	// reset to "did an inline comment"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '/' && reader.readAhead() == '*') {
				// a multi-line comment
				// write a whitespace before
				if (prevNonWhitespace == '{') {
					// put this comment on a new line
					writer.newLine();
					// increase the indent because we're starting a new block
					// (and the code later won't be executed)
					writer.indentIncrease();
				} else if (!isOnBlankLine && prevNonWhitespace != '(' && prevNonWhitespace != -1 && prevNonWhitespace != -3) {
					writer.write(' ');
				} else if (prevNonWhitespace == -1 && !withinHtml) {
					// don't newline for initial out-of-html blocks
				} else if (prevNonWhitespace == ';' || prevNonWhitespace == -1 || prevNonWhitespace == -2 || prevNonWhitespace == -1 || prevNonWhitespace == '}') {
					writer.newLine();
					// this comment is on its own line
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '*'
				jumpOverPhpBlockComment(reader, writer, true);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '\n' || cur == '\r') {
				isOnBlankLine = true;
			}
			
			if (Character.isWhitespace(cur) && ignoreWhitespaceAfterPhp(prev)) {
				// print just a space if necessary
				if (needsWhitespaceCharacterPhp(prev)) {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur) && Character.isWhitespace(prev)) {
				// skip multiple whitespace
			} else if (Character.isWhitespace(cur) && !Character.isWhitespace(prev)) {
				// we _may_ actually need this whitespace
				if (prev != '[' && prev != '!') {
					needsWhitespace = true;
				}
			} else if (Character.isWhitespace(cur)) {
				// ignore whitespace otherwise
			} else {
				// put a newline before?
				if (needsLineBefore) {
					writer.newLineMaybe();
					needsLineBefore = false;
				} 
				
				if (needsNewLine) {
					writer.newLineMaybe();
					needsNewLine = false;
				}
				
				isOnBlankLine = false;
				if (prevNonWhitespace == ';') {
					if (inBracket) {
						// for (...; ...; ...)
						// just write a space
						writer.write(' ');
					} else {
						writer.newLine();
					}
				} else if (prevNonWhitespace == -2 && cur != ';' && cur != ',' && cur != ')' && cur != '{' && !Character.isWhitespace(cur) && charBeforeBlockComment != '(') {
					// previous statement was closing a */; new line
					// (but not when the next character is a ';')
					writer.newLineMaybe();
					needsWhitespace = false;
				} else if (prevNonWhitespace == '{') {
					// open a new block
					writer.newLineMaybe();
					writer.indentIncrease();
					needsWhitespace = false;
					inInlineBrace = false;
				} else if (prevNonWhitespace == '}') {
					// a new statement (like ;)
					if (cur == ',' || cur == ')' || cur == ';') {
						// ignore 'function(){...}, x'
					} else if (!isInlinePhpReservedWordAfterBrace(reader, writer)) {
						// a normal ending brace
						writer.newLine();
					} else {
						// a term like 'else' after a brace
						writer.write(' ');
						inInlineBrace = true;
					}
				} else if (prevNonWhitespace == ']' && (cur == ')')) {
					// do nothing
				} else if (prevNonWhitespace != -4 && needsWhitespaceBetweenJavascript(reader, writer, prevNonWhitespace, cur)) {
					writer.write(' ');
					needsWhitespace = false;
				} else if (needsWhitespace) {
					// needs whitespace from a previous separator character
					if (!doesntActuallyNeedWhitespaceBeforeJavascript(reader, writer, cur) && prevNonWhitespace != -3) {
						if (writer.getPrevious() != '\n') {
							// don't need to write whitespace when we just wrote
							// a new line
							writer.write(' ');
						}
					}
					needsWhitespace = false;
				} 
				
				if (cur == '}') {
					// close an existing block
					writer.indentDecrease();
					writer.newLineMaybe();
				}
				
				if (cur == '(' && inInlineBrace) {
					// in an inline brace like catch(...)
					writer.write(' ');
				}
				
				// write like normal
				writer.write(cur);

				if (cur == ';') {
					inInlineBrace = false;	// impossible to have inline braces later
				}
				if (cur == '(') {
					inBracket = true;
				} else if (cur == ')') {
					inBracket = false;
				}
				
				boolean didRegexpMode = false;
				// switch into strings mode?
				if (cur == '"') {
					jumpOverPhpString(reader, writer, true);
				} else if (cur == '\'') {
					jumpOverPhpSingleString(reader, writer, true);
				} else if (cur == '/' && (prevNonWhitespace == '=' || prevNonWhitespace == '(' || prevNonWhitespace == '.' || prevNonWhitespace == ':')) {
					// regexp
					jumpOverJavascriptRegexp(reader, writer, true);
					prevNonWhitespace = -4;
					needsWhitespace = false;
					didRegexpMode = true;
				}

				if (!Character.isWhitespace(cur) && !didRegexpMode) {
					prevNonWhitespace = cur;
				}

			}
			prev = cur;
			
			// is the next tag </script>?
			if (withinHtml) {
				String nextTag = readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
				if (nextTag != null && "/script".equals(nextTag.toLowerCase())) {
					// we want to go back to html mode
					
					writer.indentDecrease(); // end indent
					// always end with a newline
					if (!needsNewLine) {
						// dont write a new line if we haven't actually written anything in here
						writer.newLineMaybe();
					}
					return;
				}
			}
			
			if (reader.readAhead(5) != null && reader.readAhead(5).equals("<?php")) {
				if (isOnBlankLine) {
					writer.newLineMaybe();
				}
				
				if (prevNonWhitespace == '{') {
					writer.indentIncrease();
				}
				
				// we need to switch into php mode!
				// we expect the php block will end with a ?>,
				// otherwise how will we know when the </script> ends?
				cleanPhpBlock(reader, writer);
				
				if (prevNonWhitespace == '{') {
					writer.indentDecrease();
				}
			}
		}
		
		if (withinHtml) {
			// it's NOT ok to fall out of script mode; this means we never
			// found a </script>.
			throw new InlineCleanerException("Unexpectedly terminated out of Javascript mode", reader);
		} else {
			// in a JS file, its perfectly OK
			return;
		}
	}

	/**
	 * We are in an HTML tag and we have just written
	 * '&lt;a'; we now want to parse and clean any attributes.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlTagAttributes(InlineStringReader reader,
			InlineStringWriter writer) throws IOException, CleanerException {
		
		int cur = -1;
		int prev = -1;
		boolean needWhitespace = false;
		boolean ignoreWhitespaceAfter = false;
		while ((cur = reader.read()) != -1) {
			if (Character.isWhitespace(cur)) {
				if (prev == -1) {
					// write initial whitespace
					needWhitespace = true;
				} else if (Character.isWhitespace(prev)) {
					// ignore multiple whitespace
				} else if (ignoreWhitespaceAfter) {
					// ignore whitespace after =
				} else {
					// write whitespace between attributes
					needWhitespace = true;
				}
			} else if (cur == '=') {
				writer.write(cur);
				// ignore any whitespace before and after this =
				needWhitespace = false;
				ignoreWhitespaceAfter = true;
			} else if (cur == '"' || cur == '\'') {
				// parse until end of string
				if (needWhitespace) {
					writer.write(' ');
				}
				writer.write(cur);
				jumpOverHtmlAttributeString(reader, writer, cur, true);
				ignoreWhitespaceAfter = false;
				needWhitespace = true;		// any further attributes needs whitespace
			} else if (cur == '<' && "?php".equals(reader.readAhead(4))) {
				// starting PHP mode
				if (needWhitespace) {
					writer.write(' ');
					needWhitespace = false;
				}
				reader.unread(cur);	// put '<' back on the stack
				cleanPhpBlock(reader, writer);
				
				// will return out of PHP block back into attributes mode
			} else {
				// we are reading an attribute "foo" or "foo=bar" or "foo=\"bar\""
				// do we need whitespace before?
				if (needWhitespace) {
					writer.write(' ');
					needWhitespace = false;
				}
				ignoreWhitespaceAfter = false;	// turn off
				writer.write(cur);
			}
			prev = cur;
			
			// is the next character >? if so, bail (the previous method is expecting it)
			if (reader.readAhead() == '>') {
				return;
			}
		}
		throw new InlineCleanerException("Expected > to end HTML tag while parsing for attributes", reader);
		
	}

	/**
	 * We are in an HTML tag, and we have hit a [stringCharacter]. We need to process
	 * until we find the end of the string.
	 * 
	 * @param reader
	 * @param writer
	 * @param stringCharacter either " or '
	 * @param allowJumpToPhp can we jump to PHP mode?
	 */
	protected void jumpOverHtmlAttributeString(InlineStringReader reader,
			InlineStringWriter writer, int stringCharacter, boolean allowJumpToPhp) throws IOException, CleanerException {
		try {
			// disable wordwrap, so we don't wrap strings in tags!
			writer.enableWordwrap(false);
			writer.enableIndent(false);
			int cur = -1;
			while ((cur = reader.read()) != -1) {
				if (cur == stringCharacter) {
					// at the end of the string
					writer.write(cur);
					return;
				}
				
				if (allowJumpToPhp && cur == '<' && reader.readAhead(4).equals("?php")) {
					// we can jump to PHP mode
					// stick '<' back into the stream
					reader.unread(cur);
					
					// jump into PHP mode
					cleanPhpBlock(reader, writer);
					continue;
				}
				
				// write the character as normal
				writer.write(cur);
	
				// there is no escaping in HTML
			}
			throw new InlineCleanerException("HTML Attribute string did not terminate", reader);
		} finally {
			// re-enable wordwrap
			writer.enableWordwrap(true);
			writer.enableIndent(true);
		}
	}

	/**
	 * Read ahead in the stream 'html...>...' and find the current HTML tag.
	 * Also includes formatted attributes. TODO link
	 * 
	 * If this returns an empty string, it may mean EOF, or the stream
	 * begins with [A-Za-z0-9_\-/].
	 * 
	 * @see InlineStringReader#readAheadUntilEndHtmlTag()
	 * @param reader
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	private String findHtmlTagName(InlineStringReader reader) throws IOException, CleanerException {
		return readAheadUntilEndHtmlTag(reader);
	}

	/**
	 * Remove space around HTML text until we find the character '<'
	 * (don't include this in the output)
	 * 
	 * This also controls the formatting of the output text.
	 * 
	 * @param reader
	 * @param writer
	 * @param c
	 * @return true if there is more text to go, or false at EOF
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	private boolean removeHtmlTextSpacingUntil(InlineStringReader reader,
			InlineStringWriter writer, char c) throws IOException, CleanerException {
		
		int cur;
		int prev = -1;
		boolean addWhitespace = false;
		while ((cur = reader.readAhead()) != -1) {
			if (cur == '<') {
				// we're done
				// do we need to add previous whitespace?
				if (addWhitespace) {
					// read ahead to find the next tag
					// eat the <
					String nextTag = readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
					if (nextTag.isEmpty() || (nextTag.charAt(0) != '/' && nextTag.charAt(0) != '!')) {
						writer.write(' ');
						addWhitespace = false;
					}
				}
				return true;
			}
			
			// skip multiple whitespace
			reader.read();	// eat a char
			if (Character.isWhitespace(cur)) {
				if (Character.isWhitespace(prev)) {
					// ignore multiple whitespace inline
				} else if (prev == -1) {
					// ignore initial whitespace
				} else if (reader.readAhead() == '<' && (reader.readAhead(2).charAt(1) == '/' || reader.readAhead(2).charAt(1) == '!')) {
					// ignore trailing whitespace at end of tags
				} else {
					// write only a space character (newlines aren't copied)
					addWhitespace = true;	// add the whitespace later
				}
			} else {
				// add previous whitespace?
				if (addWhitespace) {
					writer.write(' ');
					addWhitespace = false;
				}
				// write as normal
				writer.write(cur);
			}
			prev = cur;
		}
		
		return false;		
	}
	
	/**
	 * Same as {@link #readAheadUntilEndHtmlTag(org.openiaml.iacleaner.IAInlineCleaner.InlineStringReader)},
	 * except this skips over a '<' in front.
	 * 
	 * @param reader
	 * @param writer
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected String readAheadUntilEndHtmlTagWithOpenBrace(InlineStringReader reader,
			InlineStringWriter writer) throws IOException, CleanerException {
		int oldChar = reader.getLastChar();
		int cur = reader.read();	// consume <
		String nextTag = readAheadUntilEndHtmlTag(reader);
		reader.unread(cur); 		// put the < back
		reader.setLastChar(oldChar);
		return nextTag;
	}

	/**
	 * We want to read ahead, and see what the next HTML tag is.
	 * 
	 * Read ahead until we find something outside [A-Za-z0-9_\-/!], ignoring
	 * leading whitespace.
	 * 
	 * Will only read up to 512 characters into the stream.
	 * 
	 * Return the text found, or throws an exception.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected String readAheadUntilEndHtmlTag(InlineStringReader reader) throws IOException, CleanerException {
		int oldLast = reader.getLastChar();
		char[] buffer = new char[512];	// to unread back to reader
		char[] retBuffer = new char[512];	// to return

		int i = -1;	// pos in buffer
		int j = -1;	// pos in retBuffer
		int cur;
		
		while ((cur = reader.read()) != -1) {
			i++;
			buffer[i] = (char) cur;
			if (Character.isWhitespace(cur) && j == -1) {
				// leading whitespace: skip
				continue;
			}
			
			j++;
			retBuffer[j] = (char) cur;
			if (!(Character.isLetterOrDigit(cur) || cur == '_' || cur == '-' || cur == '/' || cur == '!')) {
				// we found it
				reader.unread(buffer, 0, i + 1);
				reader.setLastChar(oldLast); // reset
				return new String(retBuffer, 0, j /* don't include the last */);
			}
		}
		// return the entire buffer
		if (i > 0) {
			throw new InlineCleanerException("Could not read until end of HTML tag; never found end tag. Buffer = " + String.valueOf(buffer).substring(0, i - 1), reader);
		} else {
			throw new InlineCleanerException("Could not read until end of HTML tag; never found end tag. Buffer is empty", reader);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String)
	 */
	public String cleanScript(String script) throws CleanerException {
		return cleanScript(script, "php");
	}

}
