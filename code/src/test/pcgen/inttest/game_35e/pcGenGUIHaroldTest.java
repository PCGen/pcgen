package pcgen.inttest.game_35e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Unit Test Case designed to hit many features of PCGen
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIHaroldTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIHaroldTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIHaroldTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIHaroldTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("Harold", "35e");
	}
}
