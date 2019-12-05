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
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.testsupport.AbstractListFacetTest;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.bonustokens.Combat;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class AppliedBonusFacetTest extends AbstractListFacetTest<BonusObj>
{
    private AppliedBonusFacet facet = new AppliedBonusFacet();
    private LoadContext context;

    @Override
    protected AbstractListFacet<CharID, BonusObj> getFacet()
    {
        return facet;
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        context =
                new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                        new ConsolidatedListCommitStrategy());
        addBonus(Combat.class);
    }

    @AfterEach
    @Override
    public void tearDown()
    {
        TokenRegistration.clearTokens();
        facet = null;
        context = null;
        super.tearDown();
    }

    @Override
    protected BonusObj getObject()
    {
        return Bonus.newBonus(context, "COMBAT|AC|2");
    }
}
