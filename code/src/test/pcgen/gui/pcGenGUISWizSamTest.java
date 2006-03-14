package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUISWizSamTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUISWizSamTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUISWizSamTest.class);
	}

   public void testSWizSam() throws Exception {
        runTest("SWizSam", "3e");
    }



}