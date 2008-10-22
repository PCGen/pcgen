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
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSkillChoiceActor;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.ObjectMatchingReference;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class ClassSkillsToken extends AbstractToken implements
		CDOMSecondaryToken<PCClass>
{
	@Override
	public String getTokenName()
	{
		return "CLASSSKILLS";
	}

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getFullName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in " + getFullName()
							+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in " + getFullName() + ": "
						+ countString);
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMReference<Skill>> refs = new ArrayList<CDOMReference<Skill>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		CDOMGroupRef<Skill> allRef = context.ref
				.getCDOMAllReference(SKILL_CLASS);
		Integer autoRank = null;
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_ALL.equals(tokText))
			{
				refs.add(allRef);
			}
			else if (Constants.LST_UNTRAINED.equals(tokText))
			{
				ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<Skill, Boolean>(
						SKILL_CLASS, allRef, ObjectKey.USE_UNTRAINED,
						Boolean.TRUE);
				omr.returnIncludesNulls(true);
				refs.add(omr);
			}
			else if (Constants.LST_TRAINED.equals(tokText))
			{
				refs.add(new ObjectMatchingReference<Skill, Boolean>(
						SKILL_CLASS, allRef, ObjectKey.USE_UNTRAINED,
						Boolean.FALSE));
			}
			else if (Constants.LST_EXCLUSIVE.equals(tokText))
			{
				refs
						.add(new ObjectMatchingReference<Skill, Boolean>(
								SKILL_CLASS, allRef, ObjectKey.EXCLUSIVE,
								Boolean.TRUE));
			}
			else if (Constants.LST_NONEXCLUSIVE.equals(tokText)
					|| Constants.LST_CROSSCLASS.equals(tokText))
			{
				ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<Skill, Boolean>(
						SKILL_CLASS, allRef, ObjectKey.EXCLUSIVE, Boolean.FALSE);
				omr.returnIncludesNulls(true);
				refs.add(omr);
			}
			else if (tokText.startsWith("AUTORANK="))
			{
				if (autoRank != null)
				{
					Logging.errorPrint("Cannot have two AUTORANK= items in "
							+ getFullName() + ": " + value);
					return false;
				}
				String rankString = tokText.substring(9);
				autoRank = Integer.decode(rankString);
			}
			else
			{
				CDOMReference<Skill> skref = TokenUtilities.getTypeOrPrimitive(
						context, SKILL_CLASS, tokText);
				if (skref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getFullName() + ": " + value
							+ " had an invalid reference: " + tokText);
					return false;
				}
				refs.add(skref);
			}
		}

		ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<Skill>(refs);
		ChoiceSet<Skill> cs = new ChoiceSet<Skill>(getFullName(), rcs);
		TransitionChoice<Skill> tc = new TransitionChoice<Skill>(cs,
				FormulaFactory.getFormulaFor(count));
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		ClassSkillChoiceActor actor = new ClassSkillChoiceActor(obj, autoRank);
		tc.setChoiceActor(actor);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		Changes<TransitionChoice<?>> grantChanges = context.getObjectContext()
				.getListChanges(obj, ListKey.ADD);
		Collection<TransitionChoice<?>> addedItems = grantChanges.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (TransitionChoice<?> container : addedItems)
		{
			ChoiceSet<?> cs = container.getChoices();
			if (getFullName().equals(cs.getName())
					&& SKILL_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
