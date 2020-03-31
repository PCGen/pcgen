/*
 * Copyright 2007-2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles Strings.
 */
public class StringToken implements CDOMSecondaryToken<CDOMObject>, Chooser<String>
{

	@Override
	public String getTokenName()
	{
		return "STRING";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
	{
		if (value == null || value.isEmpty())
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " must have arguments");
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not contain , : " + value);
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not contain [] : " + value);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not end with | : " + value);
		}
		if (value.contains("||"))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments uses double separator || : " + value);
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		Set<String> set = new HashSet<>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			set.add(tokString);
		}
		SimpleChoiceSet<String> scs = new SimpleChoiceSet<>(set, Constants.PIPE);
		BasicChooseInformation<String> tc = new BasicChooseInformation<>(getTokenName(), scs, "STRING");
		tc.setTitle("Choose an Item");
		tc.setChoiceActor(this);
		context.getObjectContext().put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> tc = context.getObjectContext().getObject(cdo, ObjectKey.CHOOSE_INFO);
		if (tc == null)
		{
			return null;
		}
		if (!tc.getName().equals(getTokenName()))
		{
			// Don't unparse anything that isn't owned by this SecondaryToken
			return null;
		}
		// TODO oops
		// String title = choices.getTitle();
		// if (!title.equals(getDefaultTitle()))
		// {
		// sb.append("|TITLE=");
		// sb.append(title);
		// }
		return new String[]{tc.getLSTformat()
				// TODO oops
				// String title = choices.getTitle();
				// if (!title.equals(getDefaultTitle()))
				// {
				// sb.append("|TITLE=");
				// sb.append(title);
				// }
		};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public String decodeChoice(LoadContext context, String s)
	{
		return s;
	}

	@Override
	public String encodeChoice(String choice)
	{
		return choice;
	}

	@Override
	public void removeChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
	{
		pc.removeAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors = owner.getActors();
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, choice, pc);
			}
		}
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors = owner.getActors();
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				applyChoice(owner, choice, pc, ca);
			}
		}
	}

	@Override
	public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}

	@Override
	public void applyChoice(ChooseDriver owner, String choice, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, choice);
	}

	private void applyChoice(ChooseDriver owner, String st, PlayerCharacter pc, ChooseSelectionActor<String> ca)
	{
		ca.applyChoice(owner, st, pc);
	}

	@Override
	public List<String> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	protected AssociationListKey<String> getListKey()
	{
		return AssociationListKey.getKeyFor(String.class, "CHOOSE*STRING");
	}
}
