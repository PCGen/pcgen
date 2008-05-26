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

	public void testBooleanFields()
	{
		// Defaults to true
		assertTrue(spi.memorizesSpells());
		spi.setMemorizeSpells(false);
		assertFalse(spi.memorizesSpells());
		// Defaults to false
		assertFalse(spi.usesSpellBook());
		spi.setSpellBookUsed(true);
		assertTrue(spi.usesSpellBook());
	}

	public void testBaseStats()
	{
		try
		{
			spi.setSpellBaseStatAbbr("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			spi.setSpellBaseStatAbbr(" ");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertEquals(Constants.s_NONE, spi.getSpellBaseStatAbbr());
		spi.setSpellBaseStatAbbr("INT");
		assertEquals("INT", spi.getSpellBaseStatAbbr());

		// Bonus
		try
		{
			spi.setBonusSpellBaseStatAbbr(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			spi.setBonusSpellBaseStatAbbr("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			spi.setBonusSpellBaseStatAbbr(" ");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertEquals(Constants.s_DEFAULT, spi.getBonusSpellBaseStatAbbr());
		spi.setBonusSpellBaseStatAbbr("DEX");
		assertEquals("DEX", spi.getBonusSpellBaseStatAbbr());
	}

	public void testKnownSpellsFromSpecialty()
	{
		try
		{
			spi.setKnownSpellsFromSpecialty(-1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		// Defaults to zero
		assertEquals(0, spi.getKnownSpellsFromSpecialty());
		spi.setKnownSpellsFromSpecialty(2);
		assertEquals(2, spi.getKnownSpellsFromSpecialty());
	}
}
