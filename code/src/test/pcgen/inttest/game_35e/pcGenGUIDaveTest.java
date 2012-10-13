package pcgen.inttest.game_35e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Unit Test Case designed to hit many features of PCGen
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIDaveTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIDaveTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIDaveTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIDaveTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("Dave", "35e");
	}
}
