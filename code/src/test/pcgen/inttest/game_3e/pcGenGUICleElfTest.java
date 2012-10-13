package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 2nd level Elf Cleric Law and Protection domains.
 */
@SuppressWarnings("nls")
public class pcGenGUICleElfTest extends pcGenGUITestCase
{

	/**
	 * Run the test.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUICleElfTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name No Idea.
	 */
	public pcGenGUICleElfTest(String name)
	{
		super(name);
	}

	/**
	 * Returns a test suite of all the tests in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUICleElfTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If there is a problem.
	 */
	public void testCleElf() throws Exception
	{
		runTest("CleElf", "3e");
	}
}