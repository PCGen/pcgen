package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Modern Fast Hero 3/Infiltrator 3.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIBobTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIBobTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIBobTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIBobTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws Exception
	{
		runTest("Bob", "35e");
	}
}
