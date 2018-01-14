/*
 */
package pcgen.inttest.game_pathfinder;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Unit Test Case of a pathfinder great wyrm gold dragon.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIPfrpgGoldielocksTest extends pcGenGUITestCase
{


	public pcGenGUIPfrpgGoldielocksTest()
	{
		//	super("pf_goldielocks");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIPfrpgGoldielocksTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIPfrpgGoldielocksTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("pf_goldielocks", "Pathfinder_RPG");
	}
}
