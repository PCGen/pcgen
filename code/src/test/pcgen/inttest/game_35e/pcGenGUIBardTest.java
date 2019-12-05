
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Runs a test of the output of a bard. In particular this tests the output of
 * abilities that are conditionally granted via variables and skill ranks.
 */
public class pcGenGUIBardTest extends PcgenFtlTestCase
{

    @Test
    public void testCode() throws Exception
    {
        runTest("35e_Bard", "35e");
    }
}
