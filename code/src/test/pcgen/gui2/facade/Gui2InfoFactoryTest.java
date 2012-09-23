/**
 * Gui2InfoFactoryTest.java
 * Copyright James Dempsey, 2012
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
 * Created on 23/09/2012 12:00:00 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.TestHelper;

/**
 * The Class <code>Gui2InfoFactoryTest</code> verifies the operation of the 
 * Gui2InfoFactory class.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class Gui2InfoFactoryTest extends AbstractCharacterTestCase
{

	private MockDataSetFacade dataset;

	/**
	 * Test the getChoices method with text choices. 
	 */
	public void testGetChoices()
	{
		PlayerCharacter pc = getCharacter();
		Gui2InfoFactory ca = new Gui2InfoFactory(pc);

		Ability choiceAbility =
				TestHelper.makeAbility("Skill Focus", AbilityCategory.FEAT,
					"General");
		choiceAbility.put(ObjectKey.MULTIPLE_ALLOWED, true);
		Ability pcAbility =
				pc.addAbilityNeedCheck(AbilityCategory.FEAT, choiceAbility);
		AbilityUtilities.finaliseAbility(pcAbility, "Perception", pc,
			AbilityCategory.FEAT);
		assertEquals("Incorrect single choice", "Perception",
			ca.getChoices(pcAbility));

		AbilityUtilities.finaliseAbility(pcAbility, "Acrobatics", pc,
			AbilityCategory.FEAT);
		assertEquals("Incorrect multiple choice", "Perception,Acrobatics",
			ca.getChoices(pcAbility));
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
	}

}
