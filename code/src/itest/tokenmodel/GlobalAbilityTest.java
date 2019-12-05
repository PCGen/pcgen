/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package tokenmodel;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.AbilityLst;
import plugin.lsttokens.testsupport.BuildUtilities;

import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import util.TestURI;

public class GlobalAbilityTest extends AbstractGrantedListTokenTest<Ability>
{

    private static AbilityLst token = new AbilityLst();

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "FEAT|VIRTUAL|Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    protected Class<Ability> getGrantClass()
    {
        return Ability.class;
    }

    @Override
    protected DirectAbilityFacet getTargetFacet()
    {
        return directAbilityFacet;
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @Override
    protected int getCount()
    {
        return getTargetFacet().getCount(id);
    }

    @Override
    protected boolean containsExpected(Ability granted)
    {
        Collection<CNAbilitySelection> casSet =
                getTargetFacet().getSet(id);
        for (CNAbilitySelection cnas : casSet)
        {
            CNAbility cas = cnas.getCNAbility();
            boolean featExpected =
                    cas.getAbilityCategory() == BuildUtilities.getFeatCat();
            boolean abilityExpected = cas.getAbility()
                    .equals(context.getReferenceContext()
                            .getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Granted"));
            boolean natureExpected = cas.getNature() == Nature.VIRTUAL;
            boolean selectionExpected = cnas.getSelection() == null;
            if (featExpected && abilityExpected && natureExpected
                    && selectionExpected)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Ability createGrantedObject()
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Granted");
        context.getReferenceContext().importObject(a);
        return a;
    }

}
