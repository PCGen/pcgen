
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a 35e Psion's PsiCrystal.
 * See the PCG file for details
 */
public class pcGenGUIQPsiCrystalTest extends PcgenFtlTestCase
{
    @Test
    public void testQPsiCrystal() throws Exception
    {
        runTest("35e_Q-PsiCrystal", "35e");
    }
}
