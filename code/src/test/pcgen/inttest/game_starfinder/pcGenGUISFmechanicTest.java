/*
 */
package pcgen.inttest.game_starfinder;

import java.io.IOException;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case of a starfinder Ysoki level 20 mechanic.
 * See the PCG file for details
 */
public class pcGenGUISFmechanicTest extends PcgenFtlTestCase
{
    @Test
    public void testCode() throws IOException
    {
        runTest("sf_mechanic", "Starfinder");
    }
}
