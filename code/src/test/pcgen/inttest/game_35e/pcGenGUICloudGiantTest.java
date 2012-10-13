package pcgen.inttest.game_35e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3.5e Cloud Giant with a Half-Dragon (Brass) template applied.
 * See it's PCG file for what it contains.  
 */
@SuppressWarnings("nls")
public class pcGenGUICloudGiantTest extends pcGenGUITestCase
{

	/**
	 * standard JUnit style constructor
	 * @param name
	 */
	public pcGenGUICloudGiantTest(String name)
	{
		super(name);
	}

	/**
	 * return the test suite for this test
	 * @return the test suite for this test
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUICloudGiantTest.class);
	}

	/**
	 * Run the test
	 * @throws Exception
	 */
	public void testCloudGiantHalfDragon() throws Exception
	{
		runTest("CloudGiantHalfDragon", "35e");
	}

}