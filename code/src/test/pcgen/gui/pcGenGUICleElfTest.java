package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUICleElfTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUICleElfTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUICleElfTest.class);
	}

    public void testCleElf() throws Exception {
        runTest("CleElf", "3e");
    }


}