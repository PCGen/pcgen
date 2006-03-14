package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIJimDopTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIJimDopTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIJimDopTest.class);
	}

   public void testJimDop() throws Exception {
        runTest("JimDop", "35e");
    }



}