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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class StatIntegrationTest extends TestCase
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
	private CDOMObjectConsolidationFacet cdomFacet;
	private PCStat stat1;
	private PCStat stat2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		id = new CharID();
		altid = new CharID();
		stat1 = new PCStat();
		stat2 = new PCStat();
		stat1.put(StringKey.ABB, "Stat1");
		stat1.setName("Stat1");
		stat2.put(StringKey.ABB, "Stat2");
		stat2.setName("Stat2");
		unlockedFacet = new UnlockedStatFacet();
		lockFacet = new StatLockFacet();
		lockFacet.setFormulaResolvingFacet(new FormulaResolvingFacet());
		nonAbilityFacet = new NonAbilityFacet();
		nonAbilityFacet.setStatLockFacet(lockFacet);
		nonAbilityFacet.setUnlockedStatFacet(unlockedFacet);
		rfacet = new RaceFacet();
		tfacet = new TemplateFacet();
		cdomFacet = new CDOMObjectConsolidationFacet();
		CDOMObjectBridge bridge = new CDOMObjectBridge();
		cdomFacet.setBridgeFacet(bridge);
		rfacet.addDataFacetChangeListener(cdomFacet);
		tfacet.addDataFacetChangeListener(cdomFacet);
		cdomFacet.addDataFacetChangeListener(lockFacet);
		cdomFacet.addDataFacetChangeListener(unlockedFacet);
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
		causeUnlock(r1, stat2);
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
		Race r1 = new Race();
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
		tfacet.add(id, t1);
		assertFalse(nonAbilityFacet.isNonAbility(id, stat2));
		assertTrue(nonAbilityFacet.isNonAbility(id, stat1));
		assertFalse(nonAbilityFacet.isNonAbility(altid, stat1));
		// Make sure cleans up when template removed
		tfacet.remove(id, t1);
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
		tfacet.add(id, t1);
		testNonAbilityUnset();
		assertEquals(15, lockFacet.getLockedStat(id, stat1));
		assertNull(lockFacet.getLockedStat(id, stat2));
		// Make sure cleans up when template removed
		tfacet.remove(id, t1);
		testNonAbilityUnset();
		assertEquals(14, lockFacet.getLockedStat(id, stat1));
		assertNull(lockFacet.getLockedStat(id, stat2));
	}

	@Test
	public void testUnlockOverrideNonAbilityLockComplex()
	{
		testNonAbilityUnset();
		Race r = new Race();
		causeLockNonAbility(r, stat1);
		rfacet.set(id, r);
		PCTemplate t1 = new PCTemplate();
		causeUnlock(t1, stat1);
		tfacet.add(id, t1);
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
		tfacet.add(id, t1);
		testNonAbilityUnset();
		testLockUnsetConditional();
	}

	private void causeLockNonAbility(CDOMObject r, PCStat stat)
	{
		StatLock sl = new StatLock(stat, FormulaFactory.getFormulaFor(10));
		r.addToListFor(ListKey.STAT_LOCKS, sl);
	}

	private void causeLock(CDOMObject r, PCStat stat, int i)
	{
		StatLock sl = new StatLock(stat, FormulaFactory.getFormulaFor(i));
		r.addToListFor(ListKey.STAT_LOCKS, sl);
	}

	private void causeUnlock(CDOMObject r, PCStat stat)
	{
		r.addToListFor(ListKey.UNLOCKED_STATS, stat);
	}
}
