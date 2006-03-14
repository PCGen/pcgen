package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIDrakelingSorcTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIDrakelingSorcTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIDrakelingSorcTest.class);
	}
    public void testDrakelingSorc() throws Exception {
        runTest("DrakelingSorc", "3e");
    }


}