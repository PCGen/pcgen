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
package plugin.lsttokens.choose;

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

public class SchoolsToken extends ErrorParsingWrapper<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, PrimitiveChoiceSet<String>,
		PersistentChoiceActor<String>
{

	public String getTokenName()
	{
		return "SCHOOLS";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public ParseResult parseToken(LoadContext context, CDOMObject obj,
			String value)
	{
		if (value != null)
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addWarningMessage("CHOOSE:" + getTokenName()
					+ " will ignore arguments: " + value);
			return cpr;
		}
		// No args - legal
		ChoiceSet<String> cs = new ChoiceSet<String>(getTokenName(), this);
		cs.setTitle("School Choice");
		PersistentTransitionChoice<String> tc = new PersistentTransitionChoice<String>(
				cs, null);
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString = context.getObjectContext().getString(cdo,
				StringKey.CHOICE_STRING);
		if (chooseString == null)
		{
			return null;
		}
		String returnString;
		if (getTokenName().equals(chooseString))
		{
			returnString = "";
		}
		else
		{
			if (chooseString.indexOf(getTokenName() + '|') != 0)
			{
				return null;
			}
			returnString = chooseString.substring(getTokenName().length() + 1);
		}
		return new String[] { returnString };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public Class<? super String> getChoiceClass()
	{
		return String.class;
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_INTERSECTION;
	}

	public String getLSTformat(boolean useAny)
	{
		return getTokenName();
	}

	public Collection<String> getSet(PlayerCharacter pc)
	{
		return SettingsHandler.getGame().getUnmodifiableSchoolsList();
	}

	public String decodeChoice(String s)
	{
		return s;
	}

	public String encodeChoice(String choice)
	{
		return choice;
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner, String choice)
	{
		pc.removeAssoc(owner, AssociationListKey.CHOOSE_SCHOOL, choice);
		List<ChooseSelectionActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, choice, pc);
			}
		}
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			String choice)
	{
		pc.addAssoc(owner, AssociationListKey.CHOOSE_SCHOOL, choice);
	}

	public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}

	public void applyChoice(CDOMObject owner, String choice, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, choice);
		List<ChooseSelectionActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				applyChoice(owner, choice, pc, ca);
			}
		}
		pc.addAssociation(owner, encodeChoice(choice));
	}

	private void applyChoice(CDOMObject owner, String st, PlayerCharacter pc,
			ChooseSelectionActor<String> ca)
	{
		ca.applyChoice(owner, st, pc);
	}

	public List<String> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return pc.getAssocList(owner, AssociationListKey.CHOOSE_SCHOOL);
	}
}
