package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIMonKeeTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIMonKeeTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIMonKeeTest.class);
	}

   public void testMonKee() throws Exception {
        runTest("MonKee", "3e");
    }



}