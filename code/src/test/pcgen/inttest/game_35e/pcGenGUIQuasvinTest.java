
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 35e Psion.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIQuasvinTest extends PcgenFtlTestCase
{


	public pcGenGUIQuasvinTest()
	{
		super("35e_quasvin");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIQuasvinTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIQuasvinTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testQuasvin() throws Exception
	{
		runTest("35e_Quasvin", "35e");
	}
}
