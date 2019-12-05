
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a 3.5e 2nd level Doppleganger Fighter with a Half-Dragon (Brass)
 * template applied.
 * See PCG file for details.
 */
public class pcGenGUIJimDopTest extends PcgenFtlTestCase
{
    @Test
    public void testJimDop() throws Exception
    {
        runTest("35e_JimDop", "35e");
    }
}
