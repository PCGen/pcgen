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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>, PersistentChoiceActor<AbilitySelection>,
		DeferredToken<PCTemplate>
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
		context.getObjectContext().removeList(pct, ListKey.FEAT_TOKEN_LIST);

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;

		ReferenceManufacturer<Ability, ? extends CDOMSingleRef<Ability>> rm = context.ref
				.getManufacturer(ABILITY_CLASS, AbilityCategory.FEAT);
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities
						.getTypeOrPrimitive(context, rm, token);
				if (ability == null)
				{
					return false;
				}
				context.getObjectContext().addToList(pct,
						ListKey.FEAT_TOKEN_LIST, ability);
			}
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<CDOMReference<Ability>> changes = context.getObjectContext()
				.getListChanges(pct, ListKey.FEAT_TOKEN_LIST);
		Collection<CDOMReference<Ability>> added = changes.getAdded();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		String returnVal = null;
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			returnVal = Constants.LST_DOT_CLEAR;
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			returnVal = ReferenceUtilities.joinLstFormat(added, Constants.PIPE);
		}
		if (returnVal == null)
		{
			return null;
		}
		return new String[] { returnVal };
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
			return false;
		}
		for (String a : associationList)
		{
			if (choice.containsAssociation(a))
			{
				return true;
			}
		}
		return false;
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
		// No action required
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

	public boolean process(LoadContext context, PCTemplate pct)
	{
		List<CDOMReference<Ability>> list = pct
				.getListFor(ListKey.FEAT_TOKEN_LIST);
		if (list != null)
		{
			List<AbilityRef> refs = new ArrayList<AbilityRef>();
			for (CDOMReference<Ability> ability : list)
			{
				AbilityRef ar = new AbilityRef(ability);
				refs.add(ar);
				String token = ability.getLSTformat();
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
					ar.setChoice(choices.get(0));
				}
			}
			if (!refs.isEmpty())
			{
				AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(
						AbilityCategory.FEAT, refs, Ability.Nature.AUTOMATIC);
				ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
						getTokenName(), rcs);
				cs.setTitle("Feat Choice");
				PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
						cs, FormulaFactory.ONE);
				context.getObjectContext()
						.put(pct, ObjectKey.TEMPLATE_FEAT, tc);
				tc.setChoiceActor(this);
			}
		}
		return true;
	}

	public List<AbilitySelection> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return Collections.emptyList();
	}
}
