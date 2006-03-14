package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIWizSharTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIWizSharTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIWizSharTest.class);
	}

   public void testWizShar() throws Exception {
        runTest("WizShar", "3e");
    }



}