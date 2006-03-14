package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIBrdJoeTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIBrdJoeTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIBrdJoeTest.class);
	}

    public void testBrdJoe() throws Exception {
        runTest("BrdJoe", "3e");
    }


}