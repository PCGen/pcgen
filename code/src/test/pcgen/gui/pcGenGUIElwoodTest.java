package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

public class pcGenGUIElwoodTest
		extends pcGenGUITestCase
{

	public pcGenGUIElwoodTest()
	{
		// Empty Constructor
	}

	// standard JUnit style constructor
	public pcGenGUIElwoodTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(pcGenGUIElwoodTest.class);
	}

	public void testElwood()
			throws Exception
	{
		runTest("Elwood", "Modern");
	}
}
