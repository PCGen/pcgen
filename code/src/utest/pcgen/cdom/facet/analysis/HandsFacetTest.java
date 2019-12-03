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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HandsFacetTest
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
	private HandsFacet facet;
	private RaceFacet rfacet = new RaceFacet();
	private TemplateFacet tfacet = new TemplateFacet();

	@BeforeEach
	public void setUp() {
		facet = new HandsFacet();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet.setRaceFacet(rfacet);
		facet.setTemplateFacet(tfacet);
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
	public void testRaceTypeUnsetNull()
	{
		assertEquals(0, facet.getHands(id));
	}

	@Test
	public void testWithNothingInRaceDefault2()
	{
		rfacet.set(id, new Race());
		assertEquals(2, facet.getHands(id));
	}

	@Test
	public void testAvoidPollution()
	{
		Race r = new Race();
		r.put(IntegerKey.CREATURE_HANDS, 5);
		rfacet.set(id, r);
		assertEquals(0, facet.getHands(altid));
	}

	@Test
	public void testGetFromRace()
	{
		Race r = new Race();
		r.put(IntegerKey.CREATURE_HANDS, 5);
		rfacet.set(id, r);
		assertEquals(5, facet.getHands(id));
		rfacet.remove(id);
		assertEquals(0, facet.getHands(id));
	}

	@Test
	public void testGetFromTemplate()
	{
		rfacet.set(id, new Race());
		PCTemplate t = new PCTemplate();
		t.put(IntegerKey.CREATURE_HANDS, 5);
		tfacet.add(id, t, this);
		assertEquals(5, facet.getHands(id));
		tfacet.remove(id, t, this);
		assertEquals(2, facet.getHands(id));
	}

	@Test
	public void testGetFromTemplateSecondOverrides()
	{
		Race r = new Race();
		r.put(IntegerKey.CREATURE_HANDS, 5);
		rfacet.set(id, r);
		assertEquals(5, facet.getHands(id));
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.put(IntegerKey.CREATURE_HANDS, 3);
		tfacet.add(id, t, this);
		assertEquals(3, facet.getHands(id));
		PCTemplate t5 = new PCTemplate();
		t5.setName("Other");
		t5.put(IntegerKey.CREATURE_HANDS, 4);
		tfacet.add(id, t5, this);
		assertEquals(4, facet.getHands(id));
		tfacet.remove(id, t, this);
		assertEquals(4, facet.getHands(id));
		tfacet.add(id, t, this);
		assertEquals(3, facet.getHands(id));
		tfacet.remove(id, t, this);
		assertEquals(4, facet.getHands(id));
		tfacet.remove(id, t5, this);
		assertEquals(5, facet.getHands(id));
	}
}
