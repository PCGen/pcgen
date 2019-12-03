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
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NonProficiencyPenaltyFacetTest
{
	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * TemplateFacet framework. This class trusts that TemplateFacetTest has
	 * fully vetted TemplateFacet. PLEASE ensure all tests there are working
	 * before investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private NonProficiencyPenaltyFacet facet;
	private TemplateFacet tfacet = new TemplateFacet();

	@BeforeEach
	public void setUp() {
		facet = new NonProficiencyPenaltyFacet();
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
	public void testGenderUnsetNull()
	{
		assertEquals(SettingsHandler.getGame().getNonProfPenalty(), facet.getPenalty(id));
	}

	@Test
	public void testWithNothingInTemplates()
	{
		tfacet.add(id, new PCTemplate(), this);
		assertEquals(SettingsHandler.getGame().getNonProfPenalty(), facet.getPenalty(id));
	}

	@Test
	public void testAvoidPollution()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(IntegerKey.NONPP, -2);
		tfacet.add(id, pct, this);
		assertEquals(SettingsHandler.getGame().getNonProfPenalty(), facet.getPenalty(altid));
	}

	@Test
	public void testGenderLocked()
	{
		PCTemplate pct = new PCTemplate();
		pct.put(IntegerKey.NONPP, -3);
		tfacet.add(id, pct, this);
		assertEquals(-3, facet.getPenalty(id));
		tfacet.remove(id, pct, this);
		assertEquals(SettingsHandler.getGame().getNonProfPenalty(), facet.getPenalty(id));
	}

	@Test
	public void testMultipleGenderSetSecondDominatesGender()
	{
		PCTemplate pct = new PCTemplate();
		pct.setName("PCT");
		pct.put(IntegerKey.NONPP, -2);
		tfacet.add(id, pct, this);
		assertEquals(-2, facet.getPenalty(id));
		PCTemplate pct2 = new PCTemplate();
		pct2.setName("Other");
		pct2.put(IntegerKey.NONPP, -3);
		tfacet.add(id, pct2, this);
		assertEquals(-3, facet.getPenalty(id));
		tfacet.remove(id, pct, this);
		assertEquals(-3, facet.getPenalty(id));
		tfacet.add(id, pct, this);
		assertEquals(-2, facet.getPenalty(id));
		tfacet.remove(id, pct, this);
		assertEquals(-3, facet.getPenalty(id));
		tfacet.remove(id, pct2, this);
		assertEquals(SettingsHandler.getGame().getNonProfPenalty(), facet.getPenalty(id));
	}

}
