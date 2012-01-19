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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;

public class RaceTypeFacetTest extends TestCase
{
	private static final RaceType LAST_RACE_TYPE = RaceType
			.getConstant("TestLastRaceType");
	private static final RaceType RACE_TYPE_TOO = RaceType
			.getConstant("TestRaceTypeToo");
	private static final RaceType TEST_RACE_TYPE = RaceType
			.getConstant("TestRaceType");
	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * RaceFacet and TemplateFacet frameworks. This class trusts that
	 * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
	 * TemplateFacet. PLEASE ensure all tests there are working before
	 * investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private RaceTypeFacet facet;
	private RaceFacet rfacet = new RaceFacet();
	private TemplateFacet tfacet = new TemplateFacet();
	private CompanionModFacet cfacet = new CompanionModFacet();

	@Override
	public void setUp() throws Exception
	{
		facet = new RaceTypeFacet();
		super.setUp();
		facet.setRaceFacet(rfacet);
		facet.setTemplateFacet(tfacet);
		facet.setCompanionModFacet(cfacet);
		id = new CharID();
		altid = new CharID();
	}

	@Test
	public void testRaceTypeUnsetNull()
	{
		assertNull(facet.getRaceType(id));
	}

	@Test
	public void testWithNothingInRace()
	{
		rfacet.set(id, new Race());
		assertNull(facet.getRaceType(id));
	}

	@Test
	public void testAvoidPollution()
	{
		Race r = new Race();
		r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertNull(facet.getRaceType(altid));
	}

	@Test
	public void testGetFromRace()
	{
		Race r = new Race();
		r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		rfacet.remove(id);
		assertNull(facet.getRaceType(id));
	}

	@Test
	public void testGetFromCMod()
	{
		rfacet.set(id, new Race());
		CompanionMod c = new CompanionMod();
		c.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		cfacet.add(id, c);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		cfacet.remove(id, c);
		assertNull(facet.getRaceType(id));
	}

	@Test
	public void testGetFromTemplate()
	{
		rfacet.set(id, new Race());
		PCTemplate t = new PCTemplate();
		t.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		tfacet.add(id, t);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		tfacet.remove(id, t);
		assertNull(facet.getRaceType(id));
	}

	@Test
	public void testGetFromCModOverridesRace()
	{
		Race r = new Race();
		r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		CompanionMod c = new CompanionMod();
		c.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
		cfacet.add(id, c);
		assertEquals(RACE_TYPE_TOO, facet.getRaceType(id));
		cfacet.remove(id, c);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
	}

	@Test
	public void testGetFromTemplateOverridesRaceandCMod()
	{
		Race r = new Race();
		r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		CompanionMod c = new CompanionMod();
		c.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
		cfacet.add(id, c);
		assertEquals(RACE_TYPE_TOO, facet.getRaceType(id));
		PCTemplate t = new PCTemplate();
		t.put(ObjectKey.RACETYPE, LAST_RACE_TYPE);
		tfacet.add(id, t);
		assertEquals(LAST_RACE_TYPE, facet.getRaceType(id));
		tfacet.remove(id, t);
		assertEquals(RACE_TYPE_TOO, facet.getRaceType(id));
		cfacet.remove(id, c);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
	}

	@Test
	public void testGetFromTemplateSecondOverrides()
	{
		Race r = new Race();
		r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
		PCTemplate t = new PCTemplate();
		t.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
		tfacet.add(id, t);
		assertEquals(RACE_TYPE_TOO, facet.getRaceType(id));
		PCTemplate t2 = new PCTemplate();
		t2.put(ObjectKey.RACETYPE, LAST_RACE_TYPE);
		tfacet.add(id, t2);
		assertEquals(LAST_RACE_TYPE, facet.getRaceType(id));
		tfacet.remove(id, t);
		assertEquals(LAST_RACE_TYPE, facet.getRaceType(id));
		tfacet.add(id, t);
		assertEquals(RACE_TYPE_TOO, facet.getRaceType(id));
		tfacet.remove(id, t);
		assertEquals(LAST_RACE_TYPE, facet.getRaceType(id));
		tfacet.remove(id, t2);
		assertEquals(TEST_RACE_TYPE, facet.getRaceType(id));
	}
}
