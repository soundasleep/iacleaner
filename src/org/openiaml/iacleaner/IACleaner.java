/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.openiaml.iacleaner.ast.ASTHtmlClosingTag;
import org.openiaml.iacleaner.ast.ASTHtmlTag;
import org.openiaml.iacleaner.ast.ASTHtmlTagAttribute;
import org.openiaml.iacleaner.ast.ASTHtmlTextBlock;
import org.openiaml.iacleaner.ast.ASTStart;
import org.openiaml.iacleaner.ast.InternetApplication;
import org.openiaml.iacleaner.ast.InternetApplicationVisitor;
import org.openiaml.iacleaner.ast.ParseException;
import org.openiaml.iacleaner.ast.SimpleNode;

/**
 * A syntax formatter for Internet 
 * Applications: attempts to clean HTML, Javascript, CSS, PHP and other
 * languages.
 * 
 * @author Jevon
 *
 */
public class IACleaner {

	public static class CleanerException extends Exception {
		private static final long serialVersionUID = 1L;

		public CleanerException(String message) {
			super(message);
		}
		
		public CleanerException(String message, String context) {
			super(message + " [context=\"" + context + "\"]");
		}

		public CleanerException(Throwable e) {
			super(e.getMessage(), e);
		}
	}
	
	public List<String> warnings = new ArrayList<String>();
	
	/**
	 * Format a web script.
	 * 
	 * @param script
	 * @return
	 * @throws CleanerException if an exception occurs
	 */
	public String cleanScript(String script) throws CleanerException {

		SimpleNode node;
		try {
			node = InternetApplication.loadString(script, "UTF8");
		} catch (FileNotFoundException e) {
			throw new CleanerException(e);
		} catch (UnsupportedEncodingException e) {
			throw new CleanerException(e);
		} catch (ParseException e) {
			throw new CleanerException(e);
		}
		assert( node != null );
		
		// traverse the tree
		StringBuffer out = new StringBuffer();
		traverse(out, node);
		
		return out.toString();
		
	}

	private void traverse(StringBuffer out, SimpleNode node) {
		node.childrenAccept(visitor, out);
	}

	private final InternetApplicationVisitor visitor = new InternetApplicationVisitor() {
		@Override
		public Object visit(SimpleNode node, Object data) {
			((StringBuffer) data).append("unknown node: " + node);
			return null;
		}

		@Override
		public Object visit(ASTStart node, Object data) {
			node.childrenAccept(visitor, data);	// visit children
			return null;
		}

		@Override
		public Object visit(ASTHtmlTag node, Object data) {
			((StringBuffer) data).append("<").append(node.getName());
			node.childrenAccept(visitor, data);	// visit attributes
			((StringBuffer) data).append(">");
			return null;
		}

		@Override
		public Object visit(ASTHtmlClosingTag node, Object data) {
			((StringBuffer) data).append("</").append(node.getName());
			((StringBuffer) data).append(">");
			return null;
		}

		@Override
		public Object visit(ASTHtmlTagAttribute node, Object data) {
			((StringBuffer) data).append(" ").append(node.getName()).append("=").append(node.getValue());
			return null;
		}

		@Override
		public Object visit(ASTHtmlTextBlock node, Object data) {
			((StringBuffer) data).append(node.getText().trim());
			return null;
		}
		
	};

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
