/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * Handles the cleaning of CSS content.
 * 
 * @author Jevon
 *
 */
public class InlineCssCleaner extends InlineCSyntaxCleaner {

	public InlineCssCleaner(IAInlineCleaner inline) {
		super(inline);
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
	 * We are in HTML and we have just processed a tag
	 * '&lt;style ...&gt;'. We need to parse the inline CSS until
	 * we are about to hit '&lt;/style&gt;'.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void cleanHtmlCss(InlineStringReader reader,
			InlineStringWriter writer, boolean withinHtml) throws IOException, CleanerException {
		// is it immediately </script>?
		if (withinHtml && getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer).toLowerCase().equals("/style")) {
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
				jumpOverBlockComment(reader, writer, true);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				continue;
			}
			
			if (cur == '\n' || cur == '\r') {
				isOnBlankLine = true;
			}
			
			if (Character.isWhitespace(cur) && shouldIgnoreWhitespaceAfter(prev)) {
				// print just a space if necessary
				if (needsWhitespaceCharacterAfter(prev)) {
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
					jumpOverString(reader, writer, true);
				} else if (cur == '\'') {
					jumpOverSingleString(reader, writer, true);
				}

				if (!Character.isWhitespace(cur)) {
					prevNonWhitespace = cur;
				}

			}
			prev = cur;
			
			// is the next tag </style>?
			if (withinHtml) {
				String nextTag = getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
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
				getInline().cleanPhpBlock(reader, writer);
				
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

}
