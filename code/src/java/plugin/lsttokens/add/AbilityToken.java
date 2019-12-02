/*
 * AbilityToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
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

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet.AbilityChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * {@code AbilityToken} parses ADD:ABILITY entries.
 *
 * <p>
 * <b>Tag Name</b>: {@code ADD:ABILITY}|w|x|y|z,z<br>
 * <b>Variables Used (w)</b>: Count (Optional Number, Variable or Formula -
 * Number of choices granted).<br>
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this
 * ability will be added to).<br>
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt> or <tt>VIRTUAL</tt>)<br>
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br>
 * <p>
 * <b>What it does:</b><br>
 * <ul>
 * <li>Adds an Ability to a character, providing choices if these are required.</li>
 * <li>The Ability is added to the Ability Category specified.</li>
 * <li>Choices can be specified by including them in parenthesis after the
 * ability key name (whitespace is ignored).</li>
 * </ul>
 * (Sun, 20 May 2007) $
 */
public class AbilityToken extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<CNAbilitySelection>
{

	private static final Class<CNAbilitySelection> CAT_ABILITY_SELECTION_CLASS = CNAbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	@Override
	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + Constants.COLON + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String first = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail("Syntax of ADD:" + getTokenName() + " requires 3 to 4 |: " + value);
		}
		String second = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(
				"Syntax of ADD:" + getTokenName() + " requires a minimum of three | : " + value);
		}
		String third = sep.next();
		Formula count;
		if (sep.hasNext())
		{
			count = FormulaFactory.getFormulaFor(first);
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
			}
			if (count.isStatic() && (count.resolveStatic().doubleValue() <= 0))
			{
				return new ParseResult.Fail("Count in " + getFullName() + " must be > 0");
			}
			first = second;
			second = third;
			third = sep.next();
		}
		else
		{
			count = FormulaFactory.ONE;
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail(
				"Syntax of ADD:" + getTokenName() + " has max of four | when a count is not present: " + value);
		}

		CDOMSingleRef<AbilityCategory> acRef =
				context.getReferenceContext().getCDOMReference(ABILITY_CATEGORY_CLASS, first);

		Nature nature = Nature.valueOf(second);
		if (nature == null)
		{
			return new ParseResult.Fail(getFullName() + ": Invalid ability nature: " + second);
		}
		if (Nature.ANY.equals(nature))
		{
			return new ParseResult.Fail(
				getTokenName() + " refers to ANY Ability Nature, cannot be used in " + getTokenName() + ": " + value);
		}
		if (Nature.AUTOMATIC.equals(nature))
		{
			return new ParseResult.Fail(getTokenName() + " refers to AUTOMATIC Ability Nature, cannot be used in "
				+ getTokenName() + ": " + value);
		}

		ParseResult pr = checkSeparatorsAndNonEmpty(',', third);
		if (!pr.passed())
		{
			return pr;
		}

		List<CDOMReference<Ability>> refs = new ArrayList<>();
		ParsingSeparator tok = new ParsingSeparator(third, ',');
		tok.addGroupingPair('[', ']');
		tok.addGroupingPair('(', ')');
		boolean allowStack = false;
		int dupChoices = 0;

		ReferenceManufacturer<Ability> rm =
				context.getReferenceContext().getManufacturerByFormatName("ABILITY=" + first, ABILITY_CLASS);
		if (rm == null)
		{
			return new ParseResult.Fail("Could not get Reference Manufacturer for Category: " + first);
		}

		while (tok.hasNext())
		{
			CDOMReference<Ability> ab;
			String token = tok.next();
			if ("STACKS".equals(token))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(
						getFullName() + " found second stacking specification in value: " + value);
				}
				allowStack = true;
				continue;
			}
			else if (token.startsWith("STACKS="))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(
						getFullName() + " found second stacking specification in value: " + value);
				}
				allowStack = true;
				try
				{
					dupChoices = Integer.parseInt(token.substring(7));
				}
				catch (NumberFormatException nfe)
				{
					return new ParseResult.Fail("Invalid Stack number in " + getFullName() + ": " + value);
				}
				if (dupChoices <= 0)
				{
					return new ParseResult.Fail(
						"Invalid (less than 1) Stack number in " + getFullName() + ": " + value);
				}
				continue;
			}
			else
			{
				if (Constants.LST_ALL.equals(token))
				{
					ab = rm.getAllReference();
				}
				else
				{
					ab = TokenUtilities.getTypeOrPrimitive(rm, token);
				}
			}
			if (ab == null)
			{
				return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName() + ": " + value
					+ " had an invalid reference: " + token);
			}
			refs.add(ab);
		}

		if (refs.isEmpty())
		{
			return new ParseResult.Fail("Non-sensical " + getFullName() + ": Contains no ability reference: " + value);
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(acRef, refs, nature);
		if (!rcs.getGroupingState().isValid())
		{
			return new ParseResult.Fail(
				"Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
		}
		AbilityChoiceSet cs = new AbilityChoiceSet(getTokenName(), rcs);
		StringBuilder title = new StringBuilder(50);
		if (!Nature.NORMAL.equals(nature))
		{
			title.append(nature.toString());
			title.append(' ');
		}
		title.append(first);
		title.append(" Choice");
		cs.setTitle(title.toString());
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<>(cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.allowStack(allowStack);
		if (dupChoices != 0)
		{
			tc.setStackLimit(dupChoices);
		}
		tc.setChoiceActor(this);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges =
				context.getObjectContext().getListChanges(obj, ListKey.ADD);
		Collection<PersistentTransitionChoice<?>> addedItems = grantChanges.getAdded();
		if ((addedItems == null) || addedItems.isEmpty())
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
				AbilityChoiceSet ascs = (AbilityChoiceSet) cs;
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName() + " Count");
					return null;
				}
				if (f.isStatic() && (f.resolveStatic().doubleValue() <= 0))
				{
					context.addWriteMessage("Count in " + getFullName() + " must be > 0");
					return null;
				}
				if (!cs.getGroupingState().isValid())
				{
					context.addWriteMessage("Non-sensical " + getFullName()
						+ ": Contains ANY and a specific reference: " + cs.getLSTformat());
					return null;
				}
				StringBuilder sb = new StringBuilder();
				if (!FormulaFactory.ONE.equals(f))
				{
					sb.append(f).append(Constants.PIPE);
				}
				sb.append(ascs.getCategory().getLSTformat(false));
				sb.append(Constants.PIPE);
				sb.append(ascs.getNature());
				sb.append(Constants.PIPE);
				if (container.allowsStacking())
				{
					sb.append("STACKS");
					Integer stackLimit = container.getStackLimit();
					if (stackLimit != null)
					{
						if (stackLimit <= 0)
						{
							context.addWriteMessage("Stack Limit in " + getFullName() + " must be > 0");
							return null;
						}
						sb.append(Constants.EQUALS);
						sb.append(stackLimit.intValue());
					}
					sb.append(Constants.COMMA);
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
		Ability ab = cna.getAbility();
		AbilityCategory cat = (AbilityCategory) cna.getAbilityCategory();
		boolean isVirtual = Nature.VIRTUAL.equals(cna.getNature());
		if (isVirtual)
		{
			pc.addSavedAbility(choice, UserSelection.getInstance(), UserSelection.getInstance());
		}
		else
		{
			pc.addAbility(choice, UserSelection.getInstance(), UserSelection.getInstance());
			pc.adjustAbilities(cat, ab.getSafe(ObjectKey.SELECTION_COST));
		}
	}

	@Override
	public boolean allow(CNAbilitySelection choice, PlayerCharacter pc, boolean allowStack)
	{
		CNAbility cna = choice.getCNAbility();
		Ability ability = cna.getAbility();
		if (!ability.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT))
		{
			return false;
		}
		boolean isVirtual = Nature.VIRTUAL.equals(cna.getNature());
		if (!isVirtual && !ability.qualifies(pc, ability))
		{
			return false;
		}
		String selection = choice.getSelection();
		// Avoid any already selected
		return !AbilityUtilities.alreadySelected(pc, ability, selection, allowStack);
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
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner, CNAbilitySelection choice)
	{
		CNAbility cna = choice.getCNAbility();
		Ability ab = cna.getAbility();
		AbilityCategory cat = (AbilityCategory) cna.getAbilityCategory();
		if (cna.getNature().equals(Nature.NORMAL))
		{
			pc.adjustAbilities(cat, ab.getSafe(ObjectKey.SELECTION_COST).negate());
			pc.removeAbility(choice, UserSelection.getInstance(), UserSelection.getInstance());
		}
		else
		{
			pc.removeSavedAbility(choice, UserSelection.getInstance(), UserSelection.getInstance());
		}
	}
}
