/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple regular-expression based syntax formatter for Internet 
 * Applications: attempts to clean HTML, Javascript, CSS, PHP and other
 * languages.
 * 
 * @author Jevon
 *
 */
public class IACleaner {
	
	public static final String KEY_QUOTE = "$hide_string_";
	public static final String KEY_QUOTE_SQ = "$hide_string_sq_";
	public static final String KEY_END = "$";
	
	public static final String REPLACE_QUOTE = "$replace_quote$";
	public static final String REPLACE_QUOTE_SQ = "$replace_quote_sq$";
	
	public static final String HTML_INDENT_TAGS = "(html|head|body|div|script)";
	
	public static class CleanerException extends Exception {
		private static final long serialVersionUID = 1L;

		public CleanerException(String message) {
			super(message);
		}
		
		public CleanerException(String message, String context) {
			super(message + " [context=\"" + context + "\"]");
		}
	}
	
	public List<String> warnings = new ArrayList<String>();
	
	/**
	 * Get exceptions to the formatting: these strings are always
	 * explicitly taken out at the start of formatting.
	 * 
	 * This is to deal with regular expressions in Javascript mostly,
	 * e.g. prototype.js:
	 * 	<code>
	 *   escapedString.replace(/"/g, '\\"')
	 *  </code>
	 */
	public Map<String,String> getExceptions() {
		Map<String,String> e = new HashMap<String,String>();
		
		// prototype.js exceptions
		e.put( "/\"/g", "$exception_key_1$" );
		e.put( "/\"[^\"\\\\\\n\\r]*\"/g", "$exception_key_2$" );
		
		return e;
	}
	
