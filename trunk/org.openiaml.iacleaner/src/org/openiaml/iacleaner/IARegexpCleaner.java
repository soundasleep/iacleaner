/**
 * 
 */
package org.openiaml.iacleaner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple regular-expression based syntax formatter for Internet 
 * Applications: attempts to clean HTML, Javascript, CSS, PHP and other
 * languages.
 * 
 * It struggles with:
 * <ul>
 *   <li>Regular expressions in Javascript, e.g. /"/g</li>
 *   <li>Single quotes if they are not part of strings (unless preceded
 *   and followed by a character in [A-Za-z]</li>
 * </ul>
 * 
 * @author Jevon
 * @see http://code.google.com/p/iacleaner/
 *
 */
public class IARegexpCleaner extends DefaultIACleaner implements IACleaner {
	
	protected static final String KEY_QUOTE = "$hide_string_";
	protected static final String KEY_QUOTE_SQ = "$hide_string_sq_";
	protected static final String KEY_BLOCK = "$hide_block_";
	protected static final String KEY_LINE = "$hide_line_";
	protected static final String KEY_END = "$";
	
	protected static final String REPLACE_QUOTE = "$replace_quote$";
	protected static final String REPLACE_QUOTE_SQ = "$replace_quote_sq$";
	protected static final String INDENT_NEWLINE = "$indent_newline$";

	protected static final String HTML_INDENT_TAGS = "(html|head|body|div|script)";

	protected Map<String, String> replaceBlockComments;
	protected Map<String, String> replaceQuotes;
	protected Map<String, String> replaceQuotesSq;
	protected Map<String, String> replaceLineComments;
	
	private Map<String, String> exceptions;
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#getExceptions()
	 */
	public Map<String,String> getExceptions() {
		if (exceptions == null) {
			exceptions = new HashMap<String,String>();
			
			// prototype.js exceptions
			exceptions.put( "/\"/g", "$exception_key_1$" );
			exceptions.put( "/\"[^\"\\\\\\n\\r]*\"/g", "$exception_key_2$" );
			exceptions.put( "http://", "$exception_key_3$" );
			exceptions.put( "https://", "$exception_key_4$" );
			exceptions.put( "ftp://", "$exception_key_5$" );
			exceptions.put( "'\\\\'", "$exception_key_6$");		// need to quote \s
			exceptions.put( "'\\\\\\\\'", "$exception_key_7$");
			exceptions.put( "src=//:", "$exception_key_8$");
			exceptions.put( "'.//*'", "$exception_key_9$");
			exceptions.put( "\"//*\"", "$exception_key_10$");
			exceptions.put( "\"/*\"", "$exception_key_11$");
			exceptions.put( "'\\\\\\''", "$exception_key_12$");
			exceptions.put( "/'/g", "$exception_key_13$" );
			exceptions.put( "'\\\\\"'", "$exception_key_14$" );
			exceptions.put( ", */*", "$exception_key_15$");
			exceptions.put( "/\\[((?:[\\w-]*:)?[\\w-]+)\\s*(?:([!^$*~|]?=)\\s*((['\"])([^\\4]*?)\\4|([^'\"][^\\]]*?)))?\\]/", "$exception_key_16$"); // ha, good luck debugging this one!
		}
		
		return exceptions;
	}
	
	/* (non-Javadoc)
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String)
	 */
	public String cleanScript(String script) throws CleanerException {
		script = removeUnusualLineEndings(script);
		
		// get rid of all explicit exceptions
		for (String key : getExceptions().keySet()) {
			script = script.replace(key, getExceptions().get(key));
		}
		
		// replace all escaped quotes
		script = script.replace("\\\"", REPLACE_QUOTE);
		script = script.replace("\\'", REPLACE_QUOTE_SQ);

		// find all block comments and replace them
		script = extractBlockComments(script);
		
		// find all strings and replace them
		script = extractStrings(script);

		// find all single strings and replace them (single quotes)
		script = extractSingleStrings(script);
		
		// find all line comments and replace them
		script = extractLineComments(script);
		
		// replace single-line comments with block comments
		/*
		script = script.replaceAll("([^:])//([^\r\n]+)[\r\n]", "$1/*$2 *
		/\n");	// first char disables replacing http:// addresses" +
				"*/
		// not used anymore --
		// script = script.replaceAll("//([^\r\n]*)[\r\n]", "/*$1*/\n");
			// if this replaces http://, it's because it's in a comment
		
		// get rid of all newlines and tabs
		script = script.replaceAll("[\r\n\t]", " ");

		// get rid of multiple spaces
		script = script.replaceAll(" [ ]+", " ");
		
		// newlines at ends of statements
		script = script.replace("; ", ";\n");
		script = script.replace("{ ", "{\n");
		script = script.replace("} ", "}\n");
		script = script.replace("*/ ", "*/\n");

		// newlines at ends of html tags
		script = script.replace("> ", ">\n");

		// format indents
		script = indentScript(script);
		
		// put all the strings back in
		script = replaceExtractedElements(script);
		
		script = script.replace(REPLACE_QUOTE_SQ, "\\'");
		script = script.replace(REPLACE_QUOTE, "\\\"");
		
		// reinsert explicit exceptions
		for (String key : getExceptions().keySet()) {
			script = script.replace(getExceptions().get(key), key);
		}
		
		return script;
	}

	/**
	 * @param script
	 * @return
	 */
	private String replaceExtractedElements(String script) {
		for (String key : replaceLineComments.keySet()) {
			String value = replaceLineComments.get(key);
			script = script.replace(key, value);
		}
		for (String key : replaceQuotesSq.keySet()) {
			String value = replaceQuotesSq.get(key);
			script = script.replace(key, value);
		}
		for (String key : replaceQuotes.keySet()) {
			String value = replaceQuotes.get(key);
			script = script.replace(key, value);
		}
		for (String key : replaceBlockComments.keySet()) {
			String value = replaceBlockComments.get(key);
			script = script.replace(key, value);
		}
		return script;
	}
	
	/**
	 * Extend the {@link HashMap} to also ensure that the
	 * value set is unique.
	 * 
	 * @author Jevon
	 *
	 * @param <A>
	 * @param <B>
	 */
	public class DoubleHashMap<A,B> extends HashMap<A,B> {

		private static final long serialVersionUID = 1L;
		
		private Set<B> valueSet = new HashSet<B>();

		/* (non-Javadoc)
		 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public B put(A key, B value) {
			if (valueSet.contains(value))
				return super.get(key);
			
			valueSet.add(value);

			return super.put(key, value);
		}
		
	}

	/**
	 * @param script
	 * @return
	 * @throws CleanerException
	 */
	private String extractLineComments(String script) throws CleanerException {
		replaceLineComments = new DoubleHashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("//", i);
			if (start != -1) {
				int end = script.indexOf("\n", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated // string at character position " + i, getContext(script, i), script);
				}
				
				// skip replace the newline
				end--; 
				
				String key = KEY_LINE + i + KEY_END + INDENT_NEWLINE;		// DONT add new line (will screw up key)
				String value = script.substring(start, end + 1);				
				replaceLineComments.put(key, value);
				i = end + 1;
			} else {
				break;
			}
		}
		
		for (String key : replaceLineComments.keySet()) {
			String value = replaceLineComments.get(key);
			// we must make sure we find the entire line (so //a doesn't match //ab)
			script = script.replace(value + "\n", key + "\n");
		}
		return script;
	}

	/**
	 * @param script
	 * @return
	 * @throws CleanerException
	 */
	private String extractSingleStrings(String script) throws CleanerException {
		replaceQuotesSq = new DoubleHashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("'", i);
			if (start != -1) {
				// don't replace english words with 's in them
				// matches [A-Za-z]
				if (alphaNumeric(script.charAt(start-1)) && alphaNumeric(script.charAt(start+1))) {
					i = start + 1;
					continue;
				}
				
				int end = script.indexOf("'", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated '' string at character position " + i, getContext(script, i), script);
				}
				
				String key = KEY_QUOTE_SQ + i + KEY_END;
				String value = script.substring(start, end + 1);
				replaceQuotesSq.put(key, value);
				i = end + 1;
			} else {
				break;
			}
		}
		
		for (String key : replaceQuotesSq.keySet()) {
			String value = replaceQuotesSq.get(key);
			script = script.replace(value, key);
		}
		return script;
	}

	/**
	 * Does this character match [A-Za-z]?
	 * 
	 * @param charAt
	 * @return
	 */
	private boolean alphaNumeric(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	/**
	 * @param script
	 * @return
	 * @throws CleanerException
	 */
	private String extractStrings(String script) throws CleanerException {
		replaceQuotes = new DoubleHashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("\"", i);
			if (start != -1) {
				int end = script.indexOf("\"", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated \"\" string at character position " + i, getContext(script, i), script);
				}
				
				String key = KEY_QUOTE + i + KEY_END;
				String value = script.substring(start, end + 1);
				replaceQuotes.put(key, value);
				i = end + 1;
			} else {
				break;
			}
		}
		
		for (String key : replaceQuotes.keySet()) {
			String value = replaceQuotes.get(key);
			script = script.replace(value, key);
		}
		return script;
	}

	/**
	 * @param script
	 * @return
	 * @throws CleanerException
	 */
	private String extractBlockComments(String script) throws CleanerException {
		replaceBlockComments = new DoubleHashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("/*", i);
			if (start != -1) {
				int end = script.indexOf("*/", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated /* */ string at character position " + i, getContext(script, i), script);
				}
				
				String key = KEY_BLOCK + i + KEY_END + INDENT_NEWLINE;
				String value = script.substring(start, end + 2);		// +2
				replaceBlockComments.put(key, value);
				i = end + 2;		// +2 because the end token is 2 chars long
			} else {
				break;
			}
		}
		
		for (String key : replaceBlockComments.keySet()) {
			String value = replaceBlockComments.get(key);
			script = script.replace(value, key);
		}
		return script;
	}

	/**
	 * Remove all unusual line endings.
	 * 
	 * @param script
	 * @return
	 */
	private String removeUnusualLineEndings(String script) {
		script = script.replace("\r\n", "\n");
		script = script.replace("\r", "\n");
		return script;
	}
	
	/**
	 * Given a script, tries to indent it in such a way
	 * that is meaningful.
	 * 
	 * It assumes that all whitespace-sensitive elements (e.g.
	 * strings) have already been taken out.
	 * 
	 * This can be overridden if you find the default indenting
	 * method does not satisfy your needs.
	 * 
	 * @param script
	 * @return
	 */
	public String indentScript(String script) throws CleanerException {
		// add indents to code blocks and php blocks
		String[] lines = script.split("\n");
		int brace_count = 0;
		
		final String match_indent_open = "\\s*<" + HTML_INDENT_TAGS + "[^>]*>\\s*";
		final String match_indent_close = "\\s*</" + HTML_INDENT_TAGS + "[^>]*>\\s*";
		final String match_indent_open_close = "\\s*<" + HTML_INDENT_TAGS + "[^>]*/>\\s*";
		StringBuffer buf = new StringBuffer();
		int lineNum = 0;
		String prevLine = "";
		for (String line : lines) {	
			lineNum++;
			String lineToLower = line.toLowerCase();
			if (line.indexOf("}") != -1) {
				// close the brace
				brace_count--;
			} else if (lineToLower.matches(match_indent_close)) {
				// close the tag
				brace_count--;
			} else if (lineToLower.matches(match_indent_open_close)) {
				// close the tag (both an opening and closing tag on the same line)
				brace_count--;
			}
			
			int next_brace_count = brace_count;
			if (line.indexOf("{") != -1) {
				// open the brace for next lines
				next_brace_count++;
			} else if (lineToLower.matches(match_indent_open)) {
				// open the tag for next lines
				next_brace_count++;
			} 
			
			// indent the line
			if (brace_count < 0) {
				if (next_brace_count < 0) {
					// this means that the next line will definitely be out
					throwWarning("Unbalanced braces as of line: " + line + " (" + lineNum + "/" + lines.length + ")", getContext(lines, lineNum));
					next_brace_count = 0;
				}
				// otherwise, it could be a line like "{ }"
				brace_count = 0;
			}
			String indent = repeatString("  ", brace_count);
			line = indent + line;
			line = line.replace(INDENT_NEWLINE + " ", INDENT_NEWLINE + "\n" + indent); // fix issue 1

			brace_count = next_brace_count;
			
			buf.append(line).append("\n");
			
			// additional lines after }, unless the previous line also included a }
			if (line.contains("}") && !prevLine.contains("}")) {
				buf.append("\n");
			}
			
			prevLine = line;
		}
		
		// implode it back together
		String result = buf.toString();
		
		// simplify indentation of common structures
		result = result.replaceAll("}[\\s]+catch[\\s]+\\(", "} catch (");
		result = result.replaceAll("}[\\s]+else[\\s]+\\{", "} else {");
		result = result.replaceAll("}[\\s]+else[\\s]+if[\\s]+\\(", "} else if (");
		result = result.replaceAll("}[\\s]+elseif[\\s]+\\(", "} elseif (");
		
		return result;
	}

	/**
	 * Get the string context at a given position. 
	 * This returns a few of the lines before and after the given
	 * line number.
	 * 
	 * @param lines
	 * @param lineNum
	 * @return
	 */
	private String getContext(String[] lines, int lineNum) {
		StringBuffer buf = new StringBuffer();
		
		for (int i = lineNum - 4; i < lineNum + 4; i++) {
			if (i < 0 || i >= lines.length)
				continue;
			if (i == lineNum - 1)
				buf.append(" >> ");
			buf.append(lines[i]);
			buf.append("\n");
		}
		
		return buf.toString();
	}

	/**
	 * Get the string context at a given position. 
	 * This returns a substring of the current script around
	 * the given position.
	 * 
	 * @param script
	 * @param i
	 * @return
	 */
	private String getContext(String script, int i) {
		return script.substring( i - 20, i + 20 );
	}

	/**
	 * Repeat a string n times.
	 * 
	 * @param string string to repeat
	 * @param n times to repeat
	 * @return
	 * @throws RuntimeException if n < 0
	 */
	private String repeatString(String string, int n) {
		if (n < 0) {
			throw new RuntimeException("Cannot repeat a string <0 times (n=" + n + ")");
		}
		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < n; i++) {
			buf.append(string);
		}
		return buf.toString();
	}

	/**
	 * When formatting the output, a number of substitutions
	 * are made (e.g. taking out "strings" so they may be
	 * formatted without losing whitespace). This returns
	 * all the substitutions that have been made, in no
	 * particular order. 
	 * 
	 * @return All substitutions made
	 */
	public Map<String,String> getAllSubstitutions() {
		Map<String,String> r = new HashMap<String,String>();
		r.putAll( replaceBlockComments );
		r.putAll( replaceLineComments );
		r.putAll( replaceQuotes );
		r.putAll( replaceQuotesSq );
		return r;
	}

	/**
	 * Returns all string substitutions only.
	 * 
	 * @see #getAllSubstitutions()
	 * @return
	 */
	public Map<String,String> getStringSubstitutions() {
		Map<String,String> r = new HashMap<String,String>();
		r.putAll( replaceQuotes );
		r.putAll( replaceQuotesSq );
		return r;
	}

	/**
	 * This method doesn't actually do anything different; the 
	 * extension parameter is ignored.
	 * 
	 * @see org.openiaml.iacleaner.IACleaner#cleanScript(java.lang.String, java.lang.String)
	 */
	public String cleanScript(String script, String extension)
			throws CleanerException {
		return cleanScript(script);
	}

}
