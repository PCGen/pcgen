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
import pcgen.cdom.enumeration.SubRace;
import pcgen.core.PCTemplate;

public class SubRaceFacetTest extends TestCase
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

	@Override
	public void setUp() throws Exception
	{
		facet = new SubRaceFacet();
		super.setUp();
		facet.setTemplateFacet(tfacet);
		id = new CharID();
		altid = new CharID();
	}

	@Test
	public void testSubRaceUnsetNull()
	{
		assertNull(facet.getSubRace(id));
	}

	@Test
	public void testWithNothingInTemplates()
	{
		tfacet.add(id, new PCTemplate());
		assertNull(facet.getSubRace(id));
	}

	@Test
	public void testAvoidPollution()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testUseTemplateNameTrue()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct);
		assertEquals("TestTemplate", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testUseTemplateNameFalse()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, false);
		tfacet.add(id, pct);
		assertNull(facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSet()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSetUseNameTrueSubRaceDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testSubRaceSetUseNameFalseSubRaceDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, false);
		tfacet.add(id, pct);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testMultipleSubRaceSetSecondDominatesSubRace()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRaceToo"));
		tfacet.add(id, pct2);
		assertEquals("TestSubRaceToo", facet.getSubRace(id));
		tfacet.remove(id, pct2);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertNull(facet.getSubRace(altid));
	}

	@Test
	public void testMultipleSubRaceSetSecondDominatesUseTemplateName()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBRACE, SubRace.getConstant("TestSubRace"));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("TestTemplateToo");
		pct2.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
		tfacet.add(id, pct2);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.add(id, pct);
		assertEquals("TestSubRace", facet.getSubRace(id));
		tfacet.remove(id, pct);
		assertEquals("TestTemplateToo", facet.getSubRace(id));
		tfacet.remove(id, pct2);
		assertNull(facet.getSubRace(altid));
	}

}
