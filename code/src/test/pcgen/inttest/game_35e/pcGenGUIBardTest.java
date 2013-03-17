package pcgen.inttest.game_35e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs a test of the output of a bard. In particular this tests the output of 
 * abilities that are conditionally granted via variables and skill ranks.
 */
@SuppressWarnings("nls")
public class pcGenGUIBardTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIBardTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIBardTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIBardTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("35eBard", "35e");
	}
}
