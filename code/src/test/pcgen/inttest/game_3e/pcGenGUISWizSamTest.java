package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 1st level Halfling Wizard(Transmuter).
 * 
 * See PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUISWizSamTest extends pcGenGUITestCase
{
	/**
	 * Run the tests.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUISWizSamTest.class);
	}

	/** 
	 * Standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUISWizSamTest(String name)
	{
		super(name);
	}

	/**
	 * Return test
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUISWizSamTest.class);
	}

	/**
	 * Run test
	 * @throws Exception
	 */
	public void testSWizSam() throws Exception
	{
		runTest("SWizSam", "3e");
	}
}