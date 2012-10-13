package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test a 3e 1st lvl Half-Elf Bard 
 */
@SuppressWarnings("nls")
public class pcGenGUIBrdJoeTest extends pcGenGUITestCase
{

	/**
	 * standard JUnit style constructor
	 *  
	 * @param name No idea
	 */
	public pcGenGUIBrdJoeTest(String name)
	{
		super(name);
	}

	/**
	 * Returns the test suite containing all the tests in this class.
	 * 
	 * @return A <tt>TestSuite</tt> 
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIBrdJoeTest.class);
	}

	/**
	 * Runs the test.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testBrdJoe() throws Exception
	{
		runTest("BrdJoe", "3e");
	}

}