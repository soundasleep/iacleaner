/**
 * 
 */
package org.openiaml.iacleaner.tests;

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

	/**
	 * Default constructor.
	 * 
	 * @param title
	 */
	public TimedTestSuite(String title) {
		super(title);
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
	}
	
}