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
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubRaceFacetTest
{
	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * TemplateFacet framework. This class trusts that TemplateFacetTest has
	 * fully vetted TemplateFacet. PLEASE ensure all tests there are working
	 * before investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private SubRaceFacet facet;
	private TemplateFacet tfacet = new TemplateFacet();

	@BeforeEach
	public void setUp() {
		facet = new SubRaceFacet();
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
		tfacet = null;
	}

	@Test
	public void testSubRaceUnsetNull()
	{
		assertNull(facet.getSubRace(id));
	}

	@Test
	public void testWithNothingInTemplates()
	{
		tfacet.add(id, new PCTemplate(), this);
		assertNull(facet.getSubRace(id));
	}

	@Test
	public void testAvoidPollution()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testUseTemplateNameTrue()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct, this);
		assertEquals("TestTemplate", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testUseTemplateNameFalse()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, false);
		tfacet.add(id, pct, this);
		assertNull(facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSet()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct, this);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSetUseNameTrueSubRaceDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct, this);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSetUseNameFalseSubRaceDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, false);
		tfacet.add(id, pct, this);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testMultipleSubRaceSetSecondDominatesSubRace()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("PCT");
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct, this);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("PCT2");
		pct2.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRaceToo"));
		tfacet.add(id, pct2, this);
		assertEquals("TestSubRaceToo", facet.getSubRace(id));
		tfacet.remove(id, pct2, this);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testMultipleSubRaceSetSecondDominatesUseTemplateName()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct, this);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("TestTemplateToo");
		pct2.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct2, this);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.add(id, pct, this);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct, this);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.remove(id, pct2, this);
		assertNull(facet.getSubRace(altid));
	}

}
