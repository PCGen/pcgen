/*
 *
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.base.ChoiceActor;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;

import org.junit.jupiter.api.Test;

public class SpellCasterTokenTest extends
        AbstractAddTokenTestCase<PCClass>
{

    static SpellCasterToken subtoken = new SpellCasterToken();

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<PCClass> getTargetClass()
    {
        return PCClass.class;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    public boolean allowsFormula()
    {
        return true;
    }

    @Override
    public String getAllString()
    {
        return "ANY";
    }


    @Test
    public void testRoundRobinArcane() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "Arcane");
    }

    @Test
    public void testRoundRobinDivine() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "Divine");
    }

    @Test
    public void testRoundRobinPsionic() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "Psionic");
    }

    @Test
    public void testRoundRobinThreeSpellType() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "Arcane" + getJoinCharacter()
                + "Divine" + getJoinCharacter() + "Psionic");
    }

    @Test
    public void testInvalidInputAnySpellType()
    {
        if (isAllLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + getAllString()
                    + getJoinCharacter() + "Arcane"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputSpellTypeAny()
    {
        if (isAllLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + "Arcane"
                    + getJoinCharacter() + getAllString()));
            assertNoSideEffects();
        }
    }

    @Override
    protected ChoiceActor<PCClass> getActor()
    {
        return subtoken;
    }
}
