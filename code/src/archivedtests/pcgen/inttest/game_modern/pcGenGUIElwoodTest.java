package pcgen.inttest.game_modern;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Modern Fast Hero 3/Infiltrator 3.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIElwoodTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIElwoodTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIElwoodTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIElwoodTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testElwood() throws Exception
	{
		runTest("Elwood", "Modern");
	}
}
