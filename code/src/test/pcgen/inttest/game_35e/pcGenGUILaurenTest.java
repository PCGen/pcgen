
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUILaurenTest extends PcgenFtlTestCase
{


	public pcGenGUILaurenTest()
	{
		super("35e_lauren");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name 
	 */
	public pcGenGUILaurenTest(String name)
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
		return new TestSuite(pcGenGUILaurenTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testLauren() throws Exception
	{
		runTest("35e_Lauren", "35e");
	}
}
