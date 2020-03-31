/**
 * Copyright James Dempsey, 2011
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
package pcgen.gui2.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.util.ListFacade;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.TestHelper;
import plugin.lsttokens.choose.StringToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code CharacterAbilitiesTest} verifies the operation of the
 * CharacterAbilities class.
 */
public class CharacterAbilitiesTest extends AbstractCharacterTestCase
{

	private MockDataSetFacade dataset;
	private MockUIDelegate uiDelegate;
	private TodoManager todoManager;

	/**
	 * Test method for {@link pcgen.gui2.facade.CharacterAbilities#rebuildAbilityLists()}.
	 */
	@Test
	public final void testRebuildAbilityListsNoMult()
	{
		PlayerCharacter pc = getCharacter();
		CharacterAbilities ca = new CharacterAbilities(pc, uiDelegate, dataset, todoManager);
		ca.rebuildAbilityLists();
		ListFacade<AbilityCategory> categories = ca.getActiveAbilityCategories();
		assertNotNull("Categories should not be null", categories);
		assertTrue("Feat should be active", categories.containsElement(BuildUtilities.getFeatCat()));
		ListFacade<AbilityFacade> abilities = ca.getAbilities(BuildUtilities.getFeatCat());
		assertNotNull("Feat list should not be null", abilities);
		assertTrue("Feat list should be empty", abilities.isEmpty());
		
		// Add an entry - note rebuild is implicit
		Ability fencing = TestHelper.makeAbility("fencing", BuildUtilities.getFeatCat(), "sport");
		addAbility(BuildUtilities.getFeatCat(), fencing);
		abilities = ca.getAbilities(BuildUtilities.getFeatCat());
		assertEquals("Feat list should have one entry", 1, abilities.getSize());
		Ability abilityFromList = (Ability) abilities.getElementAt(0);
		assertEquals("Should have found fencing", fencing, abilityFromList);
	}

	/**
	 * Test method for {@link pcgen.gui2.facade.CharacterAbilities#rebuildAbilityLists()}.
	 */
	@Test
	public final void testRebuildAbilityListsMult()
	{
		PlayerCharacter pc = getCharacter();
		CharacterAbilities ca = new CharacterAbilities(pc, uiDelegate, dataset, todoManager);
		ca.rebuildAbilityLists();
		ListFacade<AbilityCategory> categories = ca.getActiveAbilityCategories();
		assertNotNull("Categories should not be null", categories);
		assertTrue("Feat should be active", categories.containsElement(BuildUtilities.getFeatCat()));
		ListFacade<AbilityFacade> abilities = ca.getAbilities(BuildUtilities.getFeatCat());
		assertNotNull("Feat list should not be null", abilities);
		assertTrue("Feat list should be empty", abilities.isEmpty());
		
		// Add an entry - note rebuild is implicit
		Ability reading = TestHelper.makeAbility("reading", BuildUtilities.getFeatCat(), "interest");
		reading.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), reading, "STRING|Magazines|Books");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		applyAbility(pc, BuildUtilities.getFeatCat(), reading, "Books");
		abilities = ca.getAbilities(BuildUtilities.getFeatCat());
		assertFalse("Feat list should not be empty", abilities.isEmpty());
		Ability abilityFromList = (Ability) abilities.getElementAt(0);
		assertEquals("Should have found reading", reading, abilityFromList);
		assertEquals("Feat list should have one entry", 1, abilities.getSize());

		// Now add the choice
		finalizeTest(abilityFromList, "Magazines", pc, BuildUtilities.getFeatCat());
		ca.rebuildAbilityLists();
		abilities = ca.getAbilities(BuildUtilities.getFeatCat());
		assertEquals("Feat list should have one entry", 1, abilities.getSize());
		abilityFromList = (Ability) abilities.getElementAt(0);
		assertEquals("Should have found reading", reading, abilityFromList);
		
	}
	@BeforeEach
	@Override
	public void setUp() throws Exception

	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGameAsProperty().get());
		dataset.addAbilityCategory(BuildUtilities.getFeatCat());
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
	}

}
