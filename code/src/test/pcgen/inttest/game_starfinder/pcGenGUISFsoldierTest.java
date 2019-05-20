/*
 */
package pcgen.inttest.game_starfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.inttest.PcgenFtlTestCase;

/**
 * Tests a Unit Test Case of a pathfinder cleric.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUISFsoldierTest extends PcgenFtlTestCase
{


	public pcGenGUISFsoldierTest()
	{
		super("sf_soldier");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUISFsoldierTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUISFsoldierTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("sf_soldier", "Starfinder");
	}
}
