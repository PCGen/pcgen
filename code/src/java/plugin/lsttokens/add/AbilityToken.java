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
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.base.ChoiceSet.AbilityChoiceSet;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilityRef;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;
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
public class AbilityToken extends AbstractToken implements
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
		return "ABILITY";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		Formula count;
		int tokenCount = pipeTok.countTokens();
		if (tokenCount == 4)
		{
			String countString = pipeTok.nextToken();
			count = FormulaFactory.getFormulaFor(countString);
			if (count.isStatic() && count.resolve(null, "").doubleValue() <= 0)
			{
				Logging.log(Logging.LST_ERROR, "Count in " + getFullName()
								+ " must be > 0");
				return false;
			}
		}
		else if (tokenCount == 3)
		{
			count = FormulaFactory.ONE;
		}
		else
		{
			Logging.log(Logging.LST_ERROR, "Syntax of ADD:" + getTokenName()
							+ " requires three | when a count is not present: "
							+ value);
			return false;
		}

		String categoryKey = pipeTok.nextToken();
		Category<Ability> category = context.ref.getCategoryFor(ABILITY_CLASS,
				categoryKey);
		if (category == null)
		{
			Logging.log(Logging.LST_ERROR, getFullName() + ": Invalid ability category: "
					+ categoryKey);
			return false;
		}

		String natureKey = pipeTok.nextToken();
		Ability.Nature nature = Ability.Nature.valueOf(natureKey);
		if (nature == null)
		{
			Logging.log(Logging.LST_ERROR, getFullName() + ": Invalid ability nature: "
					+ natureKey);
			return false;
		}
		if (Ability.Nature.ANY.equals(nature))
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " refers to ANY Ability Nature, cannot be used in "
					+ getTokenName() + ": " + value);
			return false;
		}
		if (Ability.Nature.AUTOMATIC.equals(nature))
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " refers to AUTOMATIC Ability Nature, cannot be used in "
					+ getTokenName() + ": " + value);
			return false;
		}

		String items = pipeTok.nextToken();
		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			Logging.log(Logging.LST_ERROR, "!!");
			return false;
		}

		List<AbilityRef> refs = new ArrayList<AbilityRef>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		boolean allowStack = false;
		int dupChoices = 0;

		boolean foundAny = false;
		boolean foundOther = false;

		ReferenceManufacturer<Ability, ?> rm = context.ref.getManufacturer(
				ABILITY_CLASS, category);

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
			else
			{
				if (Constants.LST_ALL.equals(token))
				{
					foundAny = true;
					ab = context.ref.getCDOMAllReference(ABILITY_CLASS,
							category);
				}
				else
				{
					foundOther = true;
					ab = TokenUtilities.getTypeOrPrimitive(rm, token);
				}
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
				ar.setChoice(choices.get(0));
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

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs,
				nature);
		AbilityChoiceSet cs = new AbilityChoiceSet(getTokenName(), rcs);
		StringBuilder title = new StringBuilder();
		if (!Ability.Nature.NORMAL.equals(nature))
		{
			title.append(nature.toString());
			title.append(' ');
		}
		title.append(category.getDisplayName());
		title.append(" Choice");
		cs.setTitle(title.toString());
		PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
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
				AbilityChoiceSet ascs = (AbilityChoiceSet) cs;
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

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void applyChoice(CDOMObject owner, AbilitySelection choice,
			PlayerCharacter pc)
	{
		Ability ab = choice.getAbility();
		String association = choice.getSelection();
		AbilityCategory cat = (AbilityCategory) choice.getAbilityCategory();
		boolean isVirtual = Ability.Nature.VIRTUAL.equals(choice.getNature());
		AbilityUtilities
				.applyAbility(pc, null, cat, ab, association, isVirtual);
		pc.addAssociation(ab, association);
	}

	public boolean allow(AbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		boolean isVirtual = Ability.Nature.VIRTUAL.equals(choice.getNature());
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
		AbilityUtilities.modAbility(pc, null, choice.getAbility(), choice
				.getSelection(), false, (AbilityCategory) choice
				.getAbilityCategory());
	}

	public List<AbilitySelection> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return Collections.emptyList();
	}
}
