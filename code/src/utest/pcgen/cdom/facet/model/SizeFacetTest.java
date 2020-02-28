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
package pcgen.cdom.facet.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SizeFacetTest
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
	private SizeFacet facet;
	private final RaceFacet rfacet = new RaceFacet();
	private final TemplateFacet tfacet = new TemplateFacet();
	private int fakeLevels = 0;
	private Map<CharID, Double> bonusInfo;
	private static SizeAdjustment t, s, m, l, h;

	private static LoadContext context;

	@BeforeEach
	void setUp() throws Exception
	{
		DataSetID cid = context.getDataSetID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet = getMockFacet();
		facet.setRaceFacet(rfacet);
		facet.setTemplateFacet(tfacet);
		facet.setFormulaResolvingFacet(new FormulaResolvingFacet());
		bonusInfo = new HashMap<>();
	}

	@BeforeAll
	static void staticSetUp()
	{
		SettingsHandler.getGameAsProperty().get().clearLoadContext();
		context = Globals.getContext();
		AbstractReferenceContext ref = context.getReferenceContext();
		FacetLibrary.getFacet(LoadContextFacet.class).set(context.getDataSetID(),
			new WeakReference<>(context));
		t = BuildUtilities.createSize("Tiny", 0);
		ref.importObject(t);
		s = BuildUtilities.createSize("Small", 1);
		ref.importObject(s);
		m = BuildUtilities.createSize("Medium", 2);
		m.put(ObjectKey.IS_DEFAULT_SIZE, true);
		ref.importObject(m);
		l = BuildUtilities.createSize("Large", 3);
		ref.importObject(l);
		h = BuildUtilities.createSize("Huge", 4);
		ref.importObject(h);
	}

	@Test
	public void testReachUnsetDefault()
	{
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(2, facet.racialSizeInt(id));
	}

	@Test
	public void testWithNothingInRaceDefaultsTo2()
	{
		rfacet.set(id, new Race());
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(2, facet.racialSizeInt(id));
		rfacet.remove(id);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(2, facet.racialSizeInt(id));
	}

	@Test
	public void testAvoidPollution()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(2, facet.get(altid).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(2, facet.racialSizeInt(altid));
	}

	@Test
	public void testGetFromRace()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetFromTemplateLowerOverridesDefault()
	{
		rfacet.set(id, new Race());
		PCTemplate t1 = new PCTemplate();
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t1, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetFromTemplateHigherOverridesDefault()
	{
		rfacet.set(id, new Race());
		PCTemplate t1 = new PCTemplate();
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t1, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetFromTemplateLowerOverridesRace()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		rfacet.set(id, r);
		PCTemplate t1 = new PCTemplate();
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t1, this);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetFromTemplateHigherOverridesRace()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		rfacet.set(id, r);
		PCTemplate t1 = new PCTemplate();
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(4));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(4, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t1, this);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetFromTemplateSecondOverrides()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t1, this);
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(4));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(4, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t1, this);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetWithBonus()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		bonusInfo.put(altid, 2.0);
		// No pollution
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		bonusInfo.put(id, 2.0);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(4, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		bonusInfo.clear();
		facet.update(id);
		assertEquals(0, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetWithLevelProgression()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		fakeLevels = 6;
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 5);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 6);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(4, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		facet.update(id);
		assertEquals(0, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
	}

	@Test
	public void testGetObjectWithBonus()
	{
		assertEquals(m, facet.get(id));
		facet.update(id);
		assertEquals(m, facet.get(id));
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(s, facet.get(id));
		bonusInfo.put(altid, 2.0);
		// No pollution
		facet.update(id);
		assertEquals(s, facet.get(id));
		bonusInfo.put(id, 2.0);
		facet.update(id);
		assertEquals(l, facet.get(id));
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(m, facet.get(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(h, facet.get(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(m, facet.get(id));
		bonusInfo.put(id, -2.0);
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		facet.update(id);
		assertEquals(t, facet.get(id));
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(4));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(m, facet.get(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(t, facet.get(id));
		bonusInfo.clear();
		facet.update(id);
		assertEquals(s, facet.get(id));
	}

	@Test
	public void testGetAbbWithBonus()
	{
		assertEquals("M", facet.getSizeAbb(id));
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
		bonusInfo.put(altid, 2.0);
		// No pollution
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
		bonusInfo.put(id, 2.0);
		facet.update(id);
		assertEquals("L", facet.getSizeAbb(id));
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals("H", facet.getSizeAbb(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		bonusInfo.put(id, -2.0);
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		facet.update(id);
		assertEquals("T", facet.getSizeAbb(id));
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(4));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals("T", facet.getSizeAbb(id));
		bonusInfo.clear();
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
	}

	@Test
	public void testGetObjectWithLevelProgression()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(s, facet.get(id));
		fakeLevels = 6;
		facet.update(id);
		assertEquals(s, facet.get(id));
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		facet.update(id);
		assertEquals(s, facet.get(id));
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
		facet.update(id);
		assertEquals(m, facet.get(id));
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 5);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 6);
		facet.update(id);
		assertEquals(l, facet.get(id));
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(m, facet.get(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(h, facet.get(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(m, facet.get(id));
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		facet.update(id);
		assertEquals(t, facet.get(id));
	}

	@Test
	public void testGetAbbWithLevelProgression()
	{
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
		fakeLevels = 6;
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		facet.update(id);
		assertEquals("S", facet.getSizeAbb(id));
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 5);
		r.addToListFor(ListKey.HITDICE_ADVANCEMENT, 6);
		facet.update(id);
		assertEquals("L", facet.getSizeAbb(id));
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(0));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals("H", facet.getSizeAbb(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals("M", facet.getSizeAbb(id));
		r.removeListFor(ListKey.HITDICE_ADVANCEMENT);
		facet.update(id);
		assertEquals("T", facet.getSizeAbb(id));
	}

	@Test
	public void testGetWithNegativeBonus()
	{
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(2, facet.racialSizeInt(id));
		Race r = new Race();
		r.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(3));
		rfacet.set(id, r);
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(3, facet.racialSizeInt(id));
		bonusInfo.put(altid, -2.0);
		// No pollution
		facet.update(id);
		assertEquals(3, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(3, facet.racialSizeInt(id));
		bonusInfo.put(id, -2.0);
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(3, facet.racialSizeInt(id));
		PCTemplate t1 = new PCTemplate();
		t1.setName("PCT");
		t1.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(1));
		tfacet.add(id, t1, this);
		facet.update(id);
		assertEquals(0, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(1, facet.racialSizeInt(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.put(FormulaKey.SIZE, FormulaFactory.getFormulaFor(4));
		tfacet.add(id, t2, this);
		facet.update(id);
		assertEquals(2, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(4, facet.racialSizeInt(id));
		tfacet.remove(id, t2, this);
		facet.update(id);
		assertEquals(0, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(1, facet.racialSizeInt(id));
		bonusInfo.clear();
		facet.update(id);
		assertEquals(1, facet.get(id).get(IntegerKey.SIZEORDER).intValue());
		assertEquals(1, facet.racialSizeInt(id));
	}
	
	/**
	 * Verify the function of the sizesAdvanced method.
	 */
	@Test
	public void testSizesAdvanced()
	{
		Race race = new Race();
		race.setName("Test Race");
		
		// Validate that there are no size changes if no advancement is specified
		assertEquals(0, facet.sizesToAdvance(race, 1), "Size increase where none specified wrong");
		assertEquals(0, facet.sizesToAdvance(race, 2), "Size increase where none specified wrong");
		assertEquals(0, facet.sizesToAdvance(race, 3), "Size increase where none specified wrong");
		assertEquals(0, facet.sizesToAdvance(race, 4), "Size increase where none specified wrong");
		assertEquals(0, facet.sizesToAdvance(race, 5), "Size increase where none specified wrong");

		// Validate that size changes occur when needed and no extra happen if advancement is specified
		race.addToListFor(ListKey.HITDICE_ADVANCEMENT, 2);
		race.addToListFor(ListKey.HITDICE_ADVANCEMENT, 4);
		assertEquals(0, facet.sizesToAdvance(race, 1), "Size increase pre first change wrong");
		assertEquals(0, facet.sizesToAdvance(race, 2), "Size increase pre first change wrong");
		assertEquals(1, facet.sizesToAdvance(race, 3), "Size increase pre last change wrong");
		assertEquals(1, facet.sizesToAdvance(race, 4), "Size increase pre last change wrong");
		assertEquals(1, facet.sizesToAdvance(race, 5), "Size increase post last change wrong");
		assertEquals(1, facet.sizesToAdvance(race, 6), "Size increase post last change wrong");
	}

	SizeFacet getMockFacet() throws NoSuchFieldException, IllegalAccessException
	{
		SizeFacet f = new SizeFacet();
		Field field = SizeFacet.class.getDeclaredField("bonusCheckingFacet");
		field.setAccessible(true);
		BonusCheckingFacet fakeFacet = new BonusCheckingFacet()
		{

			@Override
			public double getBonus(CharID cid, String bonusType,
					String bonusName)
			{
				if ("SIZEMOD".equals(bonusType) && "NUMBER".equals(bonusName))
				{
					Double d = bonusInfo.get(cid);
					return d == null ? 0 : d;
				}
				return 0;
			}

		};
		field.set(f, fakeFacet);
		field = SizeFacet.class.getDeclaredField("levelFacet");
		field.setAccessible(true);
		LevelFacet fakeLevelFacet = new LevelFacet()
		{
			@Override
			public int getMonsterLevelCount(CharID cid)
			{
				return fakeLevels;
			}

		};
		field.set(f, fakeLevelFacet);
		return f;
	}
}
