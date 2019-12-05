
package pcgen.inttest.game_35e;

import java.io.IOException;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a 3.5e Cloud Giant with a Half-Dragon (Brass) template applied.
 * See it's PCG file for what it contains.
 */
public class pcGenGUICloudGiantTest extends PcgenFtlTestCase
{
    @Test
    public void testCloudGiantHalfDragon() throws IOException
    {
        runTest("35e_CloudGiantHalfDragon", "35e");
    }

}
