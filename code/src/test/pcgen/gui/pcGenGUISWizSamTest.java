package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test for Wizard Sam
 */
public class pcGenGUISWizSamTest extends pcGenGUITestCase {

    /** 
     * Standard JUnit style constructor
     * 
     * @param name
     */
    public pcGenGUISWizSamTest(String name) {
        super(name);
    }

    /**
     * Return test
     * @return Test
     */
	public static Test suite()
	{
		return new TestSuite(pcGenGUISWizSamTest.class);
	}

    /**
     * Run test
     * @throws Exception
     */
    public void testSWizSam() throws Exception {
        runTest("SWizSam", "3e");
    }



}