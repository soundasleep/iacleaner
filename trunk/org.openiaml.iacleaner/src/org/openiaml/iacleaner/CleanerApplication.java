/**
 * 
 */
package org.openiaml.iacleaner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Allows iacleaner to be executed from the command line. 
 * 
 * <p>Implements issue 6.
 * 
 * @author jmwright
 *
 */
public class CleanerApplication {
	
	private static class CleanerApplicationException extends Exception {
		private static final long serialVersionUID = 1L;

		private CleanerApplicationException(String message) {
			super(message);
		}
	}
	
	/**
	 * Print out the help menu.
	 */
	private static void printHelp() {
		// TODO add version information
		System.out.println("Usage: java -jar iacleaner.jar [args...]");
		System.out.println();
		System.out.println(" --input <file>     Use input file, otherwise stdin");
		System.out.println(" --output <file>    Use output file, otherwise stdout");
		System.out.println(" --extension <file> Use specified extension");
		System.out.println(" --help             Display this help");
		System.out.println();
	}

	/**
	 * Permitted arguments:
	 * 
	 * <ul>
	 * <li><code>--input filename</code> : input from the given filename, otherwise from stdin
	 * <li><code>--output filename</code> : output to the given filename, otherwise to stdout
	 * <li><code>--extension ext</code> : use the given file extension, otherwise this is derived from the input filename
	 * <li><code>--help</code> : displays the list of commands
	 * </ul>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			execute(args);
		} catch (CleanerApplicationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (CleanerException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
		
	/**
	 * Actually execute iacleaner.
	 * 
	 * @param args args from command line
	 * @throws CleanerApplicationException if an invalid argument is provided
	 * @throws CleanerException if cleaning failed
	 * @throws IOException if an IO exception occurs
	 */
	public static void execute(String[] args) throws CleanerApplicationException, CleanerException, IOException {
		String input = null;
		String output = null;
		String extension = null;
		
		// cycle through arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String nextArg = (i+1) >= args.length ? null : args[i+1];
			
			if ("--input".equals(arg) || "-i".equals(arg)) {
				if (nextArg == null) {
					throw new CleanerApplicationException("Expected filename for --input");
				} else {
					if (input != null)
						throw new CleanerApplicationException("Too many --input parameters");
					input = nextArg;
					i++;
				}
			} else if ("--output".equals(arg) || "-o".equals(arg)) {
				if (nextArg == null) {
					throw new CleanerApplicationException("Expected filename for --output");
				} else {
					if (output != null)
						throw new CleanerApplicationException("Too many --output parameters");
					output = nextArg;
					i++;
				}
			} else if ("--extension".equals(arg) || "-e".equals(arg)) {
				if (nextArg == null) {
					throw new CleanerApplicationException("Expected value for --extension");
				} else {
					if (extension != null)
						throw new CleanerApplicationException("Too many --extension parameters");
					extension = nextArg;
					i++;
				}
			} else if ("--help".equals(arg) || "/?".equals(arg)) { 
				printHelp();
				System.exit(0);
			} else {
				throw new CleanerApplicationException("Unexpected argument " + arg);
			}
		}
		
		// create cleaner
		IACleaner cleaner = new IAInlineCleaner();
		String cleaned = null;

		// actually execute cleaner
		if (input == null) {
			if (extension == null) {
				cleaned = cleaner.cleanScript(System.in);
			} else {
				cleaned = cleaner.cleanScript(System.in, extension);
			}
		} else {
			if (extension == null) {
				cleaned = cleaner.cleanScript(new File(input));
			} else {
				cleaned = cleaner.cleanScript(new File(input), extension);
			}
		}
		
		if (output == null) {
			// output to stdout
			System.out.println(cleaned);
		} else {
			FileWriter writer = new FileWriter(new File(output));
			writer.write(cleaned);
			writer.close();
		}

		// done
		System.exit(0);		
	}


}
