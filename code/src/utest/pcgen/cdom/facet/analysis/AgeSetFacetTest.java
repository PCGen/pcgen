/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;
import pcgen.core.AgeSet;

public class AgeSetFacetTest extends AbstractItemFacetTest<AgeSet>
{
	private static int n = 0;

	private final AgeSetFacet facet = new AgeSetFacet();

	@Override
	protected AbstractItemFacet<CharID, AgeSet> getFacet()
	{
		return facet;
	}

	@Override
	protected AgeSet getItem()
	{
		AgeSet ageSet = new AgeSet();
		ageSet.setName("AgeSet" + n);
		ageSet.setAgeIndex(n++);
		return ageSet;
	}
}
