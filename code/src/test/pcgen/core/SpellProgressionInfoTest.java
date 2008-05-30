package pcgen.core;

import junit.framework.TestCase;
import pcgen.cdom.base.Constants;

public class SpellProgressionInfoTest extends TestCase
{

	public SpellProgressionInfo spi;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		spi = new SpellProgressionInfo();
	}

	public void testSpellType()
	{
		try
		{
			spi.setSpellType(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			spi.setSpellType("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			spi.setSpellType(" ");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertEquals(Constants.s_NONE, spi.getSpellType());
		spi.setSpellType("DIVINE");
		assertEquals("DIVINE", spi.getSpellType());
	}
}
