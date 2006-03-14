package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

public class pcGenGUIBarJackTest
		extends pcGenGUITestCase
{

	public pcGenGUIBarJackTest()
	{
		// Empty Constructor
	}

	// standard JUnit style constructor
	public pcGenGUIBarJackTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(pcGenGUIBarJackTest.class);
	}

	public void testBarJack()
			throws Exception
	{
		runTest("BarJack", "3e");
	}
}
