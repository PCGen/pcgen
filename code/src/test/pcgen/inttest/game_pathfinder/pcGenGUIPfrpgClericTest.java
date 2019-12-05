/*
 */
package pcgen.inttest.game_pathfinder;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case of a pathfinder cleric.
 * See the PCG file for details
 */

public class pcGenGUIPfrpgClericTest extends PcgenFtlTestCase
{
    @Test
    public void testCode() throws Exception
    {
        runTest("pf_Cleric", "Pathfinder_RPG");
    }
}
