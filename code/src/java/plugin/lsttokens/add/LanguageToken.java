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
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class LanguageToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, ChoiceActor<Language>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

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
		return "LANGUAGE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getFullName() + " may not have empty argument");
			return false;
		}
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
					.errorPrint("Count in " + getFullName() + " must be > 0");
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMReference<Language>> refs =
				new ArrayList<CDOMReference<Language>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		boolean foundAny = false;
		boolean foundOther = false;
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Language> lang;
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
			}
			else
			{
				foundOther = true;
				lang =
						TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, tokText);
			}
			if (lang == null)
			{
				Logging.errorPrint("  Error was encountered while parsing "
					+ getFullName() + ": " + value
					+ " had an invalid reference: " + tokText);
				return false;
			}
			refs.add(lang);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getFullName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<Language> rcs =
				new ReferenceChoiceSet<Language>(refs);
		ChoiceSet<Language> cs = new ChoiceSet<Language>("ADD", rcs);
		TransitionChoice<Language> tc =
				new TransitionChoice<Language>(cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.setTitle("Language Choice");
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<TransitionChoice<?>> grantChanges =
				context.getObjectContext().getListChanges(obj, ListKey.ADD);
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
			if (LANGUAGE_CLASS.equals(cs.getChoiceClass()))
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

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void applyChoice(Language choice, PlayerCharacter pc)
	{
		pc.addLanguageKeyed(choice.getKeyName());
	}
}
