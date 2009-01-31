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
package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilityRef;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Ability.Nature;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>, PersistentChoiceActor<AbilitySelection>
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(LoadContext context, PCTemplate pct, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Ability.Nature.AUTOMATIC;
		Formula count = FormulaFactory.ONE;

		List<AbilityRef> refs = new ArrayList<AbilityRef>();
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean allowStack = false;
		int dupChoices = 0;

		boolean first = true;

		while (tok.hasMoreTokens())
		{
			CDOMReference<Ability> ab;
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getObjectContext().removeList(pct, ListKey.TEMPLATE_FEAT);
			}
			else
			{
				ab = TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						category, token);
				if (ab == null)
				{
					Logging.log(Logging.LST_ERROR,
							"  Error was encountered while parsing "
									+ getTokenName() + ": " + value
									+ " had an invalid reference: " + token);
					return false;
				}
				AbilityRef ar = new AbilityRef(ab);
				refs.add(ar);

				if (token.indexOf('(') != -1)
				{
					List<String> choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					if (choices.size() != 1)
					{
						Logging.log(Logging.LST_ERROR,
								"Invalid use of multiple items "
										+ "in parenthesis"
										+ " (comma prohibited) in "
										+ getTokenName() + ": " + token);
						return false;
					}
					ar.addChoice(choices.get(0));
				}
			}
			first = false;
		}

		if (refs.isEmpty())
		{
			//Must have just been .CLEAR
			return true;
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs,
				nature, allowStack, dupChoices);
		ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
				getTokenName(), rcs);
		PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(pct, ListKey.TEMPLATE_FEAT, tc);
		tc.setTitle("Feat Choice");
		tc.allowStack(allowStack);
		if (dupChoices != 0)
		{
			tc.setStackLimit(dupChoices);
		}
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<PersistentTransitionChoice<?>> changes = context
				.getObjectContext().getListChanges(pct, ListKey.TEMPLATE_FEAT);
		if (changes == null || changes.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Collection<PersistentTransitionChoice<?>> added = changes.getAdded();
		List<String> addStrings = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			addStrings.add(Constants.LST_DOT_CLEAR);
		}
		if (added != null && !added.isEmpty())
		{
			for (PersistentTransitionChoice<?> container : added)
			{
				addStrings.add(container.getChoices().getLSTformat().replace(',', '|'));
			}
		}
		if (addStrings.size() == 0)
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}

	public void applyChoice(CDOMObject owner, AbilitySelection choice,
			PlayerCharacter pc)
	{
		double cost = choice.getAbility().getSafe(ObjectKey.SELECTION_COST)
				.doubleValue();
		if (cost > 0.0001)
		{
			pc.adjustFeats(cost);
		}
		String fullKey = choice.getFullAbilityKey();
		AbilityUtilities.modFeat(pc, null, fullKey, true, false);
		pc.addAssoc(owner, AssociationListKey.TEMPLATE_FEAT, fullKey);
	}

	public boolean allow(AbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		// Remove any already selected
		for (Ability a : pc.getAllAbilities())
		{
			if (AbilityCategory.FEAT.equals(a.getCDOMCategory()
					.getParentCategory()))
			{
				if (a.getKeyName().equals(choice.getAbilityKey()))
				{
					if (!pc.canSelectAbility(a, false)
							|| !a.getSafe(ObjectKey.VISIBILITY).equals(
									Visibility.DEFAULT)
							|| !allowStack(a, allowStack)
							&& hasAssoc(pc.getAssociationList(a), choice))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean hasAssoc(List<String> associationList,
			AbilitySelection choice)
	{
		if (associationList == null)
		{
			// Huh?
			return true;
		}
		for (String a : associationList)
		{
			if (choice.containsAssociation(a))
			{
				return false;
			}
		}
		return true;
	}

	private boolean allowStack(Ability a, boolean allowStack)
	{
		return a.getSafe(ObjectKey.STACKS) && allowStack;
	}

	public AbilitySelection decodeChoice(String s)
	{
		return AbilitySelection.getAbilitySelectionFromPersistentFormat(s);
	}

	public String encodeChoice(Object choice)
	{
		return ((AbilitySelection) choice).getPersistentFormat();
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			AbilitySelection choice)
	{
		//No action required
	}

	public Class<PCTemplate> getDeferredTokenClass()
	{
		return PCTemplate.class;
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			AbilitySelection choice)
	{
		AbilityUtilities.modFeat(pc, null, choice.getFullAbilityKey(), false,
				true);
	}
}
