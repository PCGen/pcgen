/**
 * CharacterAbilitiesTest.java
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
 *
 * Created on 01/05/2011 9:46:34 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import org.junit.Test;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.util.TestHelper;

/**
 * The Class <code>CharacterAbilitiesTest</code> verifies the operation of the 
 * CharacterAbilities class.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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
		ListFacade<AbilityCategoryFacade> categories = ca.getActiveAbilityCategories();
		assertNotNull("Categories should not be null", categories);
		assertTrue("Feat should be active", categories.containsElement(AbilityCategory.FEAT));
		ListFacade<AbilityFacade> abilities = ca.getAbilities(AbilityCategory.FEAT);
		assertNotNull("Feat list should not be null", abilities);
		assertTrue("Feat list should be empty", abilities.isEmpty());
		
		// Add an entry - note rebuild is implicit
		Ability fencing = TestHelper.makeAbility("fencing", AbilityCategory.FEAT, "sport");
		pc.addAbilityNeedCheck(AbilityCategory.FEAT, fencing);
		abilities = ca.getAbilities(AbilityCategory.FEAT);
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
		ListFacade<AbilityCategoryFacade> categories = ca.getActiveAbilityCategories();
		assertNotNull("Categories should not be null", categories);
		assertTrue("Feat should be active", categories.containsElement(AbilityCategory.FEAT));
		ListFacade<AbilityFacade> abilities = ca.getAbilities(AbilityCategory.FEAT);
		assertNotNull("Feat list should not be null", abilities);
		assertTrue("Feat list should be empty", abilities.isEmpty());
		
		// Add an entry - note rebuild is implicit
		Ability reading = TestHelper.makeAbility("reading", AbilityCategory.FEAT, "interest");
		reading.put(ObjectKey.MULTIPLE_ALLOWED, true);
		pc.addAbilityNeedCheck(AbilityCategory.FEAT, reading);
		abilities = ca.getAbilities(AbilityCategory.FEAT);
		assertEquals("Feat list should have one entry", 1, abilities.getSize());
		Ability abilityFromList = (Ability) abilities.getElementAt(0);
		assertEquals("Should have found reading", reading, abilityFromList);

		// Now add the choice
		AbilityUtilities.finaliseAbility(abilityFromList, "Magazines", pc, AbilityCategory.FEAT);
		ca.rebuildAbilityLists();
		abilities = ca.getAbilities(AbilityCategory.FEAT);
		assertEquals("Feat list should have one entry", 1, abilities.getSize());
		abilityFromList = (Ability) abilities.getElementAt(0);
		assertEquals("Should have found reading", reading, abilityFromList);
		
	}
	
	/* (non-Javadoc)
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGame());
		dataset.addAbilityCategory(AbilityCategory.FEAT);
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
	}

}
