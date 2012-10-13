package pcgen.inttest.game_35e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3.5e 2nd level Doppleganger Fighter with a Half-Dragon (Brass) 
 * template applied.
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUIJimDopTest extends pcGenGUITestCase
{

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
		runTest("JimDop", "35e", true);
	}
}