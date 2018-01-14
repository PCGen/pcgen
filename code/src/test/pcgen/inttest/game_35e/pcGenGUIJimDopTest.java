
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3.5e 2nd level Doppleganger Fighter with a Half-Dragon (Brass) 
 * template applied.
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUIJimDopTest extends PcgenFtlTestCase
{


	public pcGenGUIJimDopTest()
	{
		super("35e_jimdop");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name 
	 */
	public pcGenGUIJimDopTest(String name)
	{
		super(name);
	}

	/**
	 * Return a suite containing all the tests in this class.
	 * 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIJimDopTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testJimDop() throws Exception
	{
		runTest("35e_JimDop", "35e");
	}
}
