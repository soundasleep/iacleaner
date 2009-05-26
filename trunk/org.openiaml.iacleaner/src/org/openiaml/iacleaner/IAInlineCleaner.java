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
		
		/*
		 * text = removeDoubleSpacing(trim(readUntil('<')));
		 * read next 4 chars;
		 * if (text == "<?xml")
		 *   switch to xml mode(<?xml ...) permanently
		 * if (text == "<?php")
		 *   switch to php mode(<?php ... ending ?> [excluding "strings" 'strings'])
		 *   resume html
		 * if (text == "<!--")
		 *   switch to comment mode(<!-- ... ending -->)
		 *   resume html
		 * else
		 *   tag = switch to tag mode(<... ending > [excluding "strings" 'strings'])
		 *   echo <tag>;
		 *   if (need to indent)
		 *     indent++;
		 *     
		 * iterate;
		 */
		
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
		return Character.isWhitespace(prev) || prev == '{' || prev == '(' || prev == ')' || prev == '}' || prev == ';' || prev == '"' || prev == '\'';
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
		// add a space
		writer.write(' ');
		// following php code will be indented
		writer.indentIncrease();
		int cur = -1;
		int prev = ' ';
		int prevNonWhitespace = -1;
		while ((cur = reader.read()) != -1) {
			if (cur == '?' && reader.readAhead() == '>') {
				// end of php mode
				if (ignoreWhitespaceAfterPhp(prev)) {
					// add a space
					writer.write(' ');
				}
				writer.write(cur); // ?
				writer.write(reader.read()); // >
				writer.indentDecrease();	// end indent
				return;	// stop
			}
			
			if (Character.isWhitespace(cur) && ignoreWhitespaceAfterPhp(prev)) {
				// skip multiple whitespace
			} else {
				// put a newline before?
				if (prevNonWhitespace == ';') {
					writer.newLine();
				}
				
				// write like normal
				writer.write(cur);
				
				// switch into strings mode?
				if (cur == '"') {
					jumpOverPhpString(reader, writer);
				} else if (cur == '\'') {
					jumpOverPhpSingleString(reader, writer);
				}
				prevNonWhitespace = cur;
			}
			prev = cur;
		}
		// it's ok to fall out of PHP mode
		writer.indentDecrease(); // end indent
	}

	/**
	 * The last character we read in PHP mode was '"'; skip through the string
	 * until we find the end of the string.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void jumpOverPhpString(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
		int cur = -1;
		while ((cur = reader.read()) != -1) {
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
	}

	/**
	 * The last character we read in PHP mode was "'"; skip through the string
	 * until we find the end of the string.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected void jumpOverPhpSingleString(MyStringReader reader, MyStringWriter writer) throws IOException, CleanerException {
		int cur = -1;
		while ((cur = reader.read()) != -1) {
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
			tag.equals("/head") || tag.equals("/body") || tag.equals("/ol") || tag.equals("/ul");
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
		int prev = -1;
		boolean doneTag = false;
		while ((cur = reader.read()) != -1) {
			if (cur == '>') {
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
				}
				
				return tagName;
			} else if (Character.isWhitespace(cur)) {
				// end of tag name or attribute
				if (!doneTag) {
					// ignore all initial whitespace
				} else {
					// we now want to clean attributes
					// put whitespace back on stack
					//writer.write("@");
					reader.unread(cur);
					cleanHtmlTagAttributes(reader, writer);
				}
				/*
				if (!Character.isWhitespace(prev)) {
					// skip multiple whitespace
					writer.write(' ');
				}
				*/
			} else {
				// character data
				writer.write(cur);
				if (Character.isLetterOrDigit(cur) || cur == '_') {
					doneTag = true;		// we've done at least one character of the tag
				}
			}
			prev = cur;
		}
		
		// we never found the end of the tag >
		throwWarning("We never found the end of HTML tag", tagName.toString());
		return tagName.toString();
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
					int oldChar = reader.getLastChar();
					if (cur != reader.read())
						throw new InlineCleanerException("The unread buffer somehow changed", reader);	// sanity check
					String nextTag = readAheadUntilEndHtmlTag(reader);
					if (nextTag.isEmpty() || (nextTag.charAt(0) != '/' && nextTag.charAt(0) != '!')) {
						writer.write(' ');
						addWhitespace = false;
					}
					// put the < back
					reader.unread(cur);
					reader.setLastChar(oldChar);
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
	public String readAheadUntilEndHtmlTag(MyStringReader reader) throws IOException, CleanerException {
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
		throw new CleanerException("Could not read until end of HTML tag; never found end tag. Buffer = " + String.valueOf(buffer));
	}
	
	public class MyStringReader extends PushbackReader {

		private static final int PUSHBACK_BUFFER_SIZE = 1024;
		private int lastChar = -1;
		
		public MyStringReader(String s) {
			super(new StringReader(s), PUSHBACK_BUFFER_SIZE);
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
		 * @return
		 * @throws IOException
		 */
		public String readAhead(int n) throws IOException {
			int oldLast = lastChar;
			char[] c = new char[n];
			int found = read(c);
			unread(c, 0, found); // push these characters back on
			 
			lastChar = oldLast; // reset
			return String.valueOf(c).substring(0, found);
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
		
		/**
		 * Columns after this long will be wordwrapped when {@link #canWordWrap} is true.
		 */
		private static final int wordWrapCol = 79;	
		
		public MyStringWriter() {
			super();
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
		}

		/**
		 * @return
		 */
		private String getIndent() {
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
