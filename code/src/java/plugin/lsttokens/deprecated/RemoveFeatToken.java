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
package plugin.lsttokens.deprecated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.choiceset.AbilityFromClassChoiceSet;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.choiceset.CompoundOrChoiceSet;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class RemoveFeatToken extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<CNAbilitySelection>, DeprecatedToken
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<CNAbilitySelection> CAT_ABILITY_SELECTION_CLASS = CNAbilitySelection.class;

	@Override
	public String getParentToken()
	{
		return "REMOVE";
	}

	private String getFullName()
	{
		return getParentToken() + Constants.COLON + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
	{
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.NORMAL;

		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

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
				return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
			}
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
			}
			if (count.isStatic() && count.resolveStatic().doubleValue() <= 0)
			{
				return new ParseResult.Fail("Count in " + getFullName() + " must be > 0");
			}
			activeValue = sep.next();
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail(getFullName() + " had too many pipe separated items: " + value);
		}
		ParseResult pr = checkSeparatorsAndNonEmpty(',', activeValue);
		if (!pr.passed())
		{
			return pr;
		}

		List<CDOMReference<Ability>> refs = new ArrayList<>();
		List<PrimitiveChoiceSet<CNAbilitySelection>> pcs = new ArrayList<>();
		ParsingSeparator tok = new ParsingSeparator(activeValue, ',');
		tok.addGroupingPair('[', ']');
		tok.addGroupingPair('(', ')');

		boolean foundAny = false;
		boolean foundOther = false;

		ReferenceManufacturer<Ability> rm = context.getReferenceContext().getManufacturerId(AbilityCategory.FEAT);

		while (tok.hasNext())
		{
			CDOMReference<Ability> ab = null;
			String token = tok.next();
			if ("CHOICE".equals(token) || Constants.LST_ANY.equals(token))
			{
				foundAny = true;
				ab = rm.getAllReference();
			}
			else if (token.startsWith(Constants.LST_CLASS_DOT) || token.startsWith(Constants.LST_CLASS_EQUAL))
			{
				String className = token.substring(6);
				if (className.isEmpty())
				{
					return new ParseResult.Fail(getTokenName() + " must have Class name after " + token);
				}
				CDOMSingleRef<PCClass> pcc = context.getReferenceContext().getCDOMReference(PCCLASS_CLASS, className);
				AbilityFromClassChoiceSet acs = new AbilityFromClassChoiceSet(pcc);
				pcs.add(acs);
			}
			else
			{
				foundOther = true;
				ab = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ab == null)
				{
					return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName() + ": " + value
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
			return new ParseResult.Fail(
				"Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
		}

		if (!refs.isEmpty())
		{
			AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(CDOMDirectSingleRef.getRef(category), refs, nature);
			pcs.add(rcs);
		}
		if (pcs.isEmpty())
		{
			return new ParseResult.Fail("Internal Error: " + getFullName() + " did not have any references: " + value);
		}
		PrimitiveChoiceSet<CNAbilitySelection> ascs;
		if (pcs.size() == 1)
		{
			ascs = pcs.get(0);
		}
		else
		{
			ascs = new CompoundOrChoiceSet<>(pcs, Constants.COMMA);
		}
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<>(getTokenName(), ascs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<>(cs, count);
		context.getObjectContext().addToList(obj, ListKey.REMOVE, tc);
		tc.allowStack(true);
		tc.setChoiceActor(this);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges =
				context.getObjectContext().getListChanges(obj, ListKey.REMOVE);
		Collection<PersistentTransitionChoice<?>> addedItems = grantChanges.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<>();
		for (TransitionChoice<?> container : addedItems)
		{
			SelectableSet<?> cs = container.getChoices();
			if (getTokenName().equals(cs.getName()) && CAT_ABILITY_SELECTION_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName() + " Count");
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
		return addStrings.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(CDOMObject owner, CNAbilitySelection choice, PlayerCharacter pc)
	{
		CNAbility cna = choice.getCNAbility();
		Ability anAbility = cna.getAbility();

		boolean result = false;
		// adjust the associated List
		if (anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			ChoiceManagerList cm = ChooserUtilities.getChoiceManager(cna, pc);
			remove(cm, pc, cna, choice.getSelection());
			result = pc.hasAssociations(cna);
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		if (!result)
		{
			pc.removeAbility(choice, UserSelection.getInstance(), UserSelection.getInstance());
			CDOMObjectUtilities.removeAdds(anAbility, pc);
			CDOMObjectUtilities.restoreRemovals(anAbility, pc);
		}

		pc.adjustMoveRates();

		double cost = cna.getAbility().getSafe(ObjectKey.SELECTION_COST).doubleValue();
		pc.adjustAbilities(AbilityCategory.FEAT, BigDecimal.valueOf(-cost));
	}

	private static <T> void remove(ChoiceManagerList<T> aMan, PlayerCharacter pc, ChooseDriver obj, String choice)
	{
		T sel = aMan.decodeChoice(choice);
		aMan.removeChoice(pc, obj, sel);
	}

	@Override
	public boolean allow(CNAbilitySelection choice, PlayerCharacter pc, boolean allowStack)
	{
		// Only allow those already selected
		for (CNAbility cna : pc.getPoolAbilities(AbilityCategory.FEAT, Nature.NORMAL))
		{
			if (cna.getAbilityKey().equals(choice.getAbilityKey()))
			{
				Boolean multYes = cna.getAbility().getSafe(ObjectKey.MULTIPLE_ALLOWED);
				if (!multYes || hasAssoc(pc.getAssociationList(cna), choice))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasAssoc(List<String> associationList, CNAbilitySelection choice)
	{
		if (associationList == null)
		{
			Logging.errorPrint("Didn't have any associations for Ability: " + choice.getAbilityKey());
			return false;
		}
		return associationList.stream().anyMatch(choice::containsAssociation);
	}

	@Override
	public CNAbilitySelection decodeChoice(LoadContext context, String s)
	{
		return CNAbilitySelection.getAbilitySelectionFromPersistentFormat(context, s);
	}

	@Override
	public String encodeChoice(CNAbilitySelection choice)
	{
		return choice.getPersistentFormat();
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, CNAbilitySelection choice)
	{
		// String featName = choice.getAbilityKey();
		// Ability aFeat = pc.getAbilityKeyed(AbilityCategory.FEAT,
		// Ability.Nature.NORMAL, featName);
		// pc.addAssoc(owner, AssociationListKey.ADDED_ABILITY, aFeat);
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner, CNAbilitySelection choice)
	{
		if (!pc.isImporting())
		{
			pc.getSpellList();
		}

		// See if our choice is not auto or virtual
		Ability anAbility =
				pc.getMatchingAbility(AbilityCategory.FEAT, choice.getCNAbility().getAbility(), Nature.NORMAL);

		if (anAbility != null)
		{
			pc.removeAbility(choice, owner, this);
			CDOMObjectUtilities.removeAdds(anAbility, pc);
			CDOMObjectUtilities.restoreRemovals(anAbility, pc);
			pc.adjustMoveRates();
		}
	}

	@Override
	public String getMessage(CDOMObject obj, String value)
	{
		return "Feat-based tokens have been deprecated - use ABILITY based functions";
	}
}
