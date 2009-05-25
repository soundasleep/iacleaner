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
	 */
	protected void cleanHtmlScript(MyStringReader reader, MyStringWriter writer) throws IOException {
		
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
		
		while (removeHtmlTextSpacingUntil(reader, writer, '<')) {
			String next5 = reader.readAhead(4);
			if (next5.equals("<?xml")) {
				// xml mode!
				return;	// permanent change
			} else if (next5.equals("<?php")) {
				// php mode!
			} else if (next5.substring(0, 4).equals("<!--")) {
				// comment mode!
			} else {
				// tag mode!
				String htmlTag = cleanHtmlTag(reader, writer).toLowerCase();
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

		/*
		// write out until we find a <
		int cur;
		while ((cur = reader.read()) != '<') {
			writer.write(cur);
		}
		
		// get the next character
		reader.mark(5);
		int next = reader.read();
		if (next == '?') {
			// its php or xml
			
			return;
		} else {
			// its an html tag
			// read until we find the end of the tag
			
		}
		*/
		
	}
	
	/**
	 * Should the given HTML tag be intented?
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagIndented(String tag) {
		if (tag.charAt(0) == '/')
			return htmlTagIndented(tag.substring(1));
		return tag.equals("html") || tag.equals("body") || tag.equals("ol") || tag.equals("ul") ||
			tag.equals("head");
	}
	
	/**
	 * Does the given HTML tag need to be put on a new line?
	 * @param tag
	 * @return
	 */
	protected boolean htmlTagNeedsNewLine(String tag) {
		return tag.equals("h1") || tag.equals("li") || tag.equals("title") || tag.equals("link") || tag.equals("head") || tag.equals("body")
		|| tag.equals("ol") || tag.equals("ul") || tag.equals("head") || tag.equals("body");
	}

	/**
	 * We have just started an <htmlTag attr...>. Clean the tag up, and return
	 * the 'htmlTag'.
	 * 
	 * @param reader
	 * @param writer
	 * @return
	 * @throws IOException 
	 */
	protected String cleanHtmlTag(MyStringReader reader, MyStringWriter writer) throws IOException {
		// get the first char <
		int first = reader.read();
		
		// first, find out what the tag name actually is, so we know whether to indent back
		// or not
		String tagName = findHtmlTagName(reader);
		
		if (tagName.charAt(0) == '/' && htmlTagIndented(tagName)) {
			// need to un-indent before
			writer.indentDecrease();
			writer.newLine();
		}

		// does this tag need to be placed on a new line?
		if (htmlTagNeedsNewLine(tagName)) {
			writer.newLineMaybe();
		}
		
		// write the first char < (AFTER sorting out the indent)
		writer.write(first);

		int cur;
		int prev = -1;
		while ((cur = reader.read()) != -1) {
			if (cur == '>') {
				// end of tag
				writer.write(cur);
				
				if (tagName.charAt(0) != '/' && htmlTagIndented(tagName)) {
					// need to indent afterwards
					writer.indentIncrease();
					writer.newLine();
				}
				
				return tagName;
			} else if (Character.isWhitespace(cur)) {
				// end of tag name or attribute
				if (!Character.isWhitespace(prev)) {
					// skip multiple whitespace
					writer.write(' ');
				}
			} else {
				// character data
				writer.write(cur);
			}
			prev = cur;
		}
		
		// we never found the end of the tag >
		throwWarning("We never found the end of HTML tag", tagName.toString());
		return tagName.toString();
	}

	/**
	 * Read ahead in the stream 'html...>...' and find the current HTML tag.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private String findHtmlTagName(MyStringReader reader) throws IOException {
		return reader.readAheadUntilEndHtmlTag();
	}

	/**
	 * Remove space around HTML text until we find the character '<'
	 * (don't include this in the output)
	 * 
	 * @param reader
	 * @param writer
	 * @param c
	 * @return true if there is more text to go, or false at EOF
	 * @throws IOException 
	 */
	private boolean removeHtmlTextSpacingUntil(MyStringReader reader,
			MyStringWriter writer, char c) throws IOException {
		
		int cur;
		int prev = -1;
		while ((cur = reader.readAhead()) != -1) {
			if (cur == '<') {
				// we're done
				return true;
			}
			
			// skip multiple whitespace
			reader.read();	// eat a char
			if (Character.isWhitespace(cur) && Character.isWhitespace(prev)) {
				// ignore multiple whitespace inline
			} else if (prev == -1 && Character.isWhitespace(cur)) {
				// ignore initial whitespace
			} else if (Character.isWhitespace(cur) && reader.readAhead() == '<') {
				// ignore trailing whitespace
			} else {
				// write as normal
				writer.write(cur);
			}
			prev = cur;
		}
		
		return false;		
	}

	public class MyStringReader extends PushbackReader {

		private static final int PUSHBACK_BUFFER_SIZE = 1024;
		
		public MyStringReader(String s) {
			super(new StringReader(s), PUSHBACK_BUFFER_SIZE);
		}
		
		/**
		 * Read ahead until we find something outside [A-Za-z0-9_\-/]. Return the text found, or 
		 * the whole buffer if no end was found.
		 * 
		 * @return
		 * @throws IOException 
		 */
		public String readAheadUntilEndHtmlTag() throws IOException {
			char[] buffer = new char[PUSHBACK_BUFFER_SIZE];
			int i = -1;
			int cur;
			while ((cur = read()) != -1) {
				i++;
				buffer[i] = (char) cur;
				if (!(Character.isLetterOrDigit(cur) || cur == '_' || cur == '-' || cur == '/')) {
					// we found it
					unread(buffer, 0, i + 1);
					return new String(buffer, 0, i);
				}
			}
			// return the entire buffer
			return new String(buffer, 0, i + 1);
		}

		/**
		 * "Read ahead" N characters, so we can see what is coming up.
		 * 
		 * @param n number of characters to read ahead
		 * @return
		 * @throws IOException
		 */
		public String readAhead(int n) throws IOException {
			char[] c = new char[n];
			int found = read(c);
			unread(c, 0, found); // push these characters back on
			 
			return String.valueOf(c).substring(0, found);
		}
		
		/**
		 * "Read ahead" 1 character, so we can see what is coming up.
		 * 
		 * @return the next character, or -1 if EOF
		 * @throws IOException
		 */
		public int readAhead() throws IOException {
			int found = read();
			if (found != -1)
				unread(found);
			return found;
		}
		
	}
	

	public class MyStringWriter extends StringWriter {

		private int indent = 0;
		private static final String indentString = "  ";
		
		public MyStringWriter() {
			super();
		}

		/**
		 * Only write a new line if the previous line wasn't one.
		 */
		public void newLineMaybe() {
			if (previousChar != '\n')
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

		/* (non-Javadoc)
		 * @see java.io.StringWriter#write(int)
		 */
		@Override
		public void write(int c) {
			// indent?
			if (previousChar == '\n') {
				write(getIndent());
			}
			super.write(c);
			previousChar = c;
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
				super.write(cbuf[i + off]);
			}
		}

		/* (non-Javadoc)
		 * @see java.io.StringWriter#write(java.lang.String, int, int)
		 */
		@Override
		public void write(String str, int off, int len) {
			// TODO optimize
			for (int i = 0; i < len; i++) {
				super.write(str.charAt(i + off));
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
