/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.EquipmentModifier;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ServesAsTokenTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new ServesAsToken();
    static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<>();

    @Override
    public CDOMLoader<Skill> getLoader()
    {
        return loader;
    }

    @Override
    public Class<Skill> getCDOMClass()
    {
        return Skill.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidObject()
    {
        assertFalse(token.parseToken(primaryContext, new EquipmentModifier(),
                "Fireball").passed());
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeOnly()
    {
        assertFalse(parse("SKILL"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeBarOnly()
    {
        assertFalse(parse("SKILL|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyType()
    {
        assertFalse(parse("|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadType()
    {
        assertFalse(parse("CAMPAIGN|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidCatTypeNoEqual()
    {
        assertFalse(parse("ABILITY|Abil"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNonCatTypeEquals()
    {
        assertFalse(parse("SKILL=Arcane|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleEquals()
    {
        assertFalse(parse("ABILITY=FEAT=Mutation|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidUnbuiltCategory()
    {
        try
        {
            assertFalse(parse("ABILITY=Crazy|Fireball"));
        } catch (IllegalArgumentException e)
        {
            //OK as well
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellbookAndSpellBarOnly()
    {
        assertFalse(parse("SKILL|Fireball|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellBarStarting()
    {
        assertFalse(parse("SKILL||Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidWrongType()
    {
        assertFalse(parse("SPELL|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinJustSkill() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        runRoundRobin("SKILL|Fireball");
    }

    @Test
    public void testRoundRobinJustAbility() throws PersistenceLayerException
    {
        primaryProf = new Ability();
        secondaryProf = new Ability();
        AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
                AbilityCategory.class, "NEWCAT");
        AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
                AbilityCategory.class, "NEWCAT");
        BuildUtilities.buildAbility(primaryContext, pac, "Abil3");
        BuildUtilities.buildAbility(secondaryContext, sac, "Abil3");
        runRoundRobin("ABILITY=NEWCAT|Abil3");
    }


    // @Test
    // public void testRoundRobinJustSubClass() throws PersistenceLayerException
    // {
    // primaryProf = new SubClass();
    // secondaryProf = new SubClass();
    // primaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
    // secondaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
    // runRoundRobin("CLASS|Fireball");
    //	}

    @Test
    public void testRoundRobinTwoSpell() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Skill.class,
                "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Skill.class,
                "Lightning Bolt");
        runRoundRobin("SKILL|Fireball|Lightning Bolt");
    }

    @Override
    protected String getLegalValue()
    {
        return "SKILL|Jump";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SKILL|Fireball|Lightning Bolt";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return strings -> new String[]{"SKILL|Fireball|Jump|Lightning Bolt"};
    }
}
