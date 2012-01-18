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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.PCTemplate;

public class RegionFacetTest extends TestCase
{
	private static final String TEST_REGION_NAME = "TestRegion";
	private static final String TEST_SUB_REGION_NAME = "TestSubRegion";
	private static final String TEST_FULL_REGION_NAME = TEST_REGION_NAME + " ("
			+ TEST_SUB_REGION_NAME + ")";

	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * TemplateFacet framework. This class trusts that TemplateFacetTest has
	 * fully vetted TemplateFacet. PLEASE ensure all tests there are working
	 * before investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private RegionFacet facet;
	private TemplateFacet tfacet = new TemplateFacet();

	@Override
	public void setUp() throws Exception
	{
		facet = new RegionFacet();
		super.setUp();
		facet.setTemplateFacet(tfacet);
		id = new CharID();
		altid = new CharID();
	}

	@Test
	public void testRegionUnsetNull()
	{
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testWithNothingInTemplates()
	{
		tfacet.add(id, new PCTemplate());
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
		facet.setRegion(id, Region.getConstant(TEST_REGION_NAME));
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
		facet.setSubRegion(id, SubRegion.getConstant(TEST_SUB_REGION_NAME));
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getCharacterSubRegion(id));
		assertEquals(TEST_FULL_REGION_NAME, facet.getFullRegion(id));
	}

