/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

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
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilityRef;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.Ability.Nature;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

public class FeatToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<AbilitySelection>
{

	private static final Class<AbilitySelection> ABILITY_SELECTION_CLASS = AbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, getFullName() + " may not have empty argument");
			return false;
		}
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Ability.Nature.NORMAL;

		int pipeLoc = value.indexOf(Constants.PIPE);
		Formula count;
		String items;
		if (pipeLoc == -1)
		{
			count = FormulaFactory.ONE;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			count = FormulaFactory.getFormulaFor(countString);
			if (count.isStatic() && count.resolve(null, "").doubleValue() <= 0)
			{
				Logging.log(Logging.LST_ERROR, "Count in " + getFullName()
								+ " must be > 0");
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<AbilityRef> refs = new ArrayList<AbilityRef>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		boolean allowStack = false;
		int dupChoices = 0;

		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			CDOMReference<Ability> ab;
			String token = tok.nextToken();
			if ("STACKS".equals(token))
			{
				if (allowStack)
				{
					Logging.log(Logging.LST_ERROR, getFullName()
							+ " found second stacking specification in value: "
							+ value);
					return false;
				}
				allowStack = true;
				continue;
			}
			else if (token.startsWith("STACKS="))
			{
				if (allowStack)
				{
					Logging.log(Logging.LST_ERROR, getFullName()
							+ " found second stacking specification in value: "
							+ value);
					return false;
				}
				allowStack = true;
				try
				{
					dupChoices = Integer.parseInt(token.substring(7));
				}
				catch (NumberFormatException nfe)
				{
					Logging.log(Logging.LST_ERROR, "Invalid Stack number in "
							+ getFullName() + ": " + value);
					return false;
				}
				if (dupChoices <= 0)
				{
					Logging.log(Logging.LST_ERROR, "Invalid (less than 1) Stack number in "
							+ getFullName() + ": " + value);
					return false;
				}
				continue;
			}
			else if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ab = context.ref.getCDOMAllReference(ABILITY_CLASS, category);
			}
			else
			{
				foundOther = true;
				ab = TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						category, token);
			}
			if (ab == null)
			{
				Logging.log(Logging.LST_ERROR, "  Error was encountered while parsing "
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
					Logging.log(Logging.LST_ERROR, "Invalid use of multiple items "
							+ "in parenthesis (comma prohibited) in "
							+ getFullName() + ": " + token);
					return false;
				}
				ar.addChoice(choices.get(0));
			}
		}

		if (foundAny && foundOther)
		{
			Logging.log(Logging.LST_ERROR, "Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		if (refs.isEmpty())
		{
			Logging.log(Logging.LST_ERROR, "Non-sensical " + getFullName()
					+ ": Contains no ability reference: " + value);
			return false;
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs, nature,
				allowStack, dupChoices);
		ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
				getTokenName(), rcs);
		PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.setTitle("Feat Choice");
		tc.allowStack(allowStack);
		if (dupChoices != 0)
		{
			tc.setStackLimit(dupChoices);
		}
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges = context
				.getObjectContext().getListChanges(obj, ListKey.ADD);
		Collection<PersistentTransitionChoice<?>> addedItems = grantChanges
				.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (TransitionChoice<?> container : addedItems)
		{
			ChoiceSet<?> cs = container.getChoices();
			if (getTokenName().equals(cs.getName())
					&& ABILITY_SELECTION_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				StringBuilder sb = new StringBuilder();
				if (!FormulaFactory.ONE.equals(f))
				{
					sb.append(f).append(Constants.PIPE);
				}
				if (container.allowsStacking())
				{
					sb.append("STACKS");
					int stackLimit = container.getStackLimit();
					if (stackLimit != 0)
					{
						sb.append(Constants.EQUALS);
						sb.append(container.getStackLimit());
					}
					sb.append(Constants.COMMA);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
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
		AbilityUtilities.modFeat(pc, null, choice.getFullAbilityKey(), true,
				false);
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
							|| (!allowStack(a, allowStack)
							&& hasAssoc(pc.getAssociationList(a), choice)))
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
		// String featName = choice.getAbilityKey();
		// Ability aFeat = pc.getAbilityKeyed(AbilityCategory.FEAT,
		// Ability.Nature.NORMAL, featName);
		// pc.addAssoc(owner, AssociationListKey.ADDED_ABILITY, aFeat);
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			AbilitySelection choice)
	{
		AbilityUtilities.modFeat(pc, null, choice.getFullAbilityKey(), false,
				true);
	}
}
