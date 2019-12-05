package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpellProgressionCacheTest
{

    public SpellProgressionCache spi;

    @BeforeEach
    public void setUp()
    {
        spi = new SpellProgressionCache();
    }

    @AfterEach
    public void tearDown()
    {
        spi = null;
    }

    @Test
    public void testKnown()
    {
        // Test it starts out empty
        assertFalse(spi.hasKnownProgression());
        // Test no NPE triggered even if no KNOWN is loaded
        assertNull(spi.getKnownForLevel(1));
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("60"));
        l.add(FormulaFactory.getFormulaFor("61"));
        l.add(FormulaFactory.getFormulaFor("62"));
        spi.setKnown(2, l);
        assertTrue(spi.hasKnownProgression());
        // Test for loaded values
        // implicitly tests that known doesn't require loading in level order
        assertEquals(3, spi.getKnownForLevel(2).size());
        assertEquals("60", spi.getKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getKnownForLevel(2).get(2).toString());
        l.clear();
        // Ensure still true (ensures that setKnown copied the list)
        assertTrue(spi.hasKnownProgression());
        assertEquals(3, spi.getKnownForLevel(2).size());
        assertEquals("60", spi.getKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getKnownForLevel(2).get(2).toString());
        // No known at levels below the loaded level
        assertNull(spi.getKnownForLevel(1));
        // Levels above loaded level return values of lower levels
        assertEquals(3, spi.getKnownForLevel(3).size());
        assertEquals("60", spi.getKnownForLevel(3).get(0).toString());
        assertEquals("61", spi.getKnownForLevel(3).get(1).toString());
        assertEquals("62", spi.getKnownForLevel(3).get(2).toString());
        // Ensure we are transferred ownership of returned list
        List<Formula> returnList = spi.getKnownForLevel(2);
        returnList.clear();
        assertTrue(spi.hasKnownProgression());
        assertEquals(3, spi.getKnownForLevel(2).size());
        assertEquals("60", spi.getKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getKnownForLevel(2).get(2).toString());
        // Ensure SET is a SET not an ADD
        l.clear();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi.setKnown(2, l);
        assertEquals(2, spi.getKnownForLevel(2).size());
        assertEquals("51", spi.getKnownForLevel(2).get(0).toString());
        assertEquals("50", spi.getKnownForLevel(2).get(1).toString());
        // Some advanced testing (skipped levels)
        l.clear();
        l.add(FormulaFactory.getFormulaFor("43"));
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi.setKnown(4, l);
        assertEquals(4, spi.getKnownForLevel(4).size());
        assertEquals("43", spi.getKnownForLevel(4).get(0).toString());
        assertEquals("42", spi.getKnownForLevel(4).get(1).toString());
        assertEquals("41", spi.getKnownForLevel(4).get(2).toString());
        assertEquals("40", spi.getKnownForLevel(4).get(3).toString());
        assertEquals(4, spi.getKnownForLevel(5).size());
        assertEquals("43", spi.getKnownForLevel(5).get(0).toString());
        assertEquals("42", spi.getKnownForLevel(5).get(1).toString());
        assertEquals("41", spi.getKnownForLevel(5).get(2).toString());
        assertEquals("40", spi.getKnownForLevel(5).get(3).toString());
        assertEquals(2, spi.getKnownForLevel(2).size());
        assertEquals("51", spi.getKnownForLevel(2).get(0).toString());
        assertEquals("50", spi.getKnownForLevel(2).get(1).toString());
        assertEquals(2, spi.getKnownForLevel(3).size());
        assertEquals("51", spi.getKnownForLevel(3).get(0).toString());
        assertEquals("50", spi.getKnownForLevel(3).get(1).toString());
        // Highest Known Spell Level
        assertEquals(3, spi.getHighestKnownSpellLevel());
    }

    @Test
    public void testSetKnownErrors()
    {
        List<Formula> l = new ArrayList<>();
        try
        {
            spi.setKnown(1, null);
            fail("Set Known took null list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setKnown(1, l);
            fail("Set Known took empty list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(FormulaFactory.getFormulaFor("60"));
        try
        {
            spi.setKnown(0, l);
            fail("Set Known took level zero");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setKnown(-1, l);
            fail("Set Known took level negative level");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(null);
        try
        {
            spi.setKnown(0, l);
            fail("Set Known took list containing null");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testSpecialtyKnown()
    {
        // Test it starts out empty
        assertFalse(spi.hasSpecialtyKnownProgression());
        // Test no NPE triggered even if no KNOWN is loaded
        assertNull(spi.getSpecialtyKnownForLevel(1));
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("60"));
        l.add(FormulaFactory.getFormulaFor("61"));
        l.add(FormulaFactory.getFormulaFor("62"));
        spi.setSpecialtyKnown(2, l);
        assertTrue(spi.hasSpecialtyKnownProgression());
        // Test for loaded values
        // implicitly tests that known doesn't require loading in level order
        assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
        assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2).toString());
        l.clear();
        // Ensure still true (ensures that setKnown copied the list)
        assertTrue(spi.hasSpecialtyKnownProgression());
        assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
        assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2).toString());
        // No known at levels below the loaded level
        assertNull(spi.getSpecialtyKnownForLevel(1));
        // Levels above loaded level return values of lower levels
        assertEquals(3, spi.getSpecialtyKnownForLevel(3).size());
        assertEquals("60", spi.getSpecialtyKnownForLevel(3).get(0).toString());
        assertEquals("61", spi.getSpecialtyKnownForLevel(3).get(1).toString());
        assertEquals("62", spi.getSpecialtyKnownForLevel(3).get(2).toString());
        // Ensure we are transferred ownership of returned list
        List<Formula> returnList = spi.getSpecialtyKnownForLevel(2);
        returnList.clear();
        assertTrue(spi.hasSpecialtyKnownProgression());
        assertEquals(3, spi.getSpecialtyKnownForLevel(2).size());
        assertEquals("60", spi.getSpecialtyKnownForLevel(2).get(0).toString());
        assertEquals("61", spi.getSpecialtyKnownForLevel(2).get(1).toString());
        assertEquals("62", spi.getSpecialtyKnownForLevel(2).get(2).toString());
        // Ensure SET is a SET not an ADD
        l.clear();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi.setSpecialtyKnown(2, l);
        assertEquals(2, spi.getSpecialtyKnownForLevel(2).size());
        assertEquals("51", spi.getSpecialtyKnownForLevel(2).get(0).toString());
        assertEquals("50", spi.getSpecialtyKnownForLevel(2).get(1).toString());
        // Some advanced testing (skipped levels)
        l.clear();
        l.add(FormulaFactory.getFormulaFor("43"));
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi.setSpecialtyKnown(4, l);
        assertEquals(4, spi.getSpecialtyKnownForLevel(4).size());
        assertEquals("43", spi.getSpecialtyKnownForLevel(4).get(0).toString());
        assertEquals("42", spi.getSpecialtyKnownForLevel(4).get(1).toString());
        assertEquals("41", spi.getSpecialtyKnownForLevel(4).get(2).toString());
        assertEquals("40", spi.getSpecialtyKnownForLevel(4).get(3).toString());
        assertEquals(4, spi.getSpecialtyKnownForLevel(5).size());
        assertEquals("43", spi.getSpecialtyKnownForLevel(5).get(0).toString());
        assertEquals("42", spi.getSpecialtyKnownForLevel(5).get(1).toString());
        assertEquals("41", spi.getSpecialtyKnownForLevel(5).get(2).toString());
        assertEquals("40", spi.getSpecialtyKnownForLevel(5).get(3).toString());
        assertEquals(2, spi.getSpecialtyKnownForLevel(2).size());
        assertEquals("51", spi.getSpecialtyKnownForLevel(2).get(0).toString());
        assertEquals("50", spi.getSpecialtyKnownForLevel(2).get(1).toString());
        assertEquals(2, spi.getSpecialtyKnownForLevel(3).size());
        assertEquals("51", spi.getSpecialtyKnownForLevel(3).get(0).toString());
        assertEquals("50", spi.getSpecialtyKnownForLevel(3).get(1).toString());
    }

    @Test
    public void testSetSpecialtyKnownErrors()
    {
        List<Formula> l = new ArrayList<>();
        try
        {
            spi.setSpecialtyKnown(1, null);
            fail("Set SpecialtyKnown took null list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setSpecialtyKnown(1, l);
            fail("Set SpecialtyKnown took empty list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(FormulaFactory.getFormulaFor("60"));
        try
        {
            spi.setSpecialtyKnown(0, l);
            fail("Set SpecialtyKnown took level zero");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setSpecialtyKnown(-1, l);
            fail("Set SpecialtyKnown took level negative level");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(null);
        try
        {
            spi.setSpecialtyKnown(0, l);
            fail("Set SpecialtyKnown took list containing null");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testCast()
    {
        // Test it starts out empty
        assertFalse(spi.hasCastProgression());
        // Test no NPE triggered even if no Cast is loaded
        assertNull(spi.getCastForLevel(1));
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("60"));
        l.add(FormulaFactory.getFormulaFor("61"));
        l.add(FormulaFactory.getFormulaFor("62"));
        spi.setCast(2, l);
        assertTrue(spi.hasCastProgression());
        // Test for loaded values
        // implicitly tests that Cast doesn't require loading in level order
        assertEquals(3, spi.getCastForLevel(2).size());
        assertEquals("60", spi.getCastForLevel(2).get(0).toString());
        assertEquals("61", spi.getCastForLevel(2).get(1).toString());
        assertEquals("62", spi.getCastForLevel(2).get(2).toString());
        l.clear();
        // Ensure still true (ensures that setCast copied the list)
        assertTrue(spi.hasCastProgression());
        assertEquals(3, spi.getCastForLevel(2).size());
        assertEquals("60", spi.getCastForLevel(2).get(0).toString());
        assertEquals("61", spi.getCastForLevel(2).get(1).toString());
        assertEquals("62", spi.getCastForLevel(2).get(2).toString());
        // No Cast at levels below the loaded level
        assertNull(spi.getCastForLevel(1));
        // Levels above loaded level return values of lower levels
        assertEquals(3, spi.getCastForLevel(3).size());
        assertEquals("60", spi.getCastForLevel(3).get(0).toString());
        assertEquals("61", spi.getCastForLevel(3).get(1).toString());
        assertEquals("62", spi.getCastForLevel(3).get(2).toString());
        // Ensure we are transferred ownership of returned list
        List<Formula> returnList = spi.getCastForLevel(2);
        returnList.clear();
        assertTrue(spi.hasCastProgression());
        assertEquals(3, spi.getCastForLevel(2).size());
        assertEquals("60", spi.getCastForLevel(2).get(0).toString());
        assertEquals("61", spi.getCastForLevel(2).get(1).toString());
        assertEquals("62", spi.getCastForLevel(2).get(2).toString());
        // Ensure SET is a SET not an ADD
        l.clear();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi.setCast(2, l);
        assertEquals(2, spi.getCastForLevel(2).size());
        assertEquals("51", spi.getCastForLevel(2).get(0).toString());
        assertEquals("50", spi.getCastForLevel(2).get(1).toString());
        // Some advanced testing (skipped levels)
        l.clear();
        l.add(FormulaFactory.getFormulaFor("43"));
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi.setCast(4, l);
        assertEquals(4, spi.getCastForLevel(4).size());
        assertEquals("43", spi.getCastForLevel(4).get(0).toString());
        assertEquals("42", spi.getCastForLevel(4).get(1).toString());
        assertEquals("41", spi.getCastForLevel(4).get(2).toString());
        assertEquals("40", spi.getCastForLevel(4).get(3).toString());
        assertEquals(4, spi.getCastForLevel(5).size());
        assertEquals("43", spi.getCastForLevel(5).get(0).toString());
        assertEquals("42", spi.getCastForLevel(5).get(1).toString());
        assertEquals("41", spi.getCastForLevel(5).get(2).toString());
        assertEquals("40", spi.getCastForLevel(5).get(3).toString());
        assertEquals(2, spi.getCastForLevel(2).size());
        assertEquals("51", spi.getCastForLevel(2).get(0).toString());
        assertEquals("50", spi.getCastForLevel(2).get(1).toString());
        assertEquals(2, spi.getCastForLevel(3).size());
        assertEquals("51", spi.getCastForLevel(3).get(0).toString());
        assertEquals("50", spi.getCastForLevel(3).get(1).toString());
        // Highest Cast Spell Level
        assertEquals(3, spi.getHighestCastSpellLevel());
    }

    @Test
    public void testSetCastErrors()
    {
        List<Formula> l = new ArrayList<>();
        try
        {
            spi.setCast(1, null);
            fail("Set Cast took null list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setCast(1, l);
            fail("Set Cast took empty list");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(FormulaFactory.getFormulaFor("60"));
        try
        {
            spi.setCast(0, l);
            fail("Set Cast took level zero");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        try
        {
            spi.setCast(-1, l);
            fail("Set Cast took level negative level");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        l.add(null);
        try
        {
            spi.setCast(0, l);
            fail("Set Cast took list containing null");
        } catch (IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testGetMinLevelForSpellLevel()
    {
        // Works for known
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi.setKnown(2, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi.setKnown(3, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("33"));
        l.add(FormulaFactory.getFormulaFor("32"));
        l.add(FormulaFactory.getFormulaFor("31"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi.setKnown(4, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("23"));
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        l.add(FormulaFactory.getFormulaFor("20"));
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
        SpellProgressionCache spi2 = new SpellProgressionCache();
        l.clear();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi2.setCast(2, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi2.setCast(3, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("33"));
        l.add(FormulaFactory.getFormulaFor("32"));
        l.add(FormulaFactory.getFormulaFor("31"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi2.setCast(4, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("23"));
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        l.add(FormulaFactory.getFormulaFor("20"));
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

    @Test
    public void testMaxSpellLevelForClassLevel()
    {
        // Works for known
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi.setKnown(2, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi.setKnown(3, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("33"));
        l.add(FormulaFactory.getFormulaFor("32"));
        l.add(FormulaFactory.getFormulaFor("31"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi.setKnown(4, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("23"));
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        l.add(FormulaFactory.getFormulaFor("20"));
        l.add(FormulaFactory.getFormulaFor("19"));
        spi.setKnown(5, l);
        assertEquals(-1, spi.getMaxSpellLevelForClassLevel(1));
        assertEquals(1, spi.getMaxSpellLevelForClassLevel(2));
        assertEquals(2, spi.getMaxSpellLevelForClassLevel(3));
        assertEquals(3, spi.getMaxSpellLevelForClassLevel(4));
        assertEquals(4, spi.getMaxSpellLevelForClassLevel(5));
        assertEquals(4, spi.getMaxSpellLevelForClassLevel(6));

        // Works for cast too
        SpellProgressionCache spi2 = new SpellProgressionCache();
        l.clear();
        l.add(FormulaFactory.getFormulaFor("51"));
        l.add(FormulaFactory.getFormulaFor("50"));
        spi2.setCast(2, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        l.add(FormulaFactory.getFormulaFor("40"));
        spi2.setCast(3, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("33"));
        l.add(FormulaFactory.getFormulaFor("32"));
        l.add(FormulaFactory.getFormulaFor("31"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi2.setCast(4, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("23"));
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        l.add(FormulaFactory.getFormulaFor("20"));
        l.add(FormulaFactory.getFormulaFor("19"));
        spi2.setCast(5, l);
        assertEquals(-1, spi2.getMaxSpellLevelForClassLevel(1));
        assertEquals(1, spi2.getMaxSpellLevelForClassLevel(2));
        assertEquals(2, spi2.getMaxSpellLevelForClassLevel(3));
        assertEquals(3, spi2.getMaxSpellLevelForClassLevel(4));
        assertEquals(4, spi2.getMaxSpellLevelForClassLevel(5));
        assertEquals(4, spi2.getMaxSpellLevelForClassLevel(6));
    }

    @Test
    public void testAvoidCrossPollution()
    {
        assertFalse(spi.hasKnownProgression());
        assertFalse(spi.hasCastProgression());
        assertFalse(spi.hasSpecialtyKnownProgression());

        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        spi.setKnown(1, l);
        assertTrue(spi.hasKnownProgression());
        assertFalse(spi.hasCastProgression());
        assertFalse(spi.hasSpecialtyKnownProgression());

        SpellProgressionCache spi2 = new SpellProgressionCache();
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        spi2.setSpecialtyKnown(3, l);
        assertFalse(spi2.hasKnownProgression());
        assertFalse(spi2.hasCastProgression());
        assertTrue(spi2.hasSpecialtyKnownProgression());

        SpellProgressionCache spi3 = new SpellProgressionCache();
        l.clear();
        l.add(FormulaFactory.getFormulaFor("1"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi3.setCast(1, l);
        assertFalse(spi3.hasKnownProgression());
        assertTrue(spi3.hasCastProgression());
        assertFalse(spi3.hasSpecialtyKnownProgression());
    }

    @Test
    public void testClone()
    {
        List<Formula> l = new ArrayList<>();
        l.add(FormulaFactory.getFormulaFor("22"));
        l.add(FormulaFactory.getFormulaFor("21"));
        spi.setKnown(1, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("42"));
        l.add(FormulaFactory.getFormulaFor("41"));
        spi.setSpecialtyKnown(3, l);
        l.clear();
        l.add(FormulaFactory.getFormulaFor("1"));
        l.add(FormulaFactory.getFormulaFor("0"));
        spi.setCast(1, l);
        try
        {
            SpellProgressionCache spi2 = spi.clone();
            // Ensure deep enough copy
            l.clear();
            l.add(FormulaFactory.getFormulaFor("77"));
            l.add(FormulaFactory.getFormulaFor("78"));
            spi.setKnown(1, l);
            assertEquals(2, spi.getKnownForLevel(1).size());
            assertEquals("77", spi.getKnownForLevel(1).get(0).toString());
            assertEquals("78", spi.getKnownForLevel(1).get(1).toString());
            assertEquals(2, spi2.getKnownForLevel(1).size());
            assertEquals("22", spi2.getKnownForLevel(1).get(0).toString());
            assertEquals("21", spi2.getKnownForLevel(1).get(1).toString());
            l.clear();
            l.add(FormulaFactory.getFormulaFor("74"));
            l.add(FormulaFactory.getFormulaFor("75"));
            l.add(FormulaFactory.getFormulaFor("76"));
            spi.setSpecialtyKnown(3, l);
            assertEquals(3, spi.getSpecialtyKnownForLevel(3).size());
            assertEquals("74", spi.getSpecialtyKnownForLevel(3).get(0).toString());
            assertEquals("75", spi.getSpecialtyKnownForLevel(3).get(1).toString());
            assertEquals("76", spi.getSpecialtyKnownForLevel(3).get(2).toString());
            assertEquals(2, spi2.getSpecialtyKnownForLevel(3).size());
            assertEquals("42", spi2.getSpecialtyKnownForLevel(3).get(0).toString());
            assertEquals("41", spi2.getSpecialtyKnownForLevel(3).get(1).toString());
            l.clear();
            l.add(FormulaFactory.getFormulaFor("71"));
            spi.setCast(1, l);
            assertEquals(1, spi.getCastForLevel(1).size());
            assertEquals("71", spi.getCastForLevel(1).get(0).toString());
            assertEquals(2, spi2.getCastForLevel(1).size());
            assertEquals("1", spi2.getCastForLevel(1).get(0).toString());
            assertEquals("0", spi2.getCastForLevel(1).get(1).toString());
        } catch (CloneNotSupportedException e)
        {
            fail(e.getLocalizedMessage());
        }
    }
}
