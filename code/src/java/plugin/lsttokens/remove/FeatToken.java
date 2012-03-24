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
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.AbilityFromClassChoiceSet;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.choiceset.CompoundOrChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class FeatToken extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<CategorizedAbilitySelection>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<CategorizedAbilitySelection> CAT_ABILITY_SELECTION_CLASS = CategorizedAbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
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

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.NORMAL;

		ParsingSeparator sep = new ParsingSeparator(value, '|');
		String activeValue = sep.next();
		Formula count;
		if (!sep.hasNext())
		{
			count = FormulaFactory.ONE;
		}
		else
		{
			count = FormulaFactory.getFormulaFor(activeValue);
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName()
						+ " was not valid: " + count.toString());
			}
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName()
						+ " was not valid: " + count.toString());
			}
			if (count.isStatic() && count.resolve(null, "").doubleValue() <= 0)
			{
				return new ParseResult.Fail("Count in " + getFullName()
								+ " must be > 0");
			}
			activeValue = sep.next();
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail(getFullName()
					+ " had too many pipe separated items: " + value);
		}
		if (isEmpty(activeValue) || hasIllegalSeparator(',', activeValue))
		{
			return ParseResult.INTERNAL_ERROR;
		}

		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		List<PrimitiveChoiceSet<CategorizedAbilitySelection>> pcs =
				new ArrayList<PrimitiveChoiceSet<CategorizedAbilitySelection>>();
		ParsingSeparator tok = new ParsingSeparator(activeValue, ',');

		boolean foundAny = false;
		boolean foundOther = false;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (tok.hasNext())
		{
			CDOMReference<Ability> ab = null;
			String token = tok.next();
			if (Constants.LST_CHOICE.equals(token)
					|| Constants.LST_ANY.equals(token))
			{
				foundAny = true;
				ab = rm.getAllReference();
			}
			else if (token.startsWith("CLASS.") || token.startsWith("CLASS="))
			{
				String className = token.substring(6);
				if (className.length() == 0)
				{
					return new ParseResult.Fail(getTokenName()
							+ " must have Class name after " + token);
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
				ab = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ab == null)
				{
					return new ParseResult.Fail("  Error was encountered while parsing "
							+ getTokenName() + ": " + value
							+ " had an invalid reference: " + token);
				}
			}
			if (ab != null)
			{
				refs.add(ab);
			}
		}

		if (foundAny && foundOther)
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
		}

		if (!refs.isEmpty())
		{
			AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs,
					nature);
			pcs.add(rcs);
		}
		if (pcs.isEmpty())
		{
			return new ParseResult.Fail("Internal Error: " + getFullName()
					+ " did not have any references: " + value);
		}
		PrimitiveChoiceSet<CategorizedAbilitySelection> ascs;
		if (pcs.size() == 1)
		{
			ascs = pcs.get(0);
		}
		else
		{
			ascs = new CompoundOrChoiceSet<CategorizedAbilitySelection>(pcs, Constants.COMMA);
		}
		ChoiceSet<CategorizedAbilitySelection> cs = new ChoiceSet<CategorizedAbilitySelection>(
				getTokenName(), ascs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CategorizedAbilitySelection> tc =
				new ConcretePersistentTransitionChoice<CategorizedAbilitySelection>(
					cs, count);
		context.getObjectContext().addToList(obj, ListKey.REMOVE, tc);
		tc.allowStack(true);
		tc.setChoiceActor(this);
		return ParseResult.SUCCESS;
	}

	@Override
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
			SelectableSet<?> cs = container.getChoices();
			if (getTokenName().equals(cs.getName())
					&& CAT_ABILITY_SELECTION_CLASS.equals(cs.getChoiceClass()))
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

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(CDOMObject owner, CategorizedAbilitySelection choice,
			PlayerCharacter pc)
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
			// how many sub-choices to make
			double abilityCount = (pc.getSelectCorrectedAssociationCount(anAbility) * anAbility.getSafe(ObjectKey.SELECTION_COST).doubleValue());
			
			boolean result = false;
			// adjust the associated List
			if (anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				pc.removeAssociation(anAbility, choice.getSelection());
				result = pc.hasAssociations(anAbility); 
			}
			
			boolean removed = false;

			// if no sub choices made (i.e. all of them removed in Chooser box),
			// then remove the Feat
			if (!result)
			{
				removed = pc.removeRealAbility(AbilityCategory.FEAT, anAbility);
				CDOMObjectUtilities.removeAdds(anAbility, pc);
				CDOMObjectUtilities.restoreRemovals(anAbility, pc);
			}
			
			AbilityUtilities.adjustPool(anAbility, pc, false, abilityCount, removed);
			pc.adjustMoveRates();
		}
		double cost = choice.getAbility().getSafe(ObjectKey.SELECTION_COST)
				.doubleValue();
		pc.adjustAbilities(AbilityCategory.FEAT, BigDecimal.valueOf(-cost));
	}

	@Override
	public boolean allow(CategorizedAbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		// Only allow those already selected
		for (Ability a : pc.getAbilityList(AbilityCategory.FEAT, Nature.NORMAL))
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
		CategorizedAbilitySelection choice)
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

	@Override
	public CategorizedAbilitySelection decodeChoice(String s)
	{
		return CategorizedAbilitySelection.getAbilitySelectionFromPersistentFormat(s);
	}

	@Override
	public String encodeChoice(CategorizedAbilitySelection choice)
	{
		return choice.getPersistentFormat();
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		CategorizedAbilitySelection choice)
	{
		// String featName = choice.getAbilityKey();
		// Ability aFeat = pc.getAbilityKeyed(AbilityCategory.FEAT,
		// Ability.Nature.NORMAL, featName);
		// pc.addAssoc(owner, AssociationListKey.ADDED_ABILITY, aFeat);
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
		CategorizedAbilitySelection choice)
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
	public List<CategorizedAbilitySelection> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return Collections.emptyList();
	}
}
