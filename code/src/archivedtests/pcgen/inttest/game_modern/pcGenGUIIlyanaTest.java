package pcgen.inttest.game_modern;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Modern Tough Hero 3/Strong Hero 3.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIIlyanaTest extends pcGenGUITestCase
{

	/**
	 * 
	 */
	public pcGenGUIIlyanaTest()
	{
		// Empty Constructor
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIIlyanaTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIIlyanaTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testIlyana() throws Exception
	{
		runTest("msrd_Ilyana", "Modern");
	}
}
