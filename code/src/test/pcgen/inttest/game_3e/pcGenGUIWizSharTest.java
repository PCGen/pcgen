package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 1st level Human Wizard.
 * 
 * See the PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUIWizSharTest extends pcGenGUITestCase
{
	/**
	 * Run the tests.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIWizSharTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * @param name 
	 */
	public pcGenGUIWizSharTest(String name)
	{
		super(name);
	}

	/**
	 * Standard JUnit suite call
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIWizSharTest.class);
	}

	/**
	 * Run the test
	 * @throws Exception
	 */
	public void testWizShar() throws Exception
	{
		runTest("WizShar", "3e");
	}

}