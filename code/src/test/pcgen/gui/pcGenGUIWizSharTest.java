package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test to test the WizardShar  
 */
public class pcGenGUIWizSharTest extends pcGenGUITestCase {

    /**
     * standard JUnit style constructor
     * @param name 
     */
    public pcGenGUIWizSharTest(String name) {
        super(name);
    }

    /**
     * Standard JUnit suite call
     * @return Test
     */
	public static Test suite() {
		return new TestSuite(pcGenGUIWizSharTest.class);
	}

    /**
     * Run the test
     * @throws Exception
     */
    public void testWizShar() throws Exception {
        runTest("WizShar", "3e");
    }

}