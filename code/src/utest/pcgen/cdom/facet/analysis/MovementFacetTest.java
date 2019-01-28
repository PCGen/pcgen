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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.Movement;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.BeforeEach;

public class MovementFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, Movement>
{

	private MovementFacet facet = new MovementFacet();
	private Movement[] target;
	private CDOMObject[] source;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		CDOMObject cdo1 = new PCTemplate();
		cdo1.setName("Templ");
		CDOMObject cdo2 = new Race();
		cdo2.setName("Race");
		PCStat pcs1 = new PCStat();
		pcs1.setName("Stat1");
		PCStat pcs2 = new PCStat();
		pcs2.setName("Stat2");
		Movement st1 = new Movement(1);
		Movement st2 = new Movement(1);
		cdo1.addToListFor(ListKey.MOVEMENT, st1);
		cdo2.addToListFor(ListKey.MOVEMENT, st2);
		source = new CDOMObject[]{cdo1, cdo2};
		target = new Movement[]{st1, st2};
	}

	@Override
	protected AbstractSourcedListFacet<CharID, Movement> getFacet()
	{
		return facet;
	}

	@Override
	protected Movement getObject()
	{
		return new Movement(1);
	}

	@Override
	protected CDOMObject getContainingObject(int i)
	{
		return source[i];
	}

	@Override
	protected DataFacetChangeListener<CharID, CDOMObject> getListener()
	{
		return facet;
	}

	@Override
	protected Movement getTargetObject(int i)
	{
		return target[i];
	}
}
