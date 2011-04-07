/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.IOException;

import org.openiaml.iacleaner.inline.InlineStringReader;
import org.openiaml.iacleaner.inline.InlineStringWriter;


/**
 * Handles the cleaning of XML content. This doesn't actually
 * clean XML at all, but it allows for PHP content to be parsed
 * mid-stream.
 * 
 * <p>It is only safe to format white space in two scenarios: when cleaning
 * PHP code, or when processing the contents of &lt;tags&gt;. Another scenario
 * that is not implemented yet is XML comments.
 * 
 * @author Jevon
 *
 */
public class InlineXmlCleaner {
	
	private IAInlineCleaner inline;
	
	public InlineXmlCleaner(IAInlineCleaner inline) {
		this.inline = inline;
	}
	
	public IAInlineCleaner getInline() {
		return inline;
	}

	/**
	 * Clean up XML code; essentially, it just allows
	 * passbacks to PHP if necessary, otherwise outputs
	 * XML character-for-character.
	 * 
	 * @param reader
	 * @param writer
	 * @throws IOException 
	 * @throws CleanerException 
	 */
	public void cleanXmlBlock(InlineStringReader reader, InlineStringWriter writer) throws IOException, CleanerException {

		writer.enableWordwrap(false);
		
		boolean inTag = false;
		boolean inString = false;
		while (true) {
			String next5 = reader.readAhead(5);
			if (next5 != null && next5.equals("<?php")) {
				// php mode!
				boolean oldWordWrap = writer.canWordWrap();
				writer.enableWordwrap(true);
				getInline().cleanPhpBlock(reader, writer);
				writer.enableWordwrap(oldWordWrap);
				// we may continue with xml mode
			} else {
				int c = reader.read();
				if (c == -1) {
					// EOF
					break;
				} else if (!inTag && c == '<') {
					// started a <tag>
					inTag = true;
					writer.enableWordwrap(true);
				} else if (inTag && c == '>') {
					// ended a <tag>
					// all other < and >s have to be escaped in XML, so we don't
					// have to look for them explicitly
					inTag = false;
					writer.enableWordwrap(false);
				} else if (inTag && !inString && c == '"') {
					// started a "..." - must disable wordwrap
					inString = true;
					writer.enableWordwrap(false);
				} else if (inTag && inString && c == '"') {
					// ended a "..."
					inString = false;
					writer.enableWordwrap(true);
				}
				writer.write(c);
			}
		}
		
		writer.enableWordwrap(true);
	}

}
