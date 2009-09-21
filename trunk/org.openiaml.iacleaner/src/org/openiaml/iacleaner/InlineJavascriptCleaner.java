/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * Handles the cleaning of Javascript content.
 * 
 * @author Jevon
 *
 */
public class InlineJavascriptCleaner {
	
	private IAInlineCleaner inline;
	private CommonPhpJavascriptCleaner common;

	public InlineJavascriptCleaner(IAInlineCleaner inline) {
		this.inline = inline;
		this.common = new CommonPhpJavascriptCleaner(inline);
	}
	
	public IAInlineCleaner getInline() {
		return inline;
	}
	
	public CommonPhpJavascriptCleaner getCommon() {
		return common;
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
			(getCommon().previousWordIsReservedWordPhp(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			((isJavascriptOperator(a) || Character.isLetter(a)) && b == '"' /* e.g. 'echo "...' */ ) ||
			((isJavascriptOperator(a) || Character.isLetter(a)) && b == '\'' /* e.g. 'echo '...' */ ) ||
			(Character.isLetter(a) && isJavascriptOperator(b) && !isJavascriptTwoCharacterOperator(b, reader.readAhead())) /* e.g. f * g */;
	}

	private boolean isJavascriptTwoCharacterOperator(int a, int b) {
		return getCommon().isJavascriptTwoCharacterOperator(a, b);
	}

	private boolean isJavascriptOperator(int a) {
		return getCommon().isJavascriptOperator(a);
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
					if (getInline().didSwitchToPhpMode(reader, writer, cur)) {
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
		if (withinHtml && getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/script")) {
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
				
				if (getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/script")) {
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
				getCommon().jumpOverPhpInlineComment(reader, writer, true); 
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
				getCommon().jumpOverPhpBlockComment(reader, writer, true);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				inInlineBrace = false;
				continue;
			}
			
			if (cur == '\n' || cur == '\r') {
				isOnBlankLine = true;
			}
			
			if (Character.isWhitespace(cur) && getCommon().ignoreWhitespaceAfterPhp(prev)) {
				// print just a space if necessary
				if (getCommon().needsWhitespaceCharacterPhp(prev)) {
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
					} else if (!getCommon().isInlinePhpReservedWordAfterBrace(reader, writer)) {
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
					getCommon().jumpOverPhpString(reader, writer, true);
				} else if (cur == '\'') {
					getCommon().jumpOverPhpSingleString(reader, writer, true);
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
				String nextTag = getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
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
				getInline().cleanPhpBlock(reader, writer);
				
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
					getInline().cleanPhpBlock(reader, writer);
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

}
