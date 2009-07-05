/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * @author Jevon
 *
 */
public class IAInlineCleaner extends DefaultIACleaner implements IACleaner {

	/**
	 * Try to add additional information about the state based on
	 * the custom classes we use.
	 * 
	 * @author Jevon
	 *
	 */
	public class InlineCleanerException extends CleanerException {

		private static final long serialVersionUID = 1L;

		/**
		 * Try to add additional knowledge to the exception from the 
		 * given reader.
		 * 
		 * @param string
		 * @param reader
		 * @throws IOException 
		 */
		public InlineCleanerException(String string, MyStringReader reader) throws IOException {
			super(string + " [last='" + (char) reader.getLastChar() + "' following='" + reader.readAhead(32) + "']");
		}

	}

	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String)
	 */
	@Override
	public String cleanScript(String script) throws CleanerException {
		
		// put the script into a reader
		MyStringReader reader = new MyStringReader(script);
		
		// and it will output into the writer
		MyStringWriter writer = new MyStringWriter();
		
		// we will assume we're in HTML mode
		try {
			cleanHtmlScript(reader, writer);
		} catch (IOException e) {
			throw new CleanerException(e, script);
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
	protected void cleanHtmlScript(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {

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
				if (reader.readAhead() != -1) {
					/*
					if (htmlTag.equals("/h1") || htmlTag.equals("/title") ||
							htmlTag.equals("/head")) { 
						writer.newLine();	// some tags need a new line
					}
					*/
				}
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
	private boolean doesntActuallyNeedWhitespaceBeforePhp(MyStringReader reader, MyStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '[' || cur == ']' || cur == ',' || cur == '+' || cur == '-' || writer.getPrevious() == '-' || writer.getPrevious() == '+' ||
			(writer.getLastWritten(2).equals("::")) /* :: operator */ ||
			(cur == '-' && reader.readAhead() == '>') /* -> operator */ ||
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
	private boolean doesntActuallyNeedWhitespaceBeforeJavascript(MyStringReader reader, MyStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '.' || cur == ',' || cur == '[' || cur == ']' || cur == '+' || cur == '-' || writer.getPrevious() == '-' || writer.getPrevious() == '+';
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
	private boolean needsWhitespaceBetweenPhp(MyStringReader reader, MyStringWriter writer, int a, int b) throws IOException {
		return (a == ')' && b == '{') || (a == ',') || 
			(!isJavascriptOperator(a) && b == '=') || 
			(a == '=' && (b != '>' && b != '=')) || 
			(a == '.') || (b == '.') || (b == '?') || (a == '?') || 
			(b == '{') || (a != '(' && b == '!') || 
			(a != '+' && b == '+' && reader.readAhead() != '+') ||
			(a == '+' && b == '+' && reader.readAhead() == '+') ||
			(a != '-' && b == '-' && reader.readAhead() != '-' && reader.readAhead() != '>') ||
			(a == '-' && b == '-' && reader.readAhead() == '-') ||
			(a != '|' && b == '|') || 
			(a != '&' && b == '&') || 
			(b == '<' || (a != '-' && a != '=' && b == '>')) ||
			(isJavascriptOperator(a) && !isJavascriptOperator(b) && b != ')' && a != '!' && b != ';' && b != '$' && !writer.getLastWritten(2).equals("->") && !writer.getLastWritten(3).equals(", -") && !writer.getLastWritten(3).equals(", +")) ||
			(a == '*') ||
			(b == '*') || (a == ')' && b == '-') ||
			(a == ']' && b == ':') || (a == ')' && b == ':') || /* between ): or ]: */
			(a == ':' && b != ':' && !writer.getLastWritten(2).equals("::") /* between :: */) ||
			(previousWordIsReservedWordPhp(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			(Character.isLetter(a) && b == '"' /* e.g. 'echo "...' */ ) ||
			(Character.isLetter(a) && b == '\'' /* e.g. 'echo '...' */ );
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
	private boolean needsWhitespaceBetweenJavascript(MyStringReader reader, MyStringWriter writer, int a, int b) throws IOException {
		return (a == ')' && b == '{') || (a == ',') || 
			(!isJavascriptOperator(a) && b == '=') || 
			(a == '=' && (b != '>' && b != '=')) || 
			(b == '?') || (a == '?') || 
			(b == '{') || (a != '(' && b == '!') || 
			(a != '+' && b == '+' && reader.readAhead() != '+') ||
			(a == '+' && b == '+' && reader.readAhead() == '+') ||
			(a != '-' && b == '-' && reader.readAhead() != '-') ||
			(a == '-' && b == '-' && reader.readAhead() == '-') ||
			(a != '|' && b == '|') || 
			(a != '&' && b == '&') || 
			(b == '<' || b == '>') ||
			(isJavascriptOperator(a) && !isJavascriptOperator(b) && b != ')' && a != '!' && b != ';' && !writer.getLastWritten(3).equals(", -") && !writer.getLastWritten(3).equals(", +")) ||
			(a == '*') ||
			(b == '*') || (a == ')' && b == '-') ||
			(a == ']' && b == ':') || (a == ')' && b == ':') || /* between ): or ]: */
			(a == ':' && b != ':' && !writer.getLastWritten(2).equals("::") /* between :: */) ||
			(previousWordIsReservedWordPhp(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			((isPhpOperator(a) || Character.isLetter(a)) && b == '"' /* e.g. 'echo "...' */ ) ||
			((isPhpOperator(a) || Character.isLetter(a)) && b == '\'' /* e.g. 'echo '...' */ );
	}
	
	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	private boolean isPhpOperator(int a) {
		return a == '+' || a == '-' || a == '*' || a == '/' || a == '>' || a == '<' || a == '=' || a == '!' || a == '&' || a == '|';
	}
	
	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	private boolean isJavascriptOperator(int a) {
		return isPhpOperator(a);
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
	private boolean previousWordIsReservedWordPhp(MyStringWriter writer) throws IOException {
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
	protected void cleanPhpBlock(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
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
		boolean lastBlockCommentWasOnLine = false;
		boolean inBracket = false;	// currently in a (...)?
		
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
				jumpOverPhpInlineComment(reader, writer); 
				needsLineBefore = true; // we need a new line next line
				prevNonWhitespace = -3;	// reset to "did an inline comment"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '/' && reader.readAhead() == '*') {
				// a multi-line comment
				// write a whitespace before
				lastBlockCommentWasOnLine = false;
				if (!isOnBlankLine && prevNonWhitespace != '(' && prevNonWhitespace != -1 && prevNonWhitespace != -3) {
					writer.write(' ');
				} else if (prevNonWhitespace == ';' || prevNonWhitespace == -1 || prevNonWhitespace == -2 || prevNonWhitespace == -1 || prevNonWhitespace == '}') {
					writer.newLine();
					// this comment is on its own line
					lastBlockCommentWasOnLine = true;
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '*'
				jumpOverPhpBlockComment(reader, writer);
				needsWhitespace = true;	// put a space before the next statement if necessary
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
				
				isOnBlankLine = false;
				if (prevNonWhitespace == ';') {
					if (inBracket) {
						// for (...; ...; ...)
						// just write a space
						writer.write(' ');
					} else {
						writer.newLine();
					}
				} else if (prevNonWhitespace == -2 && lastBlockCommentWasOnLine && cur != ';' && !Character.isWhitespace(cur)) {
					// previous statement was closing a */; new line
					// (but not when the next character is a ';')
					writer.newLineMaybe();
					needsWhitespace = false;
					lastBlockCommentWasOnLine = false;
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
	private boolean isInlinePhpReservedWordAfterBrace(MyStringReader reader,
			MyStringWriter writer) throws IOException {
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
	protected void jumpOverPhpString(MyStringReader reader, MyStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
	
				if (cur == '\\' && reader.readAhead() == '"') {
					// escaping the next string character
					writer.write(reader.read());
				}
			}
			throw new CleanerException("PHP string did not terminate");
		} finally {
			writer.enableIndent(true);
			writer.enableWordwrap(true);	// no wordwrap!
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
	protected void jumpOverPhpSingleString(MyStringReader reader, MyStringWriter writer, boolean allowSwitchToPhpMode) throws IOException, CleanerException {
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
				
				if (cur == '\'') {
					// at the end of the string
					writer.write(cur);
					return;
				}
				
				// write the character as normal
				writer.write(cur);
	
				if (cur == '\\' && reader.readAhead() == '\'') {
					// escaping the next string character
					writer.write(reader.read());
				}
			}
			throw new CleanerException("PHP single-quoted string did not terminate");
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
	private boolean didSwitchToPhpMode(MyStringReader reader,
			MyStringWriter writer, int cur) throws IOException, CleanerException {
		if (cur == '<' && reader.readAhead(4).equals("?php")) {
			// jump into php mode
			// we will assume we return successfully from it, otherwise
			// it's pretty much impossible to tell when script mode ends
			writer.enableIndent(true);
			reader.unread('<');	// go backwards
			cleanPhpBlock(reader, writer);
			writer.enableIndent(false);
			
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
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void jumpOverPhpInlineComment(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
		try {
			writer.enableWordwrap(false);	// don't wordwrap this comment!
			int cur = -1;
			while ((cur = reader.read()) != -1) {
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
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void jumpOverPhpBlockComment(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
		try {
			writer.enableWordwrap(false);	// don't wordwrap this comment!
			int cur = -1;
			boolean isBlankLine = false;
			boolean isJavadoc = (reader.readAhead() == '*');
			while ((cur = reader.read()) != -1) {
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
			throw new CleanerException("At end of file before found end of PHP block comment");
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
	protected void cleanHtmlComment(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
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
		return tag.equals("h1") || tag.equals("li") || tag.equals("title") || tag.equals("link") || 
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
			tag.equals("/script");
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
	protected String cleanHtmlTag(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
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
					cleanHtmlJavascript(reader, writer);
					// will continue when </script>
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
	 * '&lt;script ...&gt;'. We need to parse the inline Javascript until
	 * we are about to hit '&lt;/script&gt;'.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void cleanHtmlJavascript(MyStringReader reader,
			MyStringWriter writer) throws IOException, CleanerException {
		// is it immediately </script>?
		if (readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/script")) {
			// we don't need to do anything
			return;
		}
		
		// following script code will be indented
		writer.indentIncrease();
		
		// always start with a newline (but let <script></script> remain as one line)
		boolean needsNewLine = true;
		
		boolean needsLineBefore = false;
		boolean inInlineBrace = false;
		boolean lastBlockCommentWasOnLine = false;
		boolean inBracket = false;	// currently in a (...)?
		
		int cur = -1;
		int prev = ' ';
		int prevNonWhitespace = -1;
		boolean needsWhitespace = false;
		boolean isOnBlankLine = false;	// is the current character the first character on a new line? the first line is "part" of <script>
		while ((cur = reader.read()) != -1) {
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
				jumpOverPhpInlineComment(reader, writer); 
				needsLineBefore = true; // we need a new line next line
				prevNonWhitespace = -3;	// reset to "did an inline comment"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '/' && reader.readAhead() == '*') {
				// a multi-line comment
				// write a whitespace before
				lastBlockCommentWasOnLine = false;
				if (!isOnBlankLine && prevNonWhitespace != '(' && prevNonWhitespace != -1 && prevNonWhitespace != -3) {
					writer.write(' ');
				} else if (prevNonWhitespace == ';' || prevNonWhitespace == -1 || prevNonWhitespace == -2 || prevNonWhitespace == -1 || prevNonWhitespace == '}') {
					writer.newLine();
					// this comment is on its own line
					lastBlockCommentWasOnLine = true;
				}
				writer.write(cur);	// write '/'
				writer.write(reader.read());	// write '*'
				jumpOverPhpBlockComment(reader, writer);
				needsWhitespace = true;	// put a space before the next statement if necessary
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
				
				isOnBlankLine = false;
				if (prevNonWhitespace == ';') {
					if (inBracket) {
						// for (...; ...; ...)
						// just write a space
						writer.write(' ');
					} else {
						writer.newLine();
					}
				} else if (prevNonWhitespace == -2 && lastBlockCommentWasOnLine && cur != ';' && !Character.isWhitespace(cur)) {
					// previous statement was closing a */; new line
					// (but not when the next character is a ';')
					writer.newLineMaybe();
					needsWhitespace = false;
					lastBlockCommentWasOnLine = false;
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
				} else if (needsWhitespaceBetweenJavascript(reader, writer, prevNonWhitespace, cur)) {
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
			
			// is the next tag </script>?
			String nextTag = readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
			if (nextTag.toLowerCase().equals("/script")) {
				// we want to go back to html mode
				
				writer.indentDecrease(); // end indent
				// always end with a newline
				if (!needsNewLine) {
					// dont write a new line if we haven't actually written anything in here
					writer.newLineMaybe();
				}
				return;
			}
			
			if (reader.readAhead(5).equals("<?php")) {
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
		
		// it's NOT ok to fall out of script mode; this means we never
		// found a </script>.
		throw new InlineCleanerException("Unexpectedly terminated out of Javascript mode", reader);
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
	protected void cleanHtmlTagAttributes(MyStringReader reader,
			MyStringWriter writer) throws IOException, CleanerException {
		
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
				writer.write(cur);
				jumpOverHtmlAttributeString(reader, writer, cur);
				ignoreWhitespaceAfter = false;
				needWhitespace = true;		// any further attributes needs whitespace
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
	 */
	protected void jumpOverHtmlAttributeString(MyStringReader reader,
			MyStringWriter writer, int stringCharacter) throws IOException, CleanerException {
		int cur = -1;
		while ((cur = reader.read()) != -1) {
			if (cur == stringCharacter) {
				// at the end of the string
				writer.write(cur);
				return;
			}
			
			// write the character as normal
			writer.write(cur);

			// there is no escaping in HTML
		}
		throw new InlineCleanerException("HTML Attribute string did not terminate", reader);
	}

	/**
	 * Read ahead in the stream 'html...>...' and find the current HTML tag.
	 * Also includes formatted attributes. TODO link
	 * 
	 * If this returns an empty string, it may mean EOF, or the stream
	 * begins with [A-Za-z0-9_\-/].
	 * 
	 * @see MyStringReader#readAheadUntilEndHtmlTag()
	 * @param reader
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	private String findHtmlTagName(MyStringReader reader) throws IOException, CleanerException {
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
	private boolean removeHtmlTextSpacingUntil(MyStringReader reader,
			MyStringWriter writer, char c) throws IOException, CleanerException {
		
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
	 * Same as {@link #readAheadUntilEndHtmlTag(org.openiaml.iacleaner.IAInlineCleaner.MyStringReader)},
	 * except this skips over a '<' in front.
	 * 
	 * @param reader
	 * @param writer
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected String readAheadUntilEndHtmlTagWithOpenBrace(MyStringReader reader,
			MyStringWriter writer) throws IOException, CleanerException {
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
	 * Return the text found, or throws an exception.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected String readAheadUntilEndHtmlTag(MyStringReader reader) throws IOException, CleanerException {
		int oldLast = reader.getLastChar();
		char[] buffer = new char[MyStringReader.PUSHBACK_BUFFER_SIZE];	// to unread back to reader
		char[] retBuffer = new char[MyStringReader.PUSHBACK_BUFFER_SIZE];	// to return

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
	
	public class MyStringReader extends PushbackReader {

		private static final int PUSHBACK_BUFFER_SIZE = 1024;
		private int lastChar = -1;
		
		public MyStringReader(String s) {
			super(new StringReader(s), PUSHBACK_BUFFER_SIZE);
		}

		/**
		 * Read ahead for the next two characters, excluding <b>leading</b>
		 * whitespace. Reads up to PUSHBACK_BUFFER_SIZE characters.
		 * 
		 * @param i
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
		}
		
		/**
		 * "Read ahead" 1 character, so we can see what is coming up.
		 * 
		 * @return the next character, or -1 if EOF
		 * @throws IOException
		 */
		public int readAhead() throws IOException {
			int oldLast = lastChar;
			int found = read();
			if (found != -1)
				unread(found);
			lastChar = oldLast;	// reset
			return found;
		}

		/* (non-Javadoc)
		 * @see java.io.PushbackReader#read()
		 */
		@Override
		public int read() throws IOException {
			int c = super.read();
			lastChar = c;
			return c;
		}
		
	}
	
	/**
	 * This extends a StringWriter so that if we try to write something like 
	 * 's a \n s s s \n s b' (s = space), we actually write
	 * 's a \n \n s b' (so we don't write lines of just spaces).
	 * 
	 * Any spaces at the end of the file will also be trimmed (but not newlines).
	 *  
	 * @author Jevon
	 *
	 */
	public class DontWriteLinesOfJustSpaces extends StringWriter {

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
	
	public class MyStringWriter extends DontWriteLinesOfJustSpaces {

		private int indent = 0;
		private static final String indentString = "  ";
		private boolean canWordWrap = true;
		private int col = 0;
		
		private static final int WRITE_BUFFER_SIZE = 1024;
		private int[] writeBuffer = new int[WRITE_BUFFER_SIZE];
		private int writeBufferPos = -1;	// last position written
		
		private boolean indentEnabled = true;		// turn off indenting
		
		/**
		 * Columns after this long will be wordwrapped when {@link #canWordWrap} is true.
		 */
		private static final int wordWrapCol = 79;	
		
		public MyStringWriter() {
			super();
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
				throwWarning("Fell out of indent", getBuffer().toString());
				indent = 0;
			}
		}
		
		int previousChar = -1;
		boolean wordwrapOnNext = false;

		/* (non-Javadoc)
		 * @see java.io.StringWriter#write(int)
		 */
		@Override
		public void write(int c) {
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
}
