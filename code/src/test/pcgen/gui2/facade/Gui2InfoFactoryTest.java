/**
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
 */
package pcgen.gui2.facade;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.TestHelper;
import plugin.lsttokens.choose.StringToken;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * The Class <code>Gui2InfoFactoryTest</code> verifies the operation of the 
 * Gui2InfoFactory class.
 *
 * <br/>
 * 
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
				TestHelper.makeAbility("Skill Focus", BuildUtilities.getFeatCat(),
					"General");
		choiceAbility.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		ParseResult pr = st.parseToken(Globals.getContext(), choiceAbility, "SKILL|Perception|Acrobatics");
		assertTrue(pr.passed());
		Globals.getContext().commit();
		finalizeTest(choiceAbility, "Perception", pc,
			BuildUtilities.getFeatCat());
		assertEquals("Incorrect single choice", "Perception",
			ca.getChoices(choiceAbility));

		finalizeTest(choiceAbility, "Acrobatics", pc,
			BuildUtilities.getFeatCat());
		assertEquals("Incorrect multiple choice", "Acrobatics, Perception",
			ca.getChoices(choiceAbility));
	}
	
	/**
	 * Verify getHTMLInfo for a temporary bonus.
	 */
	public void testGetHTMLInfoTempBonus()
	{
		PlayerCharacter pc = getCharacter();
		Gui2InfoFactory infoFactory = new Gui2InfoFactory(pc);

		Ability tbAbility =
				TestHelper.makeAbility("Combat expertise",
					BuildUtilities.getFeatCat(), "General");
		tbAbility.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		final Description desc = new Description("CE Desc");
		tbAbility.addToListFor(ListKey.DESCRIPTION, desc);
		Globals.getContext().commit();
		addAbility(BuildUtilities.getFeatCat(), tbAbility);

		TempBonusFacadeImpl tbf = new TempBonusFacadeImpl(tbAbility);

		assertEquals("Unexpected temp bonus result",
			"<html><b><font size=+1>Combat expertise</font></b> (Ability)<br>"
				+ "<b>Desc:</b>&nbsp;CE Desc<br><b>Source:</b>&nbsp;</html>",
			infoFactory.getHTMLInfo(tbf));
	}	
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGame());
		dataset.addAbilityCategory(BuildUtilities.getFeatCat());
	}

}
