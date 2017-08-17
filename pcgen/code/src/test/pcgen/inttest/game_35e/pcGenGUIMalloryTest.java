
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUIMalloryTest extends PcgenFtlTestCase
{


	public pcGenGUIMalloryTest()
	{
		super("35e_mallory");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */

	public pcGenGUIMalloryTest(String name)
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
		return new TestSuite(pcGenGUIMalloryTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testMallory() throws Exception
	{
		runTest("35e_Mallory", "35e");
	}
}
