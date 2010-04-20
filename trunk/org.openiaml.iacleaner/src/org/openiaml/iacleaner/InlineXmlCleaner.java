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
		
		while (true) {
			String next5 = reader.readAhead(5);
			if (next5 != null && next5.equals("<?php")) {
				// php mode!
				writer.enableWordwrap(true);
				getInline().cleanPhpBlock(reader, writer);
				writer.enableWordwrap(false);
				// we may continue with html mode
			} else {
				int c = reader.read();
				if (c == -1)
					break;
				writer.write(c);				
			}
		}
		
		writer.enableWordwrap(true);
	}

}
