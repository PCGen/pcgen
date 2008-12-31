/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ModifyChoiceDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

/**
 * Deals with the MODIFYABILITYCHOICE token
 */
public class ModifyabilitychoiceToken extends AbstractToken implements
		CDOMPrimaryToken<Ability>, ChoiceActor<Ability>, DeprecatedToken
{

	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "MODIFYABILITYCHOICE";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		AbilityCategory category = AbilityCategory.FEAT;
		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();

			CDOMReference<Ability> ref = TokenUtilities.getTypeOrPrimitive(
					context, ABILITY_CLASS, category, token);
			if (ref == null)
			{
				return false;
			}

			refs.add(ref);
		}

		ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<Ability>(refs);
		ModifyChoiceDecorator gfd = new ModifyChoiceDecorator(rcs);
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>("MODIFYFEATCHOICE", gfd);
		TransitionChoice<Ability> tc = new TransitionChoice<Ability>(cs,
				FormulaFactory.ONE);
		tc.setTitle("Select a "
				+ SettingsHandler.getGame().getSingularTabName(Tab.ABILITIES)
				+ " to modify");
		tc.setRequired(false);
		context.getObjectContext().put(ability, ObjectKey.MODIFY_CHOICE, tc);
		tc.setChoiceActor(this);

		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		// This is intentional, so as to have this export as MODIFYFEATCHOICE
		return null;
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

	public void applyChoice(CDOMObject owner, Ability choice, PlayerCharacter pc)
	{
		final List<String> abilityList = new ArrayList<String>();
		final List<String> selectedList = new ArrayList<String>();

		// build a list of available choices and choices already made.
		ChooserUtilities.modChoices(choice, abilityList, selectedList, false,
				pc, true, SettingsHandler.getGame().getAbilityCategory(
						choice.getCategory()));

		final int currentSelections = selectedList.size();

		//
		// If nothing to choose, or nothing selected, then leave
		//
		if ((abilityList.size() == 0) || (currentSelections == 0))
		{
			return;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(true); // user is required to use all available
		// pool points
		chooser.setTotalChoicesAvail(selectedList.size()); // need to remove 1
		// to add another

		chooser.setTitle("Modify selections for " + choice);
		Globals.sortChooserLists(abilityList, selectedList);
		chooser.setAvailableList(abilityList);
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		final int selectedSize = chooser.getSelectedList().size();

		if (selectedSize != currentSelections)
		{
			// need to have the same number of selections when finished
			return;
		}

		// replace old selection(s) with new and update bonuses
		pc.removeAllAssociations(choice);

		for (int i = 0; i < selectedSize; ++i)
		{
			pc
					.addAssociation(choice, (String) chooser.getSelectedList()
							.get(i));
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return "You are using MODIFYABILITYCHOICE, "
				+ "which is a poorly formed token.  "
				+ "Please use MODIFYFEATCHOICE, "
				+ "as it has the same function";
	}

	public boolean allow(Ability choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}
}
