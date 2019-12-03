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
package pcgen.cdom.facet.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RacialSubTypesFacetTest
{
	private static final RaceSubType LAST_RACE_TYPE = RaceSubType
			.getConstant("TestLastRACESUBTYPE");
	private static final RaceSubType RACE_TYPE_TOO = RaceSubType
			.getConstant("TestRACESUBTYPEToo");
	private static final RaceSubType TEST_RACE_TYPE = RaceSubType
			.getConstant("TestRACESUBTYPE");
	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * RaceFacet and TemplateFacet frameworks. This class trusts that
	 * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
	 * TemplateFacet. PLEASE ensure all tests there are working before
	 * investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private RacialSubTypesFacet facet;
	private RaceFacet rfacet = new RaceFacet();
	private TemplateFacet tfacet = new TemplateFacet();

	@BeforeEach
	public void setUp() {
		facet = new RacialSubTypesFacet();
		facet.setRaceFacet(rfacet);
		facet.setTemplateFacet(tfacet);
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
	}

	@AfterEach
	public void tearDown()
	{
		id = null;
		altid = null;
		facet = null;
		rfacet = null;
		tfacet = null;
	}

	@Test
	public void testRaceSubTypesUnsetEmpty()
	{
		assertNotNull(facet.getRacialSubTypes(id));
		assertTrue(facet.getRacialSubTypes(id).isEmpty());
	}

	@Test
	public void testWithNothingInRace()
	{
		rfacet.set(id, new Race());
		assertSubTypesEmpty();
	}

	private void assertSubTypesEmpty()
	{
		assertNotNull(facet.getRacialSubTypes(id));
		assertTrue(facet.getRacialSubTypes(id).isEmpty());
	}

	@Test
	public void testAvoidPollution()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertNotNull(facet.getRacialSubTypes(altid));
		assertTrue(facet.getRacialSubTypes(altid).isEmpty());
	}

	@Test
	public void testGetFromRace()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		rfacet.remove(id);
		assertSubTypesEmpty();
	}

	@Test
	public void testGetRemoved()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		PCTemplate t = new PCTemplate();
		t.addToListFor(ListKey.REMOVED_RACESUBTYPE, TEST_RACE_TYPE);
		tfacet.add(id, t, this);
		assertSubTypesEmpty();
		tfacet.remove(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
	}

	@Test
	public void testGetFromTemplate()
	{
		rfacet.set(id, new Race());
		PCTemplate t = new PCTemplate();
		t.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		tfacet.add(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		tfacet.remove(id, t, this);
		assertSubTypesEmpty();
	}

	@Test
	public void testGetFromRaceAndTemplate()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		PCTemplate t = new PCTemplate();
		t.addToListFor(ListKey.RACESUBTYPE, RACE_TYPE_TOO);
		tfacet.add(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO);
		tfacet.remove(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
	}

	@Test
	public void testGetFromTemplateOverridesRaceandCMod()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.addToListFor(ListKey.RACESUBTYPE, RACE_TYPE_TOO);
		tfacet.add(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO);
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.addToListFor(ListKey.RACESUBTYPE, LAST_RACE_TYPE);
		tfacet.add(id, t2, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO,
				LAST_RACE_TYPE);
		tfacet.remove(id, t2, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO);
		tfacet.remove(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
	}

	@Test
	public void testGetFromTemplateSecondOverrides()
	{
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.addToListFor(ListKey.RACESUBTYPE, RACE_TYPE_TOO);
		tfacet.add(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO);
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.addToListFor(ListKey.RACESUBTYPE, LAST_RACE_TYPE);
		tfacet.add(id, t2, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO,
				LAST_RACE_TYPE);
		tfacet.remove(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, LAST_RACE_TYPE);
		tfacet.add(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, RACE_TYPE_TOO,
				LAST_RACE_TYPE);
		tfacet.remove(id, t, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE, LAST_RACE_TYPE);
		tfacet.remove(id, t2, this);
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
	}

	@Test
	public void testContains()
	{
		assertFalse(facet.contains(id, TEST_RACE_TYPE));
		assertFalse(facet.contains(id, RACE_TYPE_TOO));
		assertFalse(facet.contains(id, LAST_RACE_TYPE));
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertFalse(facet.contains(id, RACE_TYPE_TOO));
		assertFalse(facet.contains(id, LAST_RACE_TYPE));
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.addToListFor(ListKey.RACESUBTYPE, RACE_TYPE_TOO);
		tfacet.add(id, t, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertTrue(facet.contains(id, RACE_TYPE_TOO));
		assertFalse(facet.contains(id, LAST_RACE_TYPE));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.addToListFor(ListKey.RACESUBTYPE, LAST_RACE_TYPE);
		tfacet.add(id, t2, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertTrue(facet.contains(id, RACE_TYPE_TOO));
		assertTrue(facet.contains(id, LAST_RACE_TYPE));
		tfacet.remove(id, t, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertFalse(facet.contains(id, RACE_TYPE_TOO));
		assertTrue(facet.contains(id, LAST_RACE_TYPE));
		tfacet.add(id, t, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertTrue(facet.contains(id, RACE_TYPE_TOO));
		assertTrue(facet.contains(id, LAST_RACE_TYPE));
		tfacet.remove(id, t, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertFalse(facet.contains(id, RACE_TYPE_TOO));
		assertTrue(facet.contains(id, LAST_RACE_TYPE));
		tfacet.remove(id, t2, this);
		assertTrue(facet.contains(id, TEST_RACE_TYPE));
		assertFalse(facet.contains(id, RACE_TYPE_TOO));
		assertFalse(facet.contains(id, LAST_RACE_TYPE));
	}

	@Test
	public void testCount()
	{
		assertEquals(0, facet.getCount(id));
		Race r = new Race();
		r.addToListFor(ListKey.RACESUBTYPE, TEST_RACE_TYPE);
		rfacet.set(id, r);
		assertEquals(1, facet.getCount(id));
		assertList(facet.getRacialSubTypes(id), TEST_RACE_TYPE);
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.addToListFor(ListKey.RACESUBTYPE, RACE_TYPE_TOO);
		tfacet.add(id, t, this);
		assertEquals(2, facet.getCount(id));
		PCTemplate t2 = new PCTemplate();
		t2.setName("Other");
		t2.addToListFor(ListKey.RACESUBTYPE, LAST_RACE_TYPE);
		tfacet.add(id, t2, this);
		assertEquals(3, facet.getCount(id));
		tfacet.remove(id, t, this);
		assertEquals(2, facet.getCount(id));
		tfacet.add(id, t, this);
		assertEquals(3, facet.getCount(id));
		tfacet.remove(id, t, this);
		assertEquals(2, facet.getCount(id));
		tfacet.remove(id, t2, this);
		assertEquals(1, facet.getCount(id));
		/*
		 * TODO Note this doesn't test duplicates. We need to check appropriate
		 * behavior of RaceSubType (set vs. list)
		 */
	}

	private <T> void assertList(Collection<T> c, T... array)
	{
		assertNotNull(c);
		assertNotNull(array);
		assertEquals(array.length, c.size());
		/*
		 * WARNING This method doesn't check for A,B,B,B tested against A,A,B,B
		 * 
		 * Number of instances is not properly counted!
		 */
		for (T obj : array)
		{
			assertTrue(c.contains(obj));
		}
	}

}
