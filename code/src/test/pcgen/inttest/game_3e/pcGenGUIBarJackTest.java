package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 4th lvl Gnome Barbarian
 */
@SuppressWarnings("nls")
public class pcGenGUIBarJackTest extends pcGenGUITestCase
{

	/**
	 * Runs the test
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIBarJackTest.class);
	}

	/**
	 * Default constructor
	 */
	public pcGenGUIBarJackTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * @param name No idea
	 */
	public pcGenGUIBarJackTest(String name)
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
		return new TestSuite(pcGenGUIBarJackTest.class);
	}

	/**
	 * Main test case.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testBarJack() throws Exception
	{
		runTest("BarJack", "3e");
	}
}