	/**
	 * Format a web script using regular expressions.
	 * 
	 * @param script
	 * @return
	 * @throws CleanerException if an exception occurs
	 */
	public String cleanScript(String script) throws CleanerException {
		// get rid of all explicit exceptions
		for (String key : getExceptions().keySet()) {
			script = script.replace(key, getExceptions().get(key));
		}
				
		// replace all escaped quotes
		/*
		script = script.replace("\\\"", REPLACE_QUOTE);
		script = script.replace("\\'", REPLACE_QUOTE_SQ);
		*/
		
		// a new method: eat up characters in sequence and use these to work out 
		// when to stop a line.
		int MODE_STRING_DOUBLE = 1;
		int MODE_STRING_SINGLE = 2;
		int MODE_COMMENT_LINE = 3;
		int MODE_COMMENT_MULTI = 4;
		int MODE_REGEXP = 5;
		int mode = 0;
		StringBuffer buf = new StringBuffer();
		StringBuffer cur = new StringBuffer();
		int replace_count = 0;
		Map<String,String> replaceElements = new HashMap<String,String>();
		for (int i = 0; i < script.length(); i++) {
			String s1 = script.substring(i, i + 1);
			String s2 = null;
			if (script.length() > i + 1) {		// lookahead of 2
				s2 = script.substring(i, i + 2);
			}
			String s5 = null;
			if (script.length() > i + 4) {		// lookahead of 5
				s5 = script.substring(i, i + 5);
			}
			
			if (mode == 0) {
				// starting a new mode?
				if (s1.equals("\"")) {
					mode = MODE_STRING_DOUBLE;
					cur.append(s1);
				} else if (s1.equals("'")) {
					mode = MODE_STRING_SINGLE;
					cur.append(s1);
				} else if ("//".equals(s2)) {
					mode = MODE_COMMENT_LINE;
					cur.append(s2);
					i++;
				} else if ("/*".equals(s2)) {
					mode = MODE_COMMENT_MULTI;
					cur.append(s2);
					i++;
				} else if ("/".equals(s1) && false) {
					mode = MODE_REGEXP;
					cur.append(s1);
					i++;
				} else {
					buf.append(s1);
				}
			} else if (mode == MODE_STRING_DOUBLE) {
				// in a "string"
				if ("\\\"".equals(s2)) {
					// escaping a " character
					cur.append(s2);
					i++;
				} else if ("\"".equals(s1)) {
					// end
					cur.append(s1);
					String key = "$replace_" + replace_count + "$";
					buf.append(key);
					replaceElements.put(key, cur.toString());
					replace_count++;
					cur = new StringBuffer();
					mode = 0;
				} else {
					cur.append(s1);
				}
			} else if (mode == MODE_STRING_SINGLE) {
				// in a 'string'
				if ("\\'".equals(s2)) {
					// escaping a ' character
					cur.append(s2);
					i++;
				} else if ("\'".equals(s1)) {
					// end
					cur.append(s1);
					String key = "$replace_" + replace_count + "$";
					buf.append(key);
					replaceElements.put(key, cur.toString());
					replace_count++;
					cur = new StringBuffer();
					mode = 0;
				} else {
					cur.append(s1);
				}
			} else if (mode == MODE_COMMENT_LINE) {
				// in a // comment
				if ("\n".equals(s1)) {
					// end
					//	cur.append(s1); // ignore the newline
					String key = "$replace_" + replace_count + "$";
					buf.append(key).append("$line_break$");
					replaceElements.put(key, cur.toString());
					replace_count++;
					cur = new StringBuffer();
					mode = 0;
				} else {
					cur.append(s1);
				}
			} else if (mode == MODE_COMMENT_MULTI) {
				// in a /* comment */
				if ("*/".equals(s2)) {
					// end
					cur.append(s2);
					i++;
					String key = "$replace_" + replace_count + "$";
					buf.append(key).append("$line_break$");
					replaceElements.put(key, cur.toString());
					replace_count++;
					cur = new StringBuffer();
					mode = 0;
				} else {
					cur.append(s1);
				}
			} else if (mode == MODE_REGEXP) {
				// in a /regexp/
				if ("\\/".equals(s2)) {
					// escaping a / character
					cur.append(s2);
					i++;
				} else if ("/".equals(s1)) {
					// end
					cur.append(s1);
					String key = "$replace_" + replace_count + "$";
					buf.append(key);
					replaceElements.put(key, cur.toString());
					replace_count++;
					cur = new StringBuffer();
					mode = 0;
				} else {
					cur.append(s1);
				}
			} else {
				throw new CleanerException("unknown mode: " + mode);
			}
		}
		
		// if we are still in a mode, we have failed
		if (mode != 0) {
			System.out.println(buf);
			String bufString = buf.toString();
			throw new CleanerException("unexpectedly still in mode: " + mode, 
					bufString.length() > 128 ? 
						(bufString.substring(bufString.length() - 128, bufString.length())) : 
						bufString);
		}
		
		// get out the replaced version
		script = buf.toString();
		
		/*

		// find all single strings and replace them
		Map<String,String> replaceQuotes = new HashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("\"", i);
			if (start != -1) {
				int end = script.indexOf("\"", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated \"\" string at character position " + i, getContext(script, i));
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

		// find all single strings and replace them (single quotes)
		Map<String,String> replaceQuotesSq = new HashMap<String,String>();
		for (int i = 0; i < script.length(); ) {
			int start = script.indexOf("'", i);
			if (start != -1) {
				int end = script.indexOf("'", start + 1);
				if (end == -1) {
					throw new CleanerException("Unterminated '' string at character position " + i, getContext(script, i));
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

		// replace single-line comments with block comments
		script = script.replaceAll("([^:])//([^\r\n]+)[\r\n]", "$1/*$2 *
		/\n");	// first char disables replacing http:// addresses" +
				"*/

		// get rid of all newlines and tabs
		script = script.replaceAll("[\r\n\t]", " ");

		// get rid of multiple spaces
		script = script.replaceAll(" [ ]+", " ");
		
		// newlines at ends of statements
		script = script.replace("; ", ";\n");
		script = script.replace("{ ", "{\n");
		script = script.replace("} ", "}\n");
		script = script.replace("$line_break$ ", "\n");
		script = script.replace("*/ ", "*/\n");

		// newlines at ends of html tags
		script = script.replace("> ", ">\n");

		// add indents to code blocks and php blocks
		String[] lines = script.split("\n");
		int brace_count = 0;
		
		String match_indent_open = "(?i)<" + HTML_INDENT_TAGS + "[^>]*>";
		String match_indent_close = "(?i)</" + HTML_INDENT_TAGS + "[^>]*>";
		buf = new StringBuffer();
		for (String line : lines) {
			boolean nextBrace = false;
			
			if (line.indexOf("}") != -1) {
				// close the brace
				brace_count--;
			} else if (line.matches(match_indent_close)) {
				// close the tag
				brace_count--;
			}
			
			int next_brace_count = brace_count;
			if (line.indexOf("{") != -1) {
				// open the brace for next lines
				next_brace_count++;
			} else if (line.matches(match_indent_open)) {
				// open the tag for next lines
				next_brace_count++;
			}
			
			// indent the line
			if (brace_count < 0) {
				if (next_brace_count < 0) {
					// this means that the next line will definitely be out
					throwWarning("Unbalanced braces as of line: " + line);
				}
				// otherwise, it could be a line like "{ }"
				brace_count = 0;
				next_brace_count = 0;
			}
			line = repeatString("  ", brace_count) + line;
			
			brace_count = next_brace_count;
			
			buf.append(line).append("\n");
		}
		
		// implode it back together
		script = buf.toString();
		
		// put all the strings back in
		for (String key : replaceElements.keySet()) {
			String value = replaceElements.get(key);
			script = script.replace(key, value);
		}
		/*
		for (String key : replaceQuotesSq.keySet()) {
			String value = replaceQuotesSq.get(key);
			script = script.replace(key, value);
		}
		for (String key : replaceQuotes.keySet()) {
			String value = replaceQuotes.get(key);
			script = script.replace(key, value);
		}
		script = script.replace(REPLACE_QUOTE_SQ, "\\'");
		script = script.replace(REPLACE_QUOTE, "\\\"");
		*/
		
		// return explicit exceptions
		for (String key : getExceptions().keySet()) {
			script = script.replace(getExceptions().get(key), key);
		}
		
		return script;
	}

