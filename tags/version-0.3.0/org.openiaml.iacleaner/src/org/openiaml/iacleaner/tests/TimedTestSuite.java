/**
 * 
 */
package org.openiaml.iacleaner.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Extends the default TestSuite to provide timer information
 * on executed tests.
 * 
 * Is marked as abstract so Eclipse doesn't try inserting it into
 * AllTests.
 * 
 * @author Jevon
 *
 */
public abstract class TimedTestSuite extends TestSuite {

	private FileWriter timedWriter;
	
	/**
	 * Default constructor.
	 * 
	 * @param title
	 * @throws IOException 
	 */
	public TimedTestSuite(String title, String file) throws IOException {
		super(title);
		timedWriter = new FileWriter(new File(file));
	}

	/**
	 * Extends {@link TestSuite#run(TestResult)} to provide an
	 * overall test elapsed time.
	 * 
	 * @see junit.framework.TestSuite#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult result) {
		long startTime = System.currentTimeMillis();
		super.run(result);
		long finishTime = System.currentTimeMillis();
		System.out.println("Total elapsed time: " + (finishTime - startTime) + " ms");
		try {
			timedWriter.write("Total elapsed time: " + (finishTime - startTime) + " ms\n");
			timedWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extends {@link TestSuite#runTest(Test, TestResult)} to provide
	 * an elapsed time counter between each test.
	 * 
	 * @see junit.framework.TestSuite#runTest(junit.framework.Test, junit.framework.TestResult)
	 */
	@Override
	public void runTest(Test test, TestResult result) {
		long startTime = System.currentTimeMillis();
		super.runTest(test, result);
		long finishTime = System.currentTimeMillis();
		System.out.println(test + ": " + (finishTime - startTime) + " ms");
		try {
			timedWriter.write(test + ": " + (finishTime - startTime) + " ms\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}