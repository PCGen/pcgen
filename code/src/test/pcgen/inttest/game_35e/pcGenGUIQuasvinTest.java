package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a 35e Psion.
 * See the PCG file for details
 */
public class pcGenGUIQuasvinTest extends PcgenFtlTestCase
{
	@Test
	public void testQuasvin() throws Exception
	{
		runTest("35e_Quasvin", "35e");
	}
}
