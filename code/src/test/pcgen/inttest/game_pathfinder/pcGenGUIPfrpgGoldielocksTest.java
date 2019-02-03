/*
 */
package pcgen.inttest.game_pathfinder;

import java.io.IOException;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Unit Test Case of a pathfinder great wyrm gold dragon.
 * See the PCG file for details
 */
public class pcGenGUIPfrpgGoldielocksTest extends PcgenFtlTestCase
{
	@Test
	public void testCode() throws IOException
	{
		runTest("pf_goldielocks", "Pathfinder_RPG");
	}
}
