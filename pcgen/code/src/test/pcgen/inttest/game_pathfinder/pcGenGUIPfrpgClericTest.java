/*
 */
package pcgen.inttest.game_pathfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.inttest.PcgenFtlTestCase;

/**
 * Tests a Unit Test Case of a pathfinder cleric.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIPfrpgClericTest extends PcgenFtlTestCase
{


	public pcGenGUIPfrpgClericTest()
	{
		super("pf_cleric");
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
		runTest("pf_Cleric", "Pathfinder_RPG");
	}
}
