
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case designed to hit many features of PCGen
 * See the PCG file for details
 */
public class pcGenGUIGordonTest extends PcgenFtlTestCase
{
    @Test
    public void testCode() throws Exception
    {
        runTest("35e_Gordon", "35e");
    }
}
