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
public class pcGenGUISFmechanicTest extends PcgenFtlTestCase
{


	public pcGenGUISFmechanicTest()
	{
		super("sf_mechanic");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUISFmechanicTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUISFmechanicTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("sf_mechanic", "Starfinder");
	}
}
