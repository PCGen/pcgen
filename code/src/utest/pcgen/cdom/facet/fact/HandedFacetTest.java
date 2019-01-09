/*
 * Copyright 2012 Vincent Lhote
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
package pcgen.cdom.facet.fact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.output.channel.compat.HandedCompat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HandedFacetTest
{
	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * TemplateFacet framework. This class trusts that TemplateFacetTest has
	 * fully vetted TemplateFacet. PLEASE ensure all tests there are working
	 * before investigating tests here.
	 */
	private CharID id;
	private HandedFacet facet;
	private TemplateFacet tfacet = new TemplateFacet();

	@BeforeEach
	public void setUp() throws Exception
	{
		facet = new HandedFacet();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
	}

	@Test
	public void testHandedUnsetNull()
	{
		assertEquals(HandedCompat.getDefaultHanded(), facet.getHanded(id));
	}

	@Test
	public void testWithNothingInTemplates()
	{
		tfacet.add(id, new PCTemplate(), this);
		assertEquals(HandedCompat.getDefaultHanded(), facet.getHanded(id));
	}

	@Test
	public void testHandedSet()
	{
		facet.setHanded(id, Handed.Left);
		assertEquals(Handed.Left, facet.getHanded(id));
		facet.removeHanded(id);
		assertEquals(HandedCompat.getDefaultHanded(), facet.getHanded(id));
	}

}
