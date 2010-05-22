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
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class SchoolsToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PrimitiveChoiceSet<String>,
		PersistentChoiceActor<String>
{

	@Override
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
		if (value == null)
		{
			// No args - deprecated
			Logging.deprecationPrint("CHOOSE:" + getTokenName()
					+ " with no argument has been deprecated");
			value = "ALL";
		}
		if (value.indexOf('[') != -1 || value.indexOf(']') != -1)
		{
			return new ParseResult.Fail(getParentToken() + ":" + getTokenName()
					+ " may not contain brackets: " + value);
		}
		int pipeLoc = value.indexOf('|');
		String activeValue;
		String title;
		if (pipeLoc == -1)
		{
			activeValue = value;
			title = getDefaultTitle();
		}
		else
		{
			String titleString = value.substring(pipeLoc + 1);
			if (titleString.startsWith("TITLE="))
			{
				title = titleString.substring(6);
				if (title.startsWith("\""))
				{
					title = title.substring(1, title.length() - 1);
				}
				activeValue = value.substring(0, pipeLoc);
			}
			else
			{
				activeValue = value;
				title = getDefaultTitle();
			}
		}
		PrimitiveChoiceSet<String> pcs;
		if (Constants.LST_ALL.equals(activeValue))
		{
			pcs = this;
		}
		else
		{
			if (hasIllegalSeparator('|', activeValue))
			{
				return ParseResult.INTERNAL_ERROR;
			}
			StringTokenizer st = new StringTokenizer(activeValue, "|");
			HashSet<String> set = new HashSet<String>();
			while (st.hasMoreTokens())
			{
				String tok = st.nextToken();
				if (Constants.LST_ALL.equals(tok))
				{
					return new ParseResult.Fail(
							"Error, Found ALL and individual items while parsing "
									+ getTokenName());
				}
				if (!set.add(tok))
				{
					return new ParseResult.Fail("Error, Found item: " + tok
							+ " twice while parsing " + getTokenName());
				}
			}
			if (set.isEmpty())
			{
				return new ParseResult.Fail("No items in set.");
			}
			pcs = new SimpleChoiceSet<String>(set, Constants.PIPE);
		}

		if (!pcs.getGroupingState().isValid())
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addErrorMessage("Invalid combination of objects was used in: "
					+ activeValue);
			cpr.addErrorMessage("  Check that ALL is not combined");
			cpr
					.addErrorMessage("  Check that a key is not joined with AND (,)");
			return cpr;
		}
		ChooseInformation<String> tc = new BasicChooseInformation<String>(
				getTokenName(), pcs);
		tc.setTitle(title);
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	private String getDefaultTitle()
	{
		return "School Choice";
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> tc = context.getObjectContext().getObject(cdo,
				ObjectKey.CHOOSE_INFO);
		if (tc == null)
		{
			return null;
		}
		if (!tc.getName().equals(getTokenName()))
		{
			// Don't unparse anything that isn't owned by this SecondaryToken
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(tc.getLSTformat());
		// TODO oops
		// String title = choices.getTitle();
		// if (!title.equals(getDefaultTitle()))
		// {
		// sb.append("|TITLE=");
		// sb.append(title);
		// }
		return new String[] { sb.toString() };
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
		return "ALL";
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
		pc.addAssociation(owner, encodeChoice(choice));
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

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		return ErrorParsingWrapper.parseToken(this, context, obj, value);
	}
}
