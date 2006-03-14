package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test a Cloud Giant PCG via PCGen, see it's PCG file for what 
 * it contains  
 */
public class pcGenGUICloudGiantTest extends pcGenGUITestCase {

    /**
     * standard JUnit style constructor
     * @param name
     */
    public pcGenGUICloudGiantTest(String name) {
        super(name);
    }
	
    /**
     * return the test suite for this test
     * @return the test suite for this test
     */
    public static Test suite()
	{
		return new TestSuite(pcGenGUICloudGiantTest.class);
	}

	/**
	 * Run the test
	 * @throws Exception
	 */
    public void testCloudGiantHalfDragon() throws Exception {
        runTest("CloudGiantHalfDragon", "35e");
    }

}