	@Test
	public void testAvoidPollution()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(altid));
		assertEquals(Constants.NONE, facet.getCharacterRegion(altid));
		assertEquals(Constants.NONE, facet.getSubRegion(altid));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(altid));
		assertEquals(Constants.NONE, facet.getFullRegion(altid));
		facet.setRegion(id, Region.getConstant(TEST_REGION_NAME));
		facet.setSubRegion(id, SubRegion.getConstant(TEST_SUB_REGION_NAME));
		assertEquals(Constants.NONE, facet.getRegion(altid));
		assertEquals(Constants.NONE, facet.getCharacterRegion(altid));
		assertEquals(Constants.NONE, facet.getSubRegion(altid));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(altid));
		assertEquals(Constants.NONE, facet.getFullRegion(altid));
	}

	@Test
	public void testUseTemplateNameRegionTrue()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		tfacet.add(id, pct);
		assertEquals("TestTemplate", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestTemplate", facet.getFullRegion(id));
	}

	@Test
	public void testUseTemplateNameRegionFalse()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, false);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSet()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		tfacet.add(id, pct);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSetUseNameTrueRegionDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		tfacet.add(id, pct);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSetUseNameFalseRegionDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, false);
		tfacet.add(id, pct);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testMultipleRegionSetSecondDominatesRegion()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		tfacet.add(id, pct);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.REGION, Region.getConstant("TestRegionToo"));
		tfacet.add(id, pct2);
		assertEquals("TestRegionToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestRegionToo", facet.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
	}

	@Test
	public void testMultipleRegionSetSecondDominatesUseTemplateName()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("TestTemplateToo");
		pct2.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		tfacet.add(id, pct2);
		assertEquals("TestTemplateToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestTemplateToo", facet.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getFullRegion(id));
	}

	@Test
	public void testUseTemplateNameSubRegionTrue()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestTemplate", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (TestTemplate)", facet
				.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testUseTemplateNameSubRegionFalse()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, false);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testSubRegionSet()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (" + TEST_SUB_REGION_NAME + ")",
				facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSetUseNameTrueSubRegionDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (" + TEST_SUB_REGION_NAME + ")",
				facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSetUseNameFalseSubRegionDominates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, false);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (" + TEST_SUB_REGION_NAME + ")",
				facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testMultipleSubRegionSetSecondDominatesSubRegion()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.SUBREGION, SubRegion.getConstant("TestRegionToo"));
		tfacet.add(id, pct2);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestRegionToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (TestRegionToo)", facet
				.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (" + TEST_SUB_REGION_NAME + ")",
				facet.getFullRegion(id));
	}

	@Test
	public void testMultipleSubRegionSetSecondDominatesUseTemplateName()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("TestTemplateToo");
		pct2.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		tfacet.add(id, pct2);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestTemplateToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (TestTemplateToo)", facet
				.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE + " (" + TEST_SUB_REGION_NAME + ")",
				facet.getFullRegion(id));
	}

	@Test
	public void testUseTemplateNameRegionSubRegionTrue()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		tfacet.add(id, pct);
		assertEquals("TestTemplate", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestTemplate", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestTemplate (TestTemplate)", facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testUseTemplateNameRegionSubRegionFalse()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("TestTemplate");
		pct.put(ObjectKey.USETEMPLATENAMEFORREGION, false);
		pct.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, false);
		tfacet.add(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testRegionSubRegionSet()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(TEST_FULL_REGION_NAME, facet.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testMultipleRegionSubRegionSetSecondDominatesRegionSubRegion()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.REGION, Region.getConstant("TestRegionToo"));
		pct2
				.put(ObjectKey.SUBREGION, SubRegion
						.getConstant("TestSubRegionToo"));
		tfacet.add(id, pct2);
		assertEquals("TestRegionToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestSubRegionToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestRegionToo (TestSubRegionToo)", facet
				.getFullRegion(id));
		tfacet.remove(id, pct);
		assertEquals("TestRegionToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestSubRegionToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestRegionToo (TestSubRegionToo)", facet
				.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(Constants.NONE, facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals(Constants.NONE, facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals(Constants.NONE, facet.getFullRegion(id));
	}

	@Test
	public void testMultipleRegionSubRegionSetSecondDominatesUseTemplateName()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant(TEST_REGION_NAME));
		pct.put(ObjectKey.SUBREGION, SubRegion
				.getConstant(TEST_SUB_REGION_NAME));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("TestTemplateToo");
		pct2.put(ObjectKey.USETEMPLATENAMEFORREGION, true);
		pct2.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
		tfacet.add(id, pct2);
		assertEquals("TestTemplateToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestTemplateToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestTemplateToo (TestTemplateToo)", facet
				.getFullRegion(id));
	}

	@Test
	public void testExplicitOverrideTemplates()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant("InitRegion"));
		pct.put(ObjectKey.SUBREGION, SubRegion.getConstant("InitSubRegion"));
		tfacet.add(id, pct);
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.REGION, Region.getConstant("TestRegionToo"));
		pct2
				.put(ObjectKey.SUBREGION, SubRegion
						.getConstant("TestSubRegionToo"));
		tfacet.add(id, pct2);
		assertEquals("TestRegionToo", facet.getRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterRegion(id));
		assertEquals("TestSubRegionToo", facet.getSubRegion(id));
		assertEquals(Constants.NONE, facet.getCharacterSubRegion(id));
		assertEquals("TestRegionToo (TestSubRegionToo)", facet
				.getFullRegion(id));
		facet.setRegion(id, Region.getConstant(TEST_REGION_NAME));
		facet.setSubRegion(id, SubRegion.getConstant(TEST_SUB_REGION_NAME));
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getCharacterSubRegion(id));
		assertEquals(TEST_FULL_REGION_NAME, facet.getFullRegion(id));
		tfacet.remove(id, pct2);
		assertEquals(TEST_REGION_NAME, facet.getRegion(id));
		assertEquals(TEST_REGION_NAME, facet.getCharacterRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getSubRegion(id));
		assertEquals(TEST_SUB_REGION_NAME, facet.getCharacterSubRegion(id));
		assertEquals(TEST_FULL_REGION_NAME, facet.getFullRegion(id));
	}

	@Test
	public void testMatchesRegion()
	{
		assertTrue(facet.matchesRegion(id, null));
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant("InitRegion"));
		pct.put(ObjectKey.SUBREGION, SubRegion.getConstant("InitSubRegion"));
		tfacet.add(id, pct);
		assertTrue(facet.matchesRegion(id, Region.getConstant("InitRegion")));
		assertFalse(facet.matchesRegion(id, Region.getConstant("TestRegionToo")));
		PCTemplate pct2 = new PCTemplate();
		pct2.put(ObjectKey.REGION, Region.getConstant("TestRegionToo"));
		pct2
				.put(ObjectKey.SUBREGION, SubRegion
						.getConstant("TestSubRegionToo"));
		tfacet.add(id, pct2);
		assertTrue(facet.matchesRegion(id, Region.getConstant("TestRegionToo")));
		assertFalse(facet.matchesRegion(id, Region.getConstant("InitRegion")));
		tfacet.remove(id, pct2);
		assertTrue(facet.matchesRegion(id, Region.getConstant("InitRegion")));
		assertFalse(facet.matchesRegion(id, Region.getConstant("TestRegionToo")));
		facet.setRegion(id, Region.getConstant(TEST_REGION_NAME));
		assertTrue(facet
				.matchesRegion(id, Region.getConstant(TEST_REGION_NAME)));
		assertFalse(facet.matchesRegion(id, Region.getConstant("TestRegionToo")));
	}

	@Test
	public void testCopyContents()
	{
		assertTrue(facet.matchesRegion(id, null));
		assertTrue(facet.matchesRegion(altid, null));
		PCTemplate pct = new PCTemplate();
		pct.put(ObjectKey.REGION, Region.getConstant("InitRegion"));
		pct.put(ObjectKey.SUBREGION, SubRegion.getConstant("InitSubRegion"));
		tfacet.add(id, pct);
		facet.copyContents(id, altid);
		//Not an explicit copy, so still null (based on templates!)
		assertTrue(facet.matchesRegion(altid, null));
		facet.setRegion(id, Region.getConstant(TEST_REGION_NAME));
		assertTrue(facet
				.matchesRegion(id, Region.getConstant(TEST_REGION_NAME)));
		assertFalse(facet.matchesRegion(id, Region.getConstant("TestRegionToo")));
		//alt didn't change
		assertTrue(facet.matchesRegion(altid, null));
		//Now copy
		facet.copyContents(id, altid);
		assertTrue(facet.matchesRegion(altid, Region.getConstant(TEST_REGION_NAME)));
		assertFalse(facet.matchesRegion(altid, Region.getConstant("TestRegionToo")));
		//and prove independence
		facet.setRegion(id, Region.getConstant("TestNewRegion"));
		assertTrue(facet
				.matchesRegion(id, Region.getConstant("TestNewRegion")));
		assertTrue(facet.matchesRegion(altid, Region.getConstant(TEST_REGION_NAME)));

	}
}
