package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 8th level Human Monk.
 * 
 * See PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUIMonKeeTest extends pcGenGUITestCase
{
	/**
	 * Run the tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIMonKeeTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * @param name
	 */
	public pcGenGUIMonKeeTest(String name)
	{
		super(name);
	}

	/**
	 * Return a suite of all tests in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIMonKeeTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testMonKee() throws Exception
	{
		runTest("MonKee", "3e");
	}
}
