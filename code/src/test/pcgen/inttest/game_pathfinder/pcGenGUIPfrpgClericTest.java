package pcgen.inttest.game_pathfinder;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Unit Test Case of a pathfinder cleric.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIPfrpgClericTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIPfrpgClericTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIPfrpgClericTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIPfrpgClericTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("PFRPGCleric", "Pathfinder_RPG");
	}
}
