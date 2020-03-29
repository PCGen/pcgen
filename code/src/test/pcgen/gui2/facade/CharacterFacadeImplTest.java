/*
 * Copyright James Dempsey, 2013
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
import static org.junit.Assert.assertNotNull;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.EquipSet;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code CharacterFacadeImplTest} verifies the behaviour of
 * CharacterFacadeImpl.
 *
 * <br/>
 * 
 */

public class CharacterFacadeImplTest extends AbstractCharacterTestCase
{

	private MockDataSetFacade dataset;
	private MockUIDelegate uiDelegate;

	/**
	 * Check the default equipment set created by CharacterFacadeImpl. 
	 */
	@Test
	public void testDefaultEquipSet()
	{
		PlayerCharacter pc = new PlayerCharacter();
		CharacterFacadeImpl charFacade =
				new CharacterFacadeImpl(pc, uiDelegate, dataset);
		assertNotNull("Unable to create CharacterFacadeImpl", charFacade);
		EquipSet defaultEquipSet =
				pc.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH);
		assertNotNull("Unable to find default equip set", defaultEquipSet);
		assertEquals("Incorrect id of the default equip set",
			EquipSet.DEFAULT_SET_PATH, defaultEquipSet.getIdPath());
	}

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGameAsProperty().get());
		dataset.addAbilityCategory(BuildUtilities.getFeatCat());
		uiDelegate = new MockUIDelegate();
	}

}
