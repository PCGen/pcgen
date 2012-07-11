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
 *
 * Created on March 20, 2007
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChoiceSet.AbilityChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * <code>AbilityToken</code> parses ADD:ABILITY entries.
 *
 * <p>
 * <b>Tag Name</b>: <code>ADD:ABILITY</code>|w|x|y|z,z<br />
 * <b>Variables Used (w)</b>: Count (Optional Number, Variable or Formula -
 * Number of choices granted).<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this
 * ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt> or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character, providing choices if these are required.</li>
 * <li>The Ability is added to the Ability Category specified.</li>
 * <li>Choices can be specified by including them in parenthesis after the
 * ability key name (whitespace is ignored).</li>
 *
 * Last Editor: $Author$ Last Edited: $Date: 2007-05-20 19:00:17 -0400
 * (Sun, 20 May 2007) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityToken extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<CategorizedAbilitySelection>
{

	private static final Class<CategorizedAbilitySelection> CAT_ABILITY_SELECTION_CLASS =
			CategorizedAbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	@Override
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
		return "ABILITY";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		if (isEmpty(value))
		{
			return new ParseResult.Fail("Value in " + getFullName()
					+ " may not be empty", context);
		}
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		String first = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail("Syntax of ADD:" + getTokenName()
					+ " requires 3 to 4 |: " + value, context);
		}
		String second = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail("Syntax of ADD:" + getTokenName()
					+ " requires a minimum of three | : " + value, context);
		}
		String third = sep.next();
		Formula count;
		if (sep.hasNext())
		{
			count = FormulaFactory.getFormulaFor(first);
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName()
						+ " was not valid: " + count.toString(), context);
			}
			if (count.isStatic() && count.resolve(null, "").doubleValue() <= 0)
			{
				return new ParseResult.Fail("Count in " + getFullName()
						+ " must be > 0", context);
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
			return new ParseResult.Fail("Syntax of ADD:" + getTokenName()
					+ " has max of four | when a count is not present: "
					+ value, context);
		}

		Category<Ability> category = context.ref
				.silentlyGetConstructedCDOMObject(ABILITY_CATEGORY_CLASS, first);
		if (category == null)
		{
			return new ParseResult.Fail(getFullName() + ": Invalid ability category: "
					+ first, context);
		}

		Nature nature = Nature.valueOf(second);
		if (nature == null)
		{
			return new ParseResult.Fail(getFullName() + ": Invalid ability nature: "
					+ second, context);
		}
		if (Nature.ANY.equals(nature))
		{
			return new ParseResult.Fail(getTokenName()
					+ " refers to ANY Ability Nature, cannot be used in "
					+ getTokenName() + ": " + value);
		}
		if (Nature.AUTOMATIC.equals(nature))
		{
			return new ParseResult.Fail(getTokenName()
					+ " refers to AUTOMATIC Ability Nature, cannot be used in "
					+ getTokenName() + ": " + value, context);
		}

		ParseResult pr = checkSeparatorsAndNonEmpty(',', third);
		if (!pr.passed())
		{
			return pr;
		}

		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		ParsingSeparator tok = new ParsingSeparator(third, ',');
		boolean allowStack = false;
		int dupChoices = 0;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, category);

		while (tok.hasNext())
		{
			CDOMReference<Ability> ab;
			String token = tok.next();
			if ("STACKS".equals(token))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(getFullName()
							+ " found second stacking specification in value: "
							+ value, context);
				}
				allowStack = true;
				continue;
			}
			else if (token.startsWith("STACKS="))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(getFullName()
							+ " found second stacking specification in value: "
							+ value, context);
				}
				allowStack = true;
				try
				{
					dupChoices = Integer.parseInt(token.substring(7));
				}
				catch (NumberFormatException nfe)
				{
					return new ParseResult.Fail("Invalid Stack number in "
							+ getFullName() + ": " + value, context);
				}
				if (dupChoices <= 0)
				{
					return new ParseResult.Fail("Invalid (less than 1) Stack number in "
							+ getFullName() + ": " + value, context);
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
				return new ParseResult.Fail("  Error was encountered while parsing "
						+ getTokenName() + ": " + value
						+ " had an invalid reference: " + token, context);
			}
			refs.add(ab);
		}

		if (refs.isEmpty())
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains no ability reference: " + value, context);
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs,
				nature);
		if (!rcs.getGroupingState().isValid())
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value, context);
		}
		AbilityChoiceSet cs = new AbilityChoiceSet(getTokenName(), rcs);
		StringBuilder title = new StringBuilder();
		if (!Nature.NORMAL.equals(nature))
		{
			title.append(nature.toString());
			title.append(' ');
		}
		title.append(category.getDisplayName());
		title.append(" Choice");
		cs.setTitle(title.toString());
		PersistentTransitionChoice<CategorizedAbilitySelection> tc =
				new ConcretePersistentTransitionChoice<CategorizedAbilitySelection>(
					cs, count);
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
			SelectableSet<?> cs = container.getChoices();
			if (getTokenName().equals(cs.getName())
					&& CAT_ABILITY_SELECTION_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityChoiceSet ascs = (AbilityChoiceSet) cs;
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				if (f.isStatic() && f.resolve(null, "").doubleValue() <= 0)
				{
					context.addWriteMessage("Count in " + getFullName()
							+ " must be > 0");
					return null;
				}
				if (!cs.getGroupingState().isValid())
				{
					context.addWriteMessage("Non-sensical " + getFullName()
							+ ": Contains ANY and a specific reference: "
							+ cs.getLSTformat());
					return null;
				}
				StringBuilder sb = new StringBuilder();
				if (!FormulaFactory.ONE.equals(f))
				{
					sb.append(f).append(Constants.PIPE);
				}
				sb.append(ascs.getCategory().getKeyName());
				sb.append(Constants.PIPE);
				sb.append(ascs.getNature());
				sb.append(Constants.PIPE);
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

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(CDOMObject owner,
		CategorizedAbilitySelection choice, PlayerCharacter pc)
	{
		Ability ab = choice.getAbility();
		String association = choice.getSelection();
		AbilityCategory cat = (AbilityCategory) choice.getAbilityCategory();
		boolean isVirtual = Nature.VIRTUAL.equals(choice.getNature());
		if (isVirtual)
		{
			Ability a = AbilityUtilities
					.addCloneOfAbilityToVirtualListwithChoices(pc,
							choice.getAbility(), choice.getSelection(), cat);
			pc.addAssoc(owner, AssociationListKey.ADDED_FEAT, a);
			pc.setAssoc(a, AssociationKey.NEEDS_SAVING, Boolean.TRUE);
		}
		else
		{
			pc.adjustAbilities(cat, ab.getSafe(ObjectKey.SELECTION_COST));
			AbilityUtilities.modAbility(pc, ab, association, cat);
		}
		pc.addAssociation(ab, association);
	}

	@Override
	public boolean allow(CategorizedAbilitySelection choice,
		PlayerCharacter pc, boolean allowStack)
	{
		boolean isVirtual = Nature.VIRTUAL.equals(choice.getNature());
		// Remove any already selected
		for (Ability a : pc.getAllAbilities())
		{
			if (a.getKeyName().equals(choice.getAbilityKey()))
			{
				if (!pc.canSelectAbility(a, isVirtual)
						|| !a.getSafe(ObjectKey.VISIBILITY).equals(
								Visibility.DEFAULT)
						|| !allowStack(a, allowStack)
						&& hasAssoc(pc.getAssociationList(a), choice))
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasAssoc(List<String> associationList,
		CategorizedAbilitySelection choice)
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

		Ability pcAbility = pc.getMatchingAbility(choice.getAbilityCategory(),
				choice.getAbility(), Nature.NORMAL);

		if (pcAbility != null)
		{
			// how many sub-choices to make
			double abilityCount = (pc.getSelectCorrectedAssociationCount(pcAbility) * pcAbility.getSafe(ObjectKey.SELECTION_COST).doubleValue());
			
			boolean result = false;
			// adjust the associated List
			if (pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				pc.removeAssociation(pcAbility, choice.getSelection());
				result = pc.hasAssociations(pcAbility); 
			}
			
			// if no sub choices made (i.e. all of them removed in Chooser box),
			// then remove the Feat
			boolean removed = false;
			
			if (!result)
			{
				removed = pc.removeRealAbility(choice.getAbilityCategory(),
						pcAbility);
				CDOMObjectUtilities.removeAdds(pcAbility, pc);
				CDOMObjectUtilities.restoreRemovals(pcAbility, pc);
			}
			
			if (choice.getAbilityCategory() == AbilityCategory.FEAT)
			{
				AbilityUtilities.adjustPool(pcAbility, pc, false, abilityCount,
						removed);
			}
			
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
