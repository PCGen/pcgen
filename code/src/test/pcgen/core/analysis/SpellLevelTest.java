/*
 * Copyright 2008 (C) James Dempsey
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

package pcgen.core.analysis;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.KnownSpellFacet;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * The Class {@code SpellLevelTest} checks the SpellLevel class.
 */
public class SpellLevelTest extends AbstractCharacterTestCase
{

    private static KnownSpellFacet listManagerFacet = FacetLibrary
            .getFacet(KnownSpellFacet.class);

    /**
     * @throws Exception
     */
    @Test
    public void testGetPCBasedBonusKnownSpells() throws Exception
    {
        LoadContext context = Globals.getContext();
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }


        final String classLine =
                "CLASS:Sorcerer	TYPE:Base.PC	SPELLSTAT:CHA	SPELLTYPE:Arcane	MEMORIZE:NO	BONUS:CASTERLEVEL|Sorcerer|CL";
        PCClassLoader classLoader = new PCClassLoader();
        PCClass pcc = classLoader.parseLine(context, null, classLine, source);

        Spell spell = TestHelper.makeSpell("Bless");

        String abilityLine =
                "Spell bonanza	CATEGORY:FEAT	SPELLKNOWN:CLASS|Sorcerer=3|KEY_Bless";
        AbilityLoader abilityLoader = new AbilityLoader();
        abilityLoader.parseLine(context, null, abilityLine, source);
        Ability ab1 = Globals.getContext().getReferenceContext()
                .getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Spell bonanza");

        // Do the post parsing cleanup
        finishLoad();

        PlayerCharacter aPC = getCharacter();

        Collection<Integer> levels = listManagerFacet.getScopes2(aPC.getCharID(), pcc.get(ObjectKey.CLASS_SPELLLIST));
        assertEquals("Initial number of spell levels incorrect", 0, levels.size());

        addAbility(BuildUtilities.getFeatCat(), ab1);

        // Now for the tests
        levels = listManagerFacet.getScopes2(aPC.getCharID(), pcc.get(ObjectKey.CLASS_SPELLLIST));
        assertEquals("Incorrect number of spell levels returned", 1, levels.size());
        assertEquals("Incorrect spell level returned", Integer.valueOf(3), levels.iterator().next());
        Collection<Spell> result = listManagerFacet.getSet(aPC.getCharID(), pcc.get(ObjectKey.CLASS_SPELLLIST), 3);
        assertEquals("Incorrect number of spells returned", 1, result.size());
        assertEquals("Incorrect spell returned", spell, result.iterator().next());

    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
