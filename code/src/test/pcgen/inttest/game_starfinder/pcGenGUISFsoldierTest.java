/*
 */
package pcgen.inttest.game_starfinder;

import java.io.IOException;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case of a starfinder Android soldier level 10.
 * See the PCG file for details
 */
public class pcGenGUISFsoldierTest extends PcgenFtlTestCase
{
    @Test
    public void testCode() throws IOException
    {
        runTest("sf_soldier", "Starfinder");
    }
}
