/*
 */
package pcgen.inttest.game_pathfinder;

import java.io.IOException;

import pcgen.inttest.PcgenFtlTestCase;

/**
 * Tests a Unit Test Case of a pathfinder great wyrm gold dragon.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIPfrpgGoldielocksTest extends PcgenFtlTestCase
{
	public pcGenGUIPfrpgGoldielocksTest()
	{
		super("pf_goldielocks");
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testCode() throws IOException
	{
		runTest("pf_goldielocks", "Pathfinder_RPG");
	}
}
