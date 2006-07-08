package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUICleElfTest extends pcGenGUITestCase {

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUICleElfTest.class);
	}
	
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