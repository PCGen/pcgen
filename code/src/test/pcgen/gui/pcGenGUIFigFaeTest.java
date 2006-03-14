package pcgen.gui;

import junit.framework.Test;
import junit.framework.TestSuite;



public class pcGenGUIFigFaeTest extends pcGenGUITestCase {

    // standard JUnit style constructor
    public pcGenGUIFigFaeTest(String name) {
        super(name);
    }

	public static Test suite()
	{
		return new TestSuite(pcGenGUIFigFaeTest.class);
	}

   public void testFigFae() throws Exception {
        runTest("FigFae", "3e");
    }



}