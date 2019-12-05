/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.analysis.NonAbilityFacet;
import pcgen.cdom.facet.analysis.NonStatStatFacet;
import pcgen.cdom.facet.analysis.NonStatToStatFacet;
import pcgen.cdom.facet.analysis.StatLockFacet;
import pcgen.cdom.facet.analysis.UnlockedStatFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatIntegrationTest
{
    /*
     * NOTE: This is not literal unit testing - it is leveraging the existing
     * RaceFacet and TemplateFacet frameworks. This class trusts that
     * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
     * TemplateFacet. PLEASE ensure all tests there are working before
     * investigating tests here.
     */
    private CharID id;
    private CharID altid;
    private UnlockedStatFacet unlockedFacet;
    private StatLockFacet lockFacet;
    private NonAbilityFacet nonAbilityFacet;
    private RaceFacet rfacet;
    private TemplateFacet tfacet;
    private PCStat stat1;
    private PCStat stat2;
    private final Object tsource = new Object();

    @BeforeEach
    public void setUp()
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
        stat1 = BuildUtilities.createStat("Stat1", "Stat1");
        stat2 = BuildUtilities.createStat("Stat2", "Stat2");
        unlockedFacet = new UnlockedStatFacet();
        lockFacet = new StatLockFacet();
        lockFacet.setFormulaResolvingFacet(new FormulaResolvingFacet());
        nonAbilityFacet = new NonAbilityFacet();
        NonStatStatFacet nonStatStatFacet = new NonStatStatFacet();
        nonAbilityFacet.setNonStatStatFacet(nonStatStatFacet);
        NonStatToStatFacet nonStatToStatFacet = new NonStatToStatFacet();
        nonAbilityFacet.setNonStatToStatFacet(nonStatToStatFacet);
        rfacet = new RaceFacet();
        tfacet = new TemplateFacet();
        CDOMObjectConsolidationFacet cdomFacet = new CDOMObjectConsolidationFacet();
        CDOMObjectBridge bridge = new CDOMObjectBridge();
        cdomFacet.setBridgeFacet(bridge);
        rfacet.addDataFacetChangeListener(cdomFacet);
        tfacet.addDataFacetChangeListener(cdomFacet);
        cdomFacet.addDataFacetChangeListener(lockFacet);
        cdomFacet.addDataFacetChangeListener(unlockedFacet);
        cdomFacet.addDataFacetChangeListener(nonStatStatFacet);
        cdomFacet.addDataFacetChangeListener(nonStatToStatFacet);
    }

    @Test
    public void testNonAbilityUnset()
    {
        assertFalse(nonAbilityFacet.isNonAbility(id, stat1));
        assertFalse(nonAbilityFacet.isNonAbility(id, stat2));
        assertFalse(nonAbilityFacet.isNonAbility(altid, stat1));
        assertFalse(nonAbilityFacet.isNonAbility(altid, stat2));
    }

    @Test
    public void testLockUnset()
    {
        assertNull(lockFacet.getLockedStat(id, stat1));
        assertNull(lockFacet.getLockedStat(id, stat2));
    }

    /*
     * TODO Would be nice to get rid of this conditional - should be easier once
     * StatFacet actually contains the stat values.
     */
    @Test
    public void testLockUnsetConditional()
    {
        if (!unlockedFacet.contains(id, stat1))
        {
            assertNull(lockFacet.getLockedStat(id, stat1));
        }
        if (!unlockedFacet.contains(id, stat2))
        {
            assertNull(lockFacet.getLockedStat(id, stat2));
        }
    }

    @Test
    public void testWithNothingInRace()
    {
        Race r = new Race();
        rfacet.set(id, r);
        testNonAbilityUnset();
        testLockUnset();
    }

    @Test
    public void testLockNonAbilityInRace()
    {
        Race r = new Race();
        causeLockNonAbility(r, stat1);
        rfacet.set(id, r);
        assertFalse(nonAbilityFacet.isNonAbility(id, stat2));
        assertTrue(nonAbilityFacet.isNonAbility(id, stat1));
        assertFalse(nonAbilityFacet.isNonAbility(altid, stat1));
        // Make sure cleans up when race changed
        rfacet.set(id, new Race());
        testNonAbilityUnset();
        testLockUnset();
    }

    @Test
    public void testLockInRace()
    {
        Race r = new Race();
        causeLock(r, stat1, 14);
        rfacet.set(id, r);
        testNonAbilityUnset();
        assertEquals(14, lockFacet.getLockedStat(id, stat1));
        assertNull(lockFacet.getLockedStat(id, stat2));
        // Make sure cleans up when race changed
        rfacet.set(id, new Race());
        testNonAbilityUnset();
        testLockUnset();
    }

    @Test
    public void testUnlockOverrideNonAbilityLockSimple()
    {
        Race r1 = new Race();
        causeLockNonAbility(r1, stat2);
        causeUnLockNonAbility(r1, stat2);
        rfacet.set(id, r1);
        testNonAbilityUnset();
        testLockUnsetConditional();
    }

    @Test
    public void testUnlockOverrideLockSimple()
    {
        Race r1 = new Race();
        causeLock(r1, stat2, 14);
        causeUnlock(r1, stat2);
        rfacet.set(id, r1);
        testNonAbilityUnset();
        testLockUnsetConditional();
    }

    @Test
    public void testUnlockInnocent()
    {
        CDOMObject r1 = new Race();
        causeUnlock(r1, stat2);
        testNonAbilityUnset();
        testLockUnset();
    }

    @Test
    public void testLockNonAbilityInTemplate()
    {
        Race r = new Race();
        rfacet.set(id, r);
        testNonAbilityUnset();
        PCTemplate t1 = new PCTemplate();
        causeLockNonAbility(t1, stat1);
        tfacet.add(id, t1, tsource);
        assertFalse(nonAbilityFacet.isNonAbility(id, stat2));
        assertTrue(nonAbilityFacet.isNonAbility(id, stat1));
        assertFalse(nonAbilityFacet.isNonAbility(altid, stat1));
        // Make sure cleans up when template removed
        tfacet.remove(id, t1, tsource);
        testNonAbilityUnset();
        testLockUnset();
    }

    @Test
    public void testLockComplex()
    {
        Race r = new Race();
        causeLock(r, stat1, 14);
        rfacet.set(id, r);
        testNonAbilityUnset();
        assertEquals(14, lockFacet.getLockedStat(id, stat1));
        assertNull(lockFacet.getLockedStat(id, stat2));
        PCTemplate t1 = new PCTemplate();
        causeLock(t1, stat1, 15);
        tfacet.add(id, t1, tsource);
        testNonAbilityUnset();
        assertEquals(15, lockFacet.getLockedStat(id, stat1));
        assertNull(lockFacet.getLockedStat(id, stat2));
        // Make sure cleans up when template removed
        tfacet.remove(id, t1, tsource);
        testNonAbilityUnset();
        assertEquals(14, lockFacet.getLockedStat(id, stat1));
        assertNull(lockFacet.getLockedStat(id, stat2));
    }

    @Test
    public void testNonStatToStatOverrideNonAbilityLockComplex()
    {
        testNonAbilityUnset();
        Race r = new Race();
        causeLockNonAbility(r, stat1);
        rfacet.set(id, r);
        PCTemplate t1 = new PCTemplate();
        causeUnLockNonAbility(t1, stat1);
        tfacet.add(id, t1, tsource);
        testNonAbilityUnset();
        testLockUnsetConditional();
    }

    @Test
    public void testUnlockOverrideLockComplex()
    {
        testNonAbilityUnset();
        Race r = new Race();
        causeLock(r, stat1, 13);
        rfacet.set(id, r);
        PCTemplate t1 = new PCTemplate();
        causeUnlock(t1, stat1);
        tfacet.add(id, t1, tsource);
        testNonAbilityUnset();
        testLockUnsetConditional();
    }

    @Test
    public void testUnlockNotOverrideNonAbilityLockComplex()
    {
        testNonAbilityUnset();
        Race r = new Race();
        causeLockNonAbility(r, stat1);
        rfacet.set(id, r);
        assertTrue(nonAbilityFacet.isNonAbility(id, stat1));
        PCTemplate t1 = new PCTemplate();
        causeUnlock(t1, stat1);
        tfacet.add(id, t1, tsource);
        assertTrue(nonAbilityFacet.isNonAbility(id, stat1));
    }

    private static void causeLockNonAbility(CDOMObject r, PCStat stat)
    {
        r.addToListFor(ListKey.NONSTAT_STATS, CDOMDirectSingleRef.getRef(stat));
    }

    private static void causeUnLockNonAbility(CDOMObject r, PCStat stat)
    {
        r.addToListFor(ListKey.NONSTAT_TO_STAT_STATS, CDOMDirectSingleRef.getRef(stat));
    }

    private static void causeLock(CDOMObject r, PCStat stat, int i)
    {
        StatLock sl = new StatLock(CDOMDirectSingleRef.getRef(stat), FormulaFactory.getFormulaFor(i));
        r.addToListFor(ListKey.STAT_LOCKS, sl);
    }

    private static void causeUnlock(CDOMObject r, PCStat stat)
    {
        r.addToListFor(ListKey.UNLOCKED_STATS, CDOMDirectSingleRef.getRef(stat));
    }
}
