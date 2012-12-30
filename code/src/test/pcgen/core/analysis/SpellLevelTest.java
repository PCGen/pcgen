/*
 * SpellLevelTest.java
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
 *
 * Created on 04/01/2009 11:40:53 AM
 *
 * $Id: $
 */

package pcgen.core.analysis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ConditionallyKnownSpellFacet;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
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

/**
 * The Class <code>SpellLevelTest</code> checks the SpellLevel class.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class SpellLevelTest extends AbstractCharacterTestCase
{

	private static ConditionallyKnownSpellFacet listManagerFacet = FacetLibrary
			.getFacet(ConditionallyKnownSpellFacet.class);

	/**
	 * Test method for {@link pcgen.core.analysis.SpellLevel#getPCBasedBonusKnownSpells(pcgen.core.PlayerCharacter, pcgen.core.PCClass)}.
	 * @throws Exception 
	 */
	public void testGetPCBasedBonusKnownSpells() throws Exception
	{
		LoadContext context = Globals.getContext();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
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
		Ability ab1 = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class,
						AbilityCategory.FEAT, "Spell bonanza");

		// Do the post parsing cleanup
		context.resolveDeferredTokens();
		context.ref.buildDeferredObjects();
		context.ref.buildDerivedObjects();
		context.ref.validate(null);
		assertTrue(context.ref.resolveReferences(null));
		context.buildTypeLists();

		PlayerCharacter aPC = getCharacter();

		Map<Integer, List<Spell>> spellsMap = listManagerFacet.getKnownSpells(aPC.getCharID(), pcc.get(ObjectKey.CLASS_SPELLLIST));
		assertEquals("Initial number of spell levels incorrect", 0, spellsMap.size());
		
		aPC.addAbilityNeedCheck(AbilityCategory.FEAT, ab1);

		// Now for the tests
		spellsMap = listManagerFacet.getKnownSpells(aPC.getCharID(), pcc.get(ObjectKey.CLASS_SPELLLIST));
		assertEquals("Incorrect number of spell levels returned", 1, spellsMap.size());
		assertEquals("Incorrect spell level returned", new Integer(3), spellsMap.keySet().iterator().next());
		List<Spell> result = spellsMap.values().iterator().next();
		assertEquals("Incorrect number of spells returned", 1, result.size());
		assertEquals("Incorrect spell returned", spell, result.get(0));
		
	}

}
