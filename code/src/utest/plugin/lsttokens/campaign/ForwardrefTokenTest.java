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
package plugin.lsttokens.campaign;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ForwardrefTokenTest extends AbstractCDOMTokenTestCase<Campaign>
{

    static CDOMPrimaryToken<Campaign> token = new ForwardRefToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
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
        assertFalse(parse("SPELL"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeBarOnly()
    {
        assertFalse(parse("SPELL|"));
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
    public void testInvalidBadLeadingComma()
    {
        assertFalse(parse("SPELL|,Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadTrailingComma()
    {
        assertFalse(parse("SPELL|Fireball,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadDoubleComma()
    {
        assertFalse(parse("SPELL|Fireball,,LightningBolt"));
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
        assertFalse(parse("SPELL=Arcane|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleEquals()
    {
        assertFalse(parse("ABILITY=FEAT=Mutation|Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellbookAndSpellBarOnly()
    {
        assertFalse(parse("SPELL|Fireball|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellBarStarting()
    {
        assertFalse(parse("SPELL||Fireball"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinJustSpell() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        runRoundRobin("SPELL|Fireball");
    }

    @Test
    public void testRoundRobinJustAbility() throws PersistenceLayerException
    {
        AbilityCategory newCatp =
                primaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class, "NEWCAT");
        AbilityCategory newCats =
                secondaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class, "NEWCAT");
        constructAbility(primaryContext, newCatp, "Abil3");
        constructAbility(secondaryContext, newCats, "Abil3");
        runRoundRobin("ABILITY=NEWCAT|Abil3");
    }

    @Test
    public void testRoundRobinTwoSpell() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        runRoundRobin("SPELL|Fireball,Lightning Bolt");
    }

    @Test
    public void testRoundRobinAbilitySpell() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        AbilityCategory newCatp =
                primaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class, "NEWCAT");
        AbilityCategory newCats =
                secondaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class, "NEWCAT");
        constructAbility(primaryContext, newCatp, "Abil3");
        constructAbility(secondaryContext, newCats, "Abil3");
        runRoundRobin("ABILITY=NEWCAT|Abil3", "SPELL|Lightning Bolt");
    }

    @Test
    public void testRoundRobinFeatSpell()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        constructAbility(primaryContext, BuildUtilities.getFeatCat(), "My Feat");
        constructAbility(secondaryContext, BuildUtilities.getFeatCat(), "My Feat");
        runRoundRobin("ABILITY=FEAT|My Feat", "SPELL|Lightning Bolt");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SPELL|Lightning Bolt";
    }

    @Override
    protected String getLegalValue()
    {
        return "ABILITY=FEAT|My Feat";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    private void constructAbility(LoadContext context, AbilityCategory newCatp,
            String name)
    {
        Ability a = newCatp.newInstance();
        a.setDisplayName(name);
        context.getReferenceContext().importObject(a);
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        super.additionalSetup(context);
        //Dummy items to ensure Category is initialized
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Dummy");
        context.getReferenceContext().importObject(a);
    }
}
