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
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Ability.Nature;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class VFeatToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<AbilitySelection>
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public boolean parse(PObject target, String value, int level)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
						+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		target.addAddList(level, getTokenName() + "(" + items + ")"
				+ countString);
		return true;
	}

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
		return "VFEAT";
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
		Nature nature = Ability.Nature.VIRTUAL;

		int pipeLoc = value.indexOf(Constants.PIPE);
		Formula count;
		String items;
		if (pipeLoc == -1)
		{
			count = Formula.ONE;
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
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		boolean allowStack = false;
		int dupChoices = 0;

		while (tok.hasMoreTokens())
		{
			CDOMReference<Ability> ab;
			String token = tok.nextToken();
			if ("STACKS".equals(token))
			{
				allowStack = true;
				continue;
			}
			else if (token.startsWith("STACKS="))
			{
				allowStack = true;
				try
				{
					dupChoices = Integer.parseInt(token.substring(7));
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Invalid Stack number in "
							+ getFullName() + ": " + value);
					return false;
				}
				continue;
			}
			else if (Constants.LST_ALL.equals(token))
			{
				ab = context.ref.getCDOMAllReference(ABILITY_CLASS, category);
			}
			else
			{
				ab = TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						category, token);
			}
			if (ab == null)
			{
				Logging.errorPrint("  Error was encountered while parsing "
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
					Logging.errorPrint("Invalid use of multiple items "
							+ "in parenthesis (comma prohibited) in "
							+ getFullName() + ": " + token);
					return false;
				}
				ar.addChoice(choices.get(0));
			}
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(refs, nature,
				allowStack, dupChoices);
		ChoiceSet<AbilitySelection> cs = new ChoiceSet<AbilitySelection>(
				"VFEAT", rcs);
		PersistentTransitionChoice<AbilitySelection> tc = new PersistentTransitionChoice<AbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.setTitle("Virtual Feat Selection");
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
		// TODO Auto-generated method stub
		return null;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void applyChoice(CDOMObject owner, AbilitySelection choice,
			PlayerCharacter pc)
	{
		String featName = choice.getAbilityKey();
		final Ability aFeat = AbilityUtilities.addVirtualAbility("FEAT",
				featName, pc.getDirectVirtualAbilities(AbilityCategory.FEAT),
				pc, null);
		pc.addAssoc(owner, AssociationListKey.ADDED_FEAT, aFeat);
		pc.setDirty(true);

		if (aFeat != null)
		{
			aFeat.setNeedsSaving(true);
		}
		else
		{
			Logging.errorPrint("Error:" + featName
					+ " not added, aPC.getFeatNamedInList() == NULL");
		}
	}

	public boolean allow(AbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		// Remove any already selected
		for (Ability a : pc.getAllAbilities())
		{
			if (a.getKeyName().equals(choice.getAbilityKey()))
			{
				Boolean multYes = a.getSafe(ObjectKey.MULTIPLE_ALLOWED);
				if (!multYes || multYes && !allowStack(a, allowStack)
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
//		String featName = choice.getAbilityKey();
//		Ability aFeat = pc.getAbilityKeyed(AbilityCategory.FEAT,
//				Ability.Nature.VIRTUAL, featName);
//		pc.addAssoc(owner, AssociationListKey.ADDED_ABILITY, aFeat);
	}
}
