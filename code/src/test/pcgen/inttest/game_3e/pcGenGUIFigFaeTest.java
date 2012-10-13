package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 4th level Half-orc Fighter.
 * See the PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUIFigFaeTest extends pcGenGUITestCase
{

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name No idea
	 */
	public pcGenGUIFigFaeTest(String name)
	{
		super(name);
	}

	/**
	 * Return a suite of all the tests in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIFigFaeTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testFigFae() throws Exception
	{
		runTest("FigFae", "3e");
	}
}