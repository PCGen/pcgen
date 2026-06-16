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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.testsupport.AbstractSourcedListFacetTest;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.bonustokens.Combat;

import org.junit.jupiter.api.BeforeEach;

public class AddedBonusFacetTest extends AbstractSourcedListFacetTest<BonusObj>
{

	private AddedBonusFacet facet = new AddedBonusFacet();

	@Override
	protected AbstractSourcedListFacet<CharID, BonusObj> getFacet()
	{
		return facet;
	}

	private LoadContext context;

	@BeforeEach
	@Override
	public void setUp()
	{
		super.setUp();
		context =
				new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		addBonus(Combat.class);

	}

	@Override
	protected BonusObj getObject()
	{
		return Bonus.newBonus(context, "COMBAT|AC|2");
	}
}
