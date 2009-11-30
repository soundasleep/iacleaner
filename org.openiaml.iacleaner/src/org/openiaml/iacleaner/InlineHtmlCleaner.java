/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;
import java.util.Stack;

import org.openiaml.iacleaner.inline.InlineCleanerException;
import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * Handles the cleaning of HTML content.
 * 
 * @author Jevon
 *
 */
public class InlineHtmlCleaner {
	
	private IAInlineCleaner inline;
	
	public InlineHtmlCleaner(IAInlineCleaner inline) {
		this.inline = inline;
	}
	
	public IAInlineCleaner getInline() {
		return inline;
	}

	/**
	 * Clean up HTML code.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void cleanHtmlBlock(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {

		Stack<String> tagStack = new Stack<String>();
		
		String lastTag = null;	// last tag
		String stackTag = null;	// current wrapping tag
		while (cleanHtmlTextUntilNextTag(stackTag, lastTag, reader, writer, '<') && reader.readAhead() != -1) {
			String next5 = reader.readAhead(5);
			if (next5.equals("<?xml")) {
				// xml mode!
				return;	// permanent change
			} else if (next5.equals("<?php")) {
				// php mode!
				getInline().cleanPhpBlock(reader, writer);
				// we may continue with html mode
			} else if (next5.substring(0, 4).equals("<!--")) {
				// comment mode!
				cleanHtmlComment(reader, writer);
				// we may continue with html mode
			} else {
				// tag mode!
				lastTag = cleanHtmlTag(reader, writer).toLowerCase();
				
				// which tag are we actually in right now?
				stackTag = getCurrentTagFromStack(lastTag, tagStack);
			}
		}
		
	}

	/**
	 * The given stack contains a stack of opened elements; when we open new 
	 * ones, they should be added to the stack, and when closed, they should be 
	 * removed.
	 * 
	 * Some elements will never be closed (e.g. <code>&lt;img&gt;</code>;
	 * the tag stack should just jump up until it finds the closing tag. 
	 * 
	 * @param newTagName
	 * @param tagStack
	 * @return the current stack tag, or null if we have run out of stack
	 */
	private String getCurrentTagFromStack(String newTagName,
			Stack<String> tagStack) {
		
		// ignore null tag names
		if (newTagName == null)
			return tagStack.empty() ? null : tagStack.peek();
		
		if (newTagName.startsWith("/")) {
			// we are closing a tag
			String r = newTagName.substring(1);	// skip '/'
			while (!tagStack.empty() && !tagStack.pop().equals(r)) {
				// pop until we find the current tag
			}
			// return the next on the stack
			if (tagStack.empty()) {
				return null;
			} else {
				return tagStack.peek();
			}
		} else {
			// add the current tag to the stack
			tagStack.push(newTagName);
			return newTagName;	// we are the top level
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
		return tag.equals("/h1") || tag.equals("/h2") || tag.equals("/h3") ||
			tag.equals("/h4") || tag.equals("/h5") || tag.equals("/h6") || 
			tag.equals("/li") || tag.equals("/title") || tag.equals("/link") || 
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
	 * @return the htmlTag found, or null if we unexpectedly fell out
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
					getInline().cleanHtmlJavascript(reader, writer, true);
					// will continue when </script>
				} else if (tagName.toLowerCase().equals("style")) {
					// css mode!
					getInline().cleanHtmlCss(reader, writer, true);
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
		getInline().throwWarning("We never found the end of HTML tag", tagName.toString());
		return null;
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
				getInline().cleanPhpBlock(reader, writer);
				
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

	/**
	 * Read ahead in the stream 'html...>...' and find the current (next) HTML tag.
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
		return getInline().readAheadUntilEndHtmlTag(reader);
	}

	/**
	 * Remove space around HTML text until we find the character '<'
	 * (don't include this in the output)
	 * 
	 * This also controls the formatting of the output text.
	 * 
	 * @param the current tag (or null if there has not been any tags yet)
	 * @param reader
	 * @param writer
	 * @param c
	 * @return true if there is more text to go, or false at EOF
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	protected boolean cleanHtmlTextUntilNextTag(String currentTag, String lastTag, InlineStringReader reader,
			InlineStringWriter writer, char c) throws IOException, CleanerException {
		
		int cur;
		int prev = -1;
		boolean addWhitespace = false;
		boolean hasDoneWhitespace = false;
		while ((cur = reader.readAhead()) != -1) {
			if (cur == '<') {
				// we're done
				// do we need to add previous whitespace?
				if (addWhitespace && !Character.isWhitespace(writer.getPrevious())) {
					String nextTag = getInline().readAheadUntilEndHtmlTagWithOpenBrace(reader, writer);
					// can we possibly ignore the whitespace?
					if (!htmlTagRequiresInlineWhitespace(currentTag) || htmlTagWillIgnoreLeadingWhitespace(lastTag) || htmlTagWillIgnoreTrailingWhitespace(nextTag)) {
						// read ahead to find the next tag
						// eat the <
						if ((nextTag.isEmpty() || (nextTag.charAt(0) != '/' && nextTag.charAt(0) != '!' /* comments */))
								&& !htmlTagNeedsNewLine(nextTag)) {
							writer.write(' ');
							addWhitespace = false;
						}
					} else {
						// the current tag requires whitespace
						
						// if ((nextTag.isEmpty() || (nextTag.charAt(0) != '/' && nextTag.charAt(0) != '!' /* comments */))
						//		&& !htmlTagNeedsNewLine(nextTag) || true) {
						
						if (!htmlTagWillIgnoreLeadingWhitespace(nextTag) ||
								!htmlTagWillIgnoreTrailingWhitespace(nextTag)) {
							// writer.write(nextTag);
							// ignore lines that follow with a commment
							if (nextTag.isEmpty() || nextTag.charAt(0) != '!' /* comments */) {
								if (!htmlTagWillIgnoreAllWhitespace(currentTag)) {
									writer.write(' ');
									addWhitespace = false;
								}
							}
						}
					}
				}
				return true;
			}
			
			// skip multiple whitespace
			reader.read();	// eat a char (that we just read into cur)
			if (Character.isWhitespace(cur)) {
				if (!hasDoneWhitespace && htmlTagRequiresInlineWhitespace(currentTag) 
						&& !htmlTagWillIgnoreLeadingWhitespace(lastTag)
						&& !Character.isWhitespace(writer.getPrevious())) {
					// we haven't done whitespace yet, but the current tag requires we put it in
					addWhitespace = true;
				} else if (Character.isWhitespace(prev)) {
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
	 * @param currentTag
	 * @return
	 */
	private boolean htmlTagWillIgnoreAllWhitespace(String currentTag) {
		return currentTag.equals("html") || currentTag.equals("body") || currentTag.equals("head");
	}

	/**
	 * Will the given next tag ignore any leading whitespace placed after it?
	 * 
	 * @param currentTag
	 * @return
	 */
	private boolean htmlTagWillIgnoreLeadingWhitespace(String currentTag) {
		if (currentTag == null)
			return true;
		
		return currentTag.equals("h1") || currentTag.equals("h2") || currentTag.equals("h3")
		|| currentTag.equals("h4") || currentTag.equals("h5") || currentTag.equals("h6")
		|| currentTag.equals("p") || currentTag.equals("body") || currentTag.equals("html")
		|| currentTag.equals("title") || currentTag.equals("style") || currentTag.equals("script")
		|| currentTag.equals("head") || currentTag.equals("div") || currentTag.equals("label")
		|| currentTag.equals("li") || currentTag.equals("ol") || currentTag.equals("ul");
	}

	/**
	 * Will the given next tag ignore any trailing whitespace placed before it?
	 * 
	 * @param nextTag
	 * @return
	 */
	private boolean htmlTagWillIgnoreTrailingWhitespace(String nextTag) {
		if (nextTag == null)
			return true;
	
		return nextTag.length() > 1 && nextTag.startsWith("/") 
			&& htmlTagWillIgnoreLeadingWhitespace(nextTag.substring(1));
	}

	/**
	 * Does the current HTML tag require us to keep whitespace?
	 * 
	 * @return
	 * @throws CleanerException 
	 * @throws IOException 
	 */
	private boolean htmlTagRequiresInlineWhitespace(String currentTag) {
		if (currentTag == null)
			return false;
		
		return true; // || currentTag.equals("a") || currentTag.equals("h4");
	}

}
