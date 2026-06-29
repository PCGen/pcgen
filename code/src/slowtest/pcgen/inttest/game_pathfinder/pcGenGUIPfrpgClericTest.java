/*
 */
package pcgen.inttest.game_pathfinder;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case of a pathfinder cleric.
 * See the PCG file for details
 */

class pcGenGUIPfrpgClericTest extends PcgenFtlTestCase
{
	@Test
	void testCode() throws Exception
	{
		runTest("pf_Cleric");
	}
}
