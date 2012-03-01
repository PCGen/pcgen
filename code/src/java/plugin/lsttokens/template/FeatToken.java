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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilitySelection;
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
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractTokenWithSeparator<PCTemplate> implements
		CDOMPrimaryToken<PCTemplate>, PersistentChoiceActor<AbilitySelection>,
		DeferredToken<PCTemplate>
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate pct, String value)
	{
		context.getObjectContext().removeList(pct, ListKey.FEAT_TOKEN_LIST);

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
				}
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ability == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				ability.setRequiresTarget(true);
				context.getObjectContext().addToList(pct,
						ListKey.FEAT_TOKEN_LIST, ability);
			}
			first = false;
		}
		return ParseResult.SUCCESS;
	}

	@Override
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

	@Override
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}

	@Override
	public void applyChoice(CDOMObject owner, AbilitySelection choice,
			PlayerCharacter pc)
	{
		double cost = choice.getAbility().getSafe(ObjectKey.SELECTION_COST)
				.doubleValue();
		if (cost > 0.0001)
		{
			pc.adjustFeats(cost);
		}
		AbilityUtilities.modAbility(pc, choice.getAbility(), choice
		.getSelection(), AbilityCategory.FEAT);
		pc.addAssoc(owner, AssociationListKey.TEMPLATE_FEAT, choice);
	}

	@Override
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

	@Override
	public AbilitySelection decodeChoice(String s)
	{
		return AbilitySelection.getAbilitySelectionFromPersistentFormat(s);
	}

	@Override
	public String encodeChoice(AbilitySelection choice)
	{
		return choice.getPersistentFormat();
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			AbilitySelection choice)
	{
		// No action required
	}

	@Override
	public Class<PCTemplate> getDeferredTokenClass()
	{
		return PCTemplate.class;
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			AbilitySelection choice)
	{
		if (!pc.isImporting())
		{
			pc.getSpellList();
		}
		
		// See if our choice is not auto or virtual
		Ability anAbility = pc.getMatchingAbility(AbilityCategory.FEAT, choice
				.getAbility(), Nature.NORMAL);

		if (anAbility != null)
		{
			pc.removeRealAbility(AbilityCategory.FEAT, anAbility);
			CDOMObjectUtilities.removeAdds(anAbility, pc);
			CDOMObjectUtilities.restoreRemovals(anAbility, pc);
			pc.adjustMoveRates();
		}
	}

	@Override
	public boolean process(LoadContext context, PCTemplate pct)
	{
		List<CDOMReference<Ability>> list = pct
				.getListFor(ListKey.FEAT_TOKEN_LIST);
		if (list != null && !list.isEmpty())
		{
			AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(
					AbilityCategory.FEAT, list, Nature.AUTOMATIC);
			ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
					getTokenName(), rcs);
			cs.setTitle("Feat Choice");
			PersistentTransitionChoice<AbilitySelection> tc = new ConcretePersistentTransitionChoice<AbilitySelection>(
					cs, FormulaFactory.ONE);
			context.getObjectContext().put(pct, ObjectKey.TEMPLATE_FEAT, tc);
			tc.setChoiceActor(this);
		}
		return true;
	}

	@Override
	public List<AbilitySelection> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return Collections.emptyList();
	}
}
