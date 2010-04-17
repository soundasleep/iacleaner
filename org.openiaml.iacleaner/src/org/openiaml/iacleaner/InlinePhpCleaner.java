/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * Handles the cleaning of PHP content.
 * 
 * @author Jevon
 *
 */
public class InlinePhpCleaner extends InlineCSyntaxCleaner {

	public InlinePhpCleaner(IAInlineCleaner inline) {
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
	private boolean doesntActuallyNeedWhitespaceBeforePhp(InlineStringReader reader, InlineStringWriter writer, int cur) throws IOException {
		return cur == '(' || cur == ')' || cur == '}' || cur == ';' || cur == '[' || cur == ']' || cur == ',' || cur == '+' || cur == '-' || writer.getPrevious() == '-' || writer.getPrevious() == '+' ||
			isTwoCharacterOperator(cur, reader.readAhead()) ||
			(writer.getLastWritten(2).equals("::")) /* :: operator */ ||
			(writer.getLastWritten(2).equals("->")) /* -> operator */;
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
	public boolean needsWhitespaceBetweenPhp(InlineStringReader reader, InlineStringWriter writer, int a, int b) throws IOException {
		return (a == ')' && b == '{') || (a == ',') || 
			(!isOperator(a) && b == '=') || 
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
			(isOperator(a) && !isOperator(b) && b != ')' && a != '!' && b != ';' && b != '$' && !writer.getLastWritten(2).equals("->") && !writer.getLastWritten(3).equals(", -") && !writer.getLastWritten(3).equals(", +")) ||
			(a == ')' && isOperator(b)) ||
			(isOperator(a) && a != '!' && b == '$') ||
			(b == '*') || (a == ')' && b == '-') ||
			(a == ']' && b == ':') || (a == ')' && b == ':') || /* between ): or ]: */
			(a == ':' && b != ':' && !writer.getLastWritten(2).equals("::") /* between :: */) ||
			(isPreviousWordReserved(writer) && (b == '(' || b == '$')) ||
			(a == ')' && Character.isLetter(b) /* e.g. 'foo() or..' */ ) ||
			(Character.isLetter(a) && b == '"' /* e.g. 'echo "...' */ ) ||
			(Character.isLetter(a) && b == '\'' /* e.g. 'echo '...' */ ) ||
			(Character.isLetter(a) && isOperator(b) && !isTwoCharacterOperator(b, reader.readAhead())) /* e.g. $f * $g */;
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
	@Override
	public boolean isTwoCharacterOperator(int a, int b) {
		return (a == '-' && b == '>') || super.isTwoCharacterOperator(a, b);
	}
	
	/**
	 * Is the given character a single-character operator?
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public boolean isOperator(int a) {
		return a == '.' || super.isOperator(a);
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
	public void cleanPhpBlock(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {
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
				jumpOverInlineComment(reader, writer, false); 
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
				jumpOverBlockComment(reader, writer, false);
				needsWhitespace = true;	// put a space before the next statement if necessary
				charBeforeBlockComment = prevNonWhitespace;		// save the previous char
				prevNonWhitespace = -2;	// reset to "did a comment block"
				inInlineBrace = false;
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
					} else if (!isNextWordInlineBrace(reader, writer)) {
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
					jumpOverString(reader, writer, false);
				} else if (cur == '\'') {
					jumpOverSingleString(reader, writer, false);
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

}
