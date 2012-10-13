package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 4th level Human Sorcerer with a Drakling template applied.
 * See PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUIDrakelingSorcTest extends pcGenGUITestCase
{
	/**
	 * Runs the test case.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIDrakelingSorcTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name No idea.
	 */
	public pcGenGUIDrakelingSorcTest(String name)
	{
		super(name);
	}

	/**
	 * Returns a test suite 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIDrakelingSorcTest.class);
	}

	/**
	 * Load and output the character.
	 * @throws Exception If an error occurs.
	 */
	public void testDrakelingSorc() throws Exception
	{
		// Commented out as it fails due to removal of dependant sources.
		//runTest("DrakelingSorc", "3e");
	}

}