	/**
	 * Get the string context at a given position.
	 * 
	 * @param script
	 * @param i
	 * @return
	 */
	private String getContext(String script, int i) {
		return script.substring( i - 20, i + 20 );
	}

	/**
	 * Throw a warning. Prints it out to stderr and adds it to
	 * {@link #getWarnings()}
	 * 
	 * @see #getWarnings()
	 * @param string
	 */
	private void throwWarning(String string) {
		System.err.println("Warning: " + string);
		warnings.add(string);
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
	 * Format a file.
	 * 
	 * @see IACleaner#cleanScript(String)
	 * @param sourceFile
	 * @return
	 * @throws IOException if an IO exception occurs
	 * @throws CleanerException if a cleaner exception occurs
	 */
	public String cleanScript(File sourceFile) throws IOException, CleanerException {
		return cleanScript(readFile(sourceFile));
	}
	
	/**
	 * Read in a file into a string.
	 * 
	 * @throws IOException if an IO exception occurs
	 */
	public static String readFile(File sourceFile) throws IOException {
		int bufSize = 128;
		StringBuffer sb = new StringBuffer(bufSize);
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile), bufSize);
				
		char[] chars = new char[bufSize];
		int numRead = 0;
		while ((numRead = reader.read(chars)) > -1) {
			sb.append(String.valueOf(chars).substring(0, numRead));	
		}
		
		reader.close();
		return sb.toString();
	}
	
	/**
	 * Have any errors occured?
	 * 
	 * @see #getWarnings()
	 * @return
	 */
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	/**
	 * Get all of the warnings thrown.
	 * 
	 * @return
	 */
	public List<String> getWarnings() {
		return warnings;
	}
	
}
