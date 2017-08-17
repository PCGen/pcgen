
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs a test of the output of a bard. In particular this tests the output of 
 * abilities that are conditionally granted via variables and skill ranks.
 */
@SuppressWarnings("nls")
public class pcGenGUIBardTest extends PcgenFtlTestCase
{


	public pcGenGUIBardTest()
	{
		super("35e_bard");
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
		runTest("35e_Bard", "35e");
	}
}
