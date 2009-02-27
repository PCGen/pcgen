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
package plugin.lsttokens.remove;

import java.math.BigDecimal;
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
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.AbilityFromClassChoiceSet;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.choiceset.CompoundOrChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilityRef;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Ability.Nature;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class FeatToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<AbilitySelection>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<AbilitySelection> ABILITY_SELECTION_CLASS = AbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getParentToken()
	{
		return "REMOVE";
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
			Logging.errorPrint(getFullName() + " may not have empty argument");
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
				Logging
						.errorPrint("Count in " + getFullName()
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
		List<PrimitiveChoiceSet<AbilitySelection>> pcs = new ArrayList<PrimitiveChoiceSet<AbilitySelection>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			CDOMReference<Ability> ab = null;
			String token = tok.nextToken();
			if (Constants.LST_CHOICE.equals(token)
					|| Constants.LST_ANY.equals(token))
			{
				foundAny = true;
				ab = context.ref.getCDOMAllReference(ABILITY_CLASS, category);
			}
			else if (token.startsWith("CLASS.") || token.startsWith("CLASS="))
			{
				String className = token.substring(6);
				if (className.length() == 0)
				{
					Logging.errorPrint(getTokenName()
							+ " must have Class name after " + token);
					return false;
				}
				CDOMSingleRef<PCClass> pcc = context.ref.getCDOMReference(
						PCCLASS_CLASS, className);
				AbilityFromClassChoiceSet acs = new AbilityFromClassChoiceSet(
						pcc);
				pcs.add(acs);
			}
			else
			{
				foundOther = true;
				ab = TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						category, token);
				if (ab == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName() + ": " + value
							+ " had an invalid reference: " + token);
					return false;
				}
			}
			if (ab != null)
			{
				AbilityRef ar = new AbilityRef(ab);
				refs.add(ar);
				if (token.indexOf('(') != -1)
				{
					List<String> choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					if (choices.size() != 1)
					{
						Logging.errorPrint("Invalid use of multiple items "
								+ "in parenthesis (comma prohibited) in "
								+ getFullName() + ": " + token);
						return false;
					}
					ar.setChoice(choices.get(0));
				}
			}
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		if (!refs.isEmpty())
		{
			AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs,
					nature);
			pcs.add(rcs);
		}
		if (pcs.isEmpty())
		{
			Logging.errorPrint("Internal Error: " + getFullName()
					+ " did not have any references: " + value);
			return false;
		}
		PrimitiveChoiceSet<AbilitySelection> ascs;
		if (pcs.size() == 1)
		{
			ascs = pcs.get(0);
		}
		else
		{
			ascs = new CompoundOrChoiceSet<AbilitySelection>(pcs);
		}
		ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
				getTokenName(), ascs, true);
		PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.REMOVE, tc);
		tc.setTitle("Select for removal");
		tc.allowStack(true);
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges = context
				.getObjectContext().getListChanges(obj, ListKey.REMOVE);
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
		AbilityUtilities.modFeat(pc, null, choice.getFullAbilityKey(), false,
				false);
		double cost = choice.getAbility().getSafe(ObjectKey.SELECTION_COST)
				.doubleValue();
		pc.adjustAbilities(AbilityCategory.FEAT, BigDecimal.valueOf(-cost));
	}

	public boolean allow(AbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		// Only allow those already selected
		for (Ability a : pc.getRealFeatList())
		{
			if (a.getKeyName().equals(choice.getAbilityKey()))
			{
				Boolean multYes = a.getSafe(ObjectKey.MULTIPLE_ALLOWED);
				if (!multYes || multYes
						&& hasAssoc(pc.getAssociationList(a), choice))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasAssoc(List<String> associationList,
			AbilitySelection choice)
	{
		if (associationList == null)
		{
			Logging.errorPrint("Didn't have any associations for Ability: "
					+ choice.getAbilityKey());
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
