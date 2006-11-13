package pcgen.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class SpellProgressionInfoTest extends TestCase {

	public SpellProgressionInfo spi;

	protected void setUp() throws Exception {
		super.setUp();
		spi = new SpellProgressionInfo();
	}

	public void testKnown() {
		// Test it starts out empty
		assertFalse(spi.hasKnownProgression());
		// Test no NPE triggered even if no KNOWN is loaded
		assertNull(spi.getKnownForLevel(1));
		List<String> l = new ArrayList<String>();
		l.add("60");
		l.add("61");
		l.add("62");
		spi.setKnown(2, l);
		assertTrue(spi.hasKnownProgression());
		// Test for loaded values
		// implicitly tests that known doesn't require loading in level order
		assertEquals(3, spi.getKnownForLevel(2).size());
		assertEquals("60", spi.getKnownForLevel(2).get(0));
		assertEquals("61", spi.getKnownForLevel(2).get(1));
		assertEquals("62", spi.getKnownForLevel(2).get(2));
		l.clear();
		// Ensure still true (ensures that setKnown copied the list)
		assertTrue(spi.hasKnownProgression());
		assertEquals(3, spi.getKnownForLevel(2).size());
		assertEquals("60", spi.getKnownForLevel(2).get(0));
		assertEquals("61", spi.getKnownForLevel(2).get(1));
		assertEquals("62", spi.getKnownForLevel(2).get(2));
		// No known at levels below the loaded level
		assertNull(spi.getKnownForLevel(1));
		// Levels above loaded level return values of lower levels
		assertEquals(3, spi.getKnownForLevel(3).size());
		assertEquals("60", spi.getKnownForLevel(3).get(0));
		assertEquals("61", spi.getKnownForLevel(3).get(1));
		assertEquals("62", spi.getKnownForLevel(3).get(2));
		// Ensure we are transferred ownership of returned list
		List<String> returnList = spi.getKnownForLevel(2);
		returnList.clear();
		assertTrue(spi.hasKnownProgression());
		assertEquals(3, spi.getKnownForLevel(2).size());
		assertEquals("60", spi.getKnownForLevel(2).get(0));
		assertEquals("61", spi.getKnownForLevel(2).get(1));
		assertEquals("62", spi.getKnownForLevel(2).get(2));
		// Ensure SET is a SET not an ADD
		l.clear();
		l.add("51");
		l.add("50");
		spi.setKnown(2, l);
		assertEquals(2, spi.getKnownForLevel(2).size());
		assertEquals("51", spi.getKnownForLevel(2).get(0));
		assertEquals("50", spi.getKnownForLevel(2).get(1));
		// Some advanced testing (skipped levels)
		l.clear();
		l.add("43");
		l.add("42");
		l.add("41");
		l.add("40");
		spi.setKnown(4, l);
		assertEquals(4, spi.getKnownForLevel(4).size());
		assertEquals("43", spi.getKnownForLevel(4).get(0));
		assertEquals("42", spi.getKnownForLevel(4).get(1));
		assertEquals("41", spi.getKnownForLevel(4).get(2));
		assertEquals("40", spi.getKnownForLevel(4).get(3));
		assertEquals(4, spi.getKnownForLevel(5).size());
		assertEquals("43", spi.getKnownForLevel(5).get(0));
		assertEquals("42", spi.getKnownForLevel(5).get(1));
		assertEquals("41", spi.getKnownForLevel(5).get(2));
		assertEquals("40", spi.getKnownForLevel(5).get(3));
		assertEquals(2, spi.getKnownForLevel(2).size());
		assertEquals("51", spi.getKnownForLevel(2).get(0));
		assertEquals("50", spi.getKnownForLevel(2).get(1));
		assertEquals(2, spi.getKnownForLevel(3).size());
		assertEquals("51", spi.getKnownForLevel(3).get(0));
		assertEquals("50", spi.getKnownForLevel(3).get(1));
		// Highest Known Spell Level
		assertEquals(3, spi.getHighestKnownSpellLevel());
	}

	public void testSetKnownErrors() {
		List<String> l = new ArrayList<String>();
		try {
			spi.setKnown(1, null);
			fail("Set Known took null list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setKnown(1, l);
			fail("Set Known took empty list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add("60");
		try {
			spi.setKnown(0, l);
			fail("Set Known took level zero");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setKnown(-1, l);
			fail("Set Known took level negative level");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add(null);
		try {
			spi.setKnown(0, l);
			fail("Set Known took list containing null");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testSpecialtyKnown() {
		// Test it starts out empty
		assertFalse(spi.hasSpecialtyKnownProgression());
		// Test no NPE triggered even if no KNOWN is loaded
		assertNull(spi.getSpecialtyKnownForLevel(1));
		List<String> l = new ArrayList<String>();
		l.add("60");
		l.add("61");
		l.add("62");
		spi.setSpecialtyKnown(2, l);
		assertTrue(spi.hasSpecialtyKnownProgression());
		// Test for loaded values
		// implicitly tests that known doesn't require loading in level order
		assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
		assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0));
		assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1));
		assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2));
		l.clear();
		// Ensure still true (ensures that setKnown copied the list)
		assertTrue(spi.hasSpecialtyKnownProgression());
		assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
		assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0));
		assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1));
		assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2));
		// No known at levels below the loaded level
		assertNull(spi.getSpecialtyKnownForLevel(1));
		// Levels above loaded level return values of lower levels
		assertEquals(3, spi.getSpecialtyKnownForLevel(3).size());
		assertEquals("60", spi.getSpecialtyKnownForLevel(3).get(0));
		assertEquals("61", spi.getSpecialtyKnownForLevel(3).get(1));
		assertEquals("62", spi.getSpecialtyKnownForLevel(3).get(2));
		// Ensure we are transferred ownership of returned list
		List<String> returnList = spi.getSpecialtyKnownForLevel(2);
		returnList.clear();
		assertTrue(spi.hasSpecialtyKnownProgression());
		assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
		assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0));
		assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1));
		assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2));
		// Ensure SET is a SET not an ADD
		l.clear();
		l.add("51");
		l.add("50");
		spi.setSpecialtyKnown(2, l);
		assertEquals(2, spi.getSpecialtyKnownForLevel(2).size());
		assertEquals("51", spi.getSpecialtyKnownForLevel(2).get(0));
		assertEquals("50", spi.getSpecialtyKnownForLevel(2).get(1));
		// Some advanced testing (skipped levels)
		l.clear();
		l.add("43");
		l.add("42");
		l.add("41");
		l.add("40");
		spi.setSpecialtyKnown(4, l);
		assertEquals(4, spi.getSpecialtyKnownForLevel(4).size());
		assertEquals("43", spi.getSpecialtyKnownForLevel(4).get(0));
		assertEquals("42", spi.getSpecialtyKnownForLevel(4).get(1));
		assertEquals("41", spi.getSpecialtyKnownForLevel(4).get(2));
		assertEquals("40", spi.getSpecialtyKnownForLevel(4).get(3));
		assertEquals(4, spi.getSpecialtyKnownForLevel(5).size());
		assertEquals("43", spi.getSpecialtyKnownForLevel(5).get(0));
		assertEquals("42", spi.getSpecialtyKnownForLevel(5).get(1));
		assertEquals("41", spi.getSpecialtyKnownForLevel(5).get(2));
		assertEquals("40", spi.getSpecialtyKnownForLevel(5).get(3));
		assertEquals(2, spi.getSpecialtyKnownForLevel(2).size());
		assertEquals("51", spi.getSpecialtyKnownForLevel(2).get(0));
		assertEquals("50", spi.getSpecialtyKnownForLevel(2).get(1));
		assertEquals(2, spi.getSpecialtyKnownForLevel(3).size());
		assertEquals("51", spi.getSpecialtyKnownForLevel(3).get(0));
		assertEquals("50", spi.getSpecialtyKnownForLevel(3).get(1));
	}

	public void testSetSpecialtyKnownErrors() {
		List<String> l = new ArrayList<String>();
		try {
			spi.setSpecialtyKnown(1, null);
			fail("Set SpecialtyKnown took null list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setSpecialtyKnown(1, l);
			fail("Set SpecialtyKnown took empty list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add("60");
		try {
			spi.setSpecialtyKnown(0, l);
			fail("Set SpecialtyKnown took level zero");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setSpecialtyKnown(-1, l);
			fail("Set SpecialtyKnown took level negative level");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add(null);
		try {
			spi.setSpecialtyKnown(0, l);
			fail("Set SpecialtyKnown took list containing null");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testCast() {
		// Test it starts out empty
		assertFalse(spi.hasCastProgression());
		// Test no NPE triggered even if no Cast is loaded
		assertNull(spi.getCastForLevel(1));
		List<String> l = new ArrayList<String>();
		l.add("60");
		l.add("61");
		l.add("62");
		spi.setCast(2, l);
		assertTrue(spi.hasCastProgression());
		// Test for loaded values
		// implicitly tests that Cast doesn't require loading in level order
		assertEquals(3, spi.getCastForLevel(2).size());
		assertEquals("60", spi.getCastForLevel(2).get(0));
		assertEquals("61", spi.getCastForLevel(2).get(1));
		assertEquals("62", spi.getCastForLevel(2).get(2));
		l.clear();
		// Ensure still true (ensures that setCast copied the list)
		assertTrue(spi.hasCastProgression());
		assertEquals(3, spi.getCastForLevel(2).size());
		assertEquals("60", spi.getCastForLevel(2).get(0));
		assertEquals("61", spi.getCastForLevel(2).get(1));
		assertEquals("62", spi.getCastForLevel(2).get(2));
		// No Cast at levels below the loaded level
		assertNull(spi.getCastForLevel(1));
		// Levels above loaded level return values of lower levels
		assertEquals(3, spi.getCastForLevel(3).size());
		assertEquals("60", spi.getCastForLevel(3).get(0));
		assertEquals("61", spi.getCastForLevel(3).get(1));
		assertEquals("62", spi.getCastForLevel(3).get(2));
		// Ensure we are transferred ownership of returned list
		List<String> returnList = spi.getCastForLevel(2);
		returnList.clear();
		assertTrue(spi.hasCastProgression());
		assertEquals(3, spi.getCastForLevel(2).size());
		assertEquals("60", spi.getCastForLevel(2).get(0));
		assertEquals("61", spi.getCastForLevel(2).get(1));
		assertEquals("62", spi.getCastForLevel(2).get(2));
		// Ensure SET is a SET not an ADD
		l.clear();
		l.add("51");
		l.add("50");
		spi.setCast(2, l);
		assertEquals(2, spi.getCastForLevel(2).size());
		assertEquals("51", spi.getCastForLevel(2).get(0));
		assertEquals("50", spi.getCastForLevel(2).get(1));
		// Some advanced testing (skipped levels)
		l.clear();
		l.add("43");
		l.add("42");
		l.add("41");
		l.add("40");
		spi.setCast(4, l);
		assertEquals(4, spi.getCastForLevel(4).size());
		assertEquals("43", spi.getCastForLevel(4).get(0));
		assertEquals("42", spi.getCastForLevel(4).get(1));
		assertEquals("41", spi.getCastForLevel(4).get(2));
		assertEquals("40", spi.getCastForLevel(4).get(3));
		assertEquals(4, spi.getCastForLevel(5).size());
		assertEquals("43", spi.getCastForLevel(5).get(0));
		assertEquals("42", spi.getCastForLevel(5).get(1));
		assertEquals("41", spi.getCastForLevel(5).get(2));
		assertEquals("40", spi.getCastForLevel(5).get(3));
		assertEquals(2, spi.getCastForLevel(2).size());
		assertEquals("51", spi.getCastForLevel(2).get(0));
		assertEquals("50", spi.getCastForLevel(2).get(1));
		assertEquals(2, spi.getCastForLevel(3).size());
		assertEquals("51", spi.getCastForLevel(3).get(0));
		assertEquals("50", spi.getCastForLevel(3).get(1));
		// Highest Cast Spell Level
		assertEquals(3, spi.getHighestCastSpellLevel());
	}

	public void testSetCastErrors() {
		List<String> l = new ArrayList<String>();
		try {
			spi.setCast(1, null);
			fail("Set Cast took null list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setCast(1, l);
			fail("Set Cast took empty list");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add("60");
		try {
			spi.setCast(0, l);
			fail("Set Cast took level zero");
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setCast(-1, l);
			fail("Set Cast took level negative level");
		} catch (IllegalArgumentException e) {
			// OK
		}
		l.add(null);
		try {
			spi.setCast(0, l);
			fail("Set Cast took list containing null");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testGetMinLevelForSpellLevel() {
		// Works for known
		List<String> l = new ArrayList<String>();
		l.add("51");
		l.add("50");
		spi.setKnown(2, l);
		l.clear();
		l.add("42");
		l.add("41");
		l.add("40");
		spi.setKnown(3, l);
		l.clear();
		l.add("33");
		l.add("32");
		l.add("31");
		l.add("0");
		spi.setKnown(4, l);
		l.clear();
		l.add("23");
		l.add("22");
		l.add("21");
		l.add("20");
		spi.setKnown(5, l);
		assertEquals(2, spi.getMinLevelForSpellLevel(0, false));
		assertEquals(3, spi.getMinLevelForSpellLevel(2, false));
		assertEquals(5, spi.getMinLevelForSpellLevel(3, false));
		assertEquals(-1, spi.getMinLevelForSpellLevel(4, false));
		assertEquals(2, spi.getMinLevelForSpellLevel(0, true));
		assertEquals(3, spi.getMinLevelForSpellLevel(2, true));
		assertEquals(4, spi.getMinLevelForSpellLevel(3, true));
		assertEquals(-1, spi.getMinLevelForSpellLevel(4, true));

		// Works for cast too
		SpellProgressionInfo spi2 = new SpellProgressionInfo();
		l.clear();
		l.add("51");
		l.add("50");
		spi2.setCast(2, l);
		l.clear();
		l.add("42");
		l.add("41");
		l.add("40");
		spi2.setCast(3, l);
		l.clear();
		l.add("33");
		l.add("32");
		l.add("31");
		l.add("0");
		spi2.setCast(4, l);
		l.clear();
		l.add("23");
		l.add("22");
		l.add("21");
		l.add("20");
		spi2.setCast(5, l);
		assertEquals(2, spi2.getMinLevelForSpellLevel(0, false));
		assertEquals(3, spi2.getMinLevelForSpellLevel(2, false));
		assertEquals(5, spi2.getMinLevelForSpellLevel(3, false));
		assertEquals(-1, spi2.getMinLevelForSpellLevel(4, false));
		assertEquals(2, spi2.getMinLevelForSpellLevel(0, true));
		assertEquals(3, spi2.getMinLevelForSpellLevel(2, true));
		assertEquals(4, spi2.getMinLevelForSpellLevel(3, true));
		assertEquals(-1, spi2.getMinLevelForSpellLevel(4, true));
	}

	public void testMaxSpellLevelForClassLevel() {
		// Works for known
		List<String> l = new ArrayList<String>();
		l.add("51");
		l.add("50");
		spi.setKnown(2, l);
		l.clear();
		l.add("42");
		l.add("41");
		l.add("40");
		spi.setKnown(3, l);
		l.clear();
		l.add("33");
		l.add("32");
		l.add("31");
		l.add("0");
		spi.setKnown(4, l);
		l.clear();
		l.add("23");
		l.add("22");
		l.add("21");
		l.add("20");
		l.add("19");
		spi.setKnown(5, l);
		assertEquals(-1, spi.getMaxSpellLevelForClassLevel(1));
		assertEquals(1, spi.getMaxSpellLevelForClassLevel(2));
		assertEquals(2, spi.getMaxSpellLevelForClassLevel(3));
		assertEquals(3, spi.getMaxSpellLevelForClassLevel(4));
		assertEquals(4, spi.getMaxSpellLevelForClassLevel(5));
		assertEquals(4, spi.getMaxSpellLevelForClassLevel(6));

		// Works for cast too
		SpellProgressionInfo spi2 = new SpellProgressionInfo();
		l.clear();
		l.add("51");
		l.add("50");
		spi2.setCast(2, l);
		l.clear();
		l.add("42");
		l.add("41");
		l.add("40");
		spi2.setCast(3, l);
		l.clear();
		l.add("33");
		l.add("32");
		l.add("31");
		l.add("0");
		spi2.setCast(4, l);
		l.clear();
		l.add("23");
		l.add("22");
		l.add("21");
		l.add("20");
		l.add("19");
		spi2.setCast(5, l);
		assertEquals(-1, spi2.getMaxSpellLevelForClassLevel(1));
		assertEquals(1, spi2.getMaxSpellLevelForClassLevel(2));
		assertEquals(2, spi2.getMaxSpellLevelForClassLevel(3));
		assertEquals(3, spi2.getMaxSpellLevelForClassLevel(4));
		assertEquals(4, spi2.getMaxSpellLevelForClassLevel(5));
		assertEquals(4, spi2.getMaxSpellLevelForClassLevel(6));
	}

	public void testAvoidCrossPollution() {
		assertFalse(spi.hasKnownProgression());
		assertFalse(spi.hasCastProgression());
		assertFalse(spi.hasSpecialtyKnownProgression());

		spi.setKnown(1, Arrays.asList("22,21".split(",")));
		assertTrue(spi.hasKnownProgression());
		assertFalse(spi.hasCastProgression());
		assertFalse(spi.hasSpecialtyKnownProgression());

		SpellProgressionInfo spi2 = new SpellProgressionInfo();
		spi2.setSpecialtyKnown(3, Arrays.asList("42,41".split(",")));
		assertFalse(spi2.hasKnownProgression());
		assertFalse(spi2.hasCastProgression());
		assertTrue(spi2.hasSpecialtyKnownProgression());

		SpellProgressionInfo spi3 = new SpellProgressionInfo();
		spi3.setCast(1, Arrays.asList("1,0".split(",")));
		assertFalse(spi3.hasKnownProgression());
		assertTrue(spi3.hasCastProgression());
		assertFalse(spi3.hasSpecialtyKnownProgression());
	}

	public void testClone() {
		spi.setKnown(1, Arrays.asList("22,21".split(",")));
		spi.setSpecialtyKnown(3, Arrays.asList("42,41".split(",")));
		spi.setCast(1, Arrays.asList("1,0".split(",")));
		try {
			SpellProgressionInfo spi2 = spi.clone();
			// Ensure deep enough copy
			spi.setKnown(1, Arrays.asList("77,78".split(",")));
			assertEquals(2, spi.getKnownForLevel(1).size());
			assertEquals("77", spi.getKnownForLevel(1).get(0));
			assertEquals("78", spi.getKnownForLevel(1).get(1));
			assertEquals(2, spi2.getKnownForLevel(1).size());
			assertEquals("22", spi2.getKnownForLevel(1).get(0));
			assertEquals("21", spi2.getKnownForLevel(1).get(1));
			spi.setSpecialtyKnown(3, Arrays.asList("74,75,76".split(",")));
			assertEquals(3, spi.getSpecialtyKnownForLevel(3).size());
			assertEquals("74", spi.getSpecialtyKnownForLevel(3).get(0));
			assertEquals("75", spi.getSpecialtyKnownForLevel(3).get(1));
			assertEquals("76", spi.getSpecialtyKnownForLevel(3).get(2));
			assertEquals(2, spi2.getSpecialtyKnownForLevel(3).size());
			assertEquals("42", spi2.getSpecialtyKnownForLevel(3).get(0));
			assertEquals("41", spi2.getSpecialtyKnownForLevel(3).get(1));
			spi.setCast(1, Arrays.asList("71".split(",")));
			assertEquals(1, spi.getCastForLevel(1).size());
			assertEquals("71", spi.getCastForLevel(1).get(0));
			assertEquals(2, spi2.getCastForLevel(1).size());
			assertEquals("1", spi2.getCastForLevel(1).get(0));
			assertEquals("0", spi2.getCastForLevel(1).get(1));
		} catch (CloneNotSupportedException e) {
			fail(e.getLocalizedMessage());
		}
	}

	public void testSpellType() {
		try {
			spi.setSpellType(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setSpellType("");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setSpellType(" ");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		assertEquals(Constants.s_NONE, spi.getSpellType());
		spi.setSpellType("DIVINE");
		assertEquals("DIVINE", spi.getSpellType());
	}

	public void testBooleanFields() {
		// Defaults to false
		assertFalse(spi.containsSpellFormula());
		spi.setContainsSpellFormula(true);
		assertTrue(spi.containsSpellFormula());
		// Defaults to true
		assertTrue(spi.memorizesSpells());
		spi.setMemorizeSpells(false);
		assertFalse(spi.memorizesSpells());
		// Defaults to false
		assertFalse(spi.usesSpellBook());
		spi.setSpellBookUsed(true);
		assertTrue(spi.usesSpellBook());
	}

	public void testBaseStats() {
		try {
			spi.setSpellBaseStatAbbr("");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setSpellBaseStatAbbr(" ");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		assertEquals(Constants.s_NONE, spi.getSpellBaseStatAbbr());
		spi.setSpellBaseStatAbbr("INT");
		assertEquals("INT", spi.getSpellBaseStatAbbr());

		// Bonus
		try {
			spi.setBonusSpellBaseStatAbbr(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setBonusSpellBaseStatAbbr("");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			spi.setBonusSpellBaseStatAbbr(" ");
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		assertEquals(Constants.s_DEFAULT, spi.getBonusSpellBaseStatAbbr());
		spi.setBonusSpellBaseStatAbbr("DEX");
		assertEquals("DEX", spi.getBonusSpellBaseStatAbbr());
	}

	public void testKnownSpellsFromSpecialty() {
		try {
			spi.setKnownSpellsFromSpecialty(-1);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		// Defaults to zero
		assertEquals(0, spi.getKnownSpellsFromSpecialty());
		spi.setKnownSpellsFromSpecialty(2);
		assertEquals(2, spi.getKnownSpellsFromSpecialty());
	}
}
