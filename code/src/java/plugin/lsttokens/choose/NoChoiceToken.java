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
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.NoChoiceManager;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles no Choice.
 */
public class NoChoiceToken implements CDOMSecondaryToken<CDOMObject>,
		ChooseInformation<String>, PersistentChoiceActor<String>
{

	private static final ClassIdentity<String> STRING_INFO = BasicClassIdentity
		.getInstance(String.class);

	@Override
	public String getTokenName()
	{
		return "NOCHOICE";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			// No args - legal
			context.obj.put(obj, ObjectKey.CHOOSE_INFO, this);
			return ParseResult.SUCCESS;
		}
		return new ParseResult.Fail("CHOOSE:" + getTokenName()
			+ " will ignore arguments: " + value, context);
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> chooseString =
				context.getObjectContext()
					.getObject(cdo, ObjectKey.CHOOSE_INFO);
		if ((chooseString == null) || !chooseString.equals(this))
		{
			return null;
		}
		return new String[]{""};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public ClassIdentity<String> getClassIdentity()
	{
		return STRING_INFO;
	}

	@Override
	public ChoiceManagerList getChoiceManager(CDOMObject owner, int cost)
	{
		return new NoChoiceManager(owner, this, cost);
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_NONE;
	}

	@Override
	public String getLSTformat()
	{
		return "*NOCHOICE";
	}

	@Override
	public String getName()
	{
		return "No Choice";
	}

	@Override
	public Collection<String> getSet(PlayerCharacter pc)
	{
		return Collections.singletonList("NOCHOICE");
	}

	@Override
	public String getTitle()
	{
		return "No Choice Available";
	}

	@Override
	public String decodeChoice(String choice)
	{
		return choice;
	}

	@Override
	public String encodeChoice(String choice)
	{
		return choice;
	}

	@Override
	public PersistentChoiceActor<String> getChoiceActor()
	{
		return this;
	}

	@Override
	public void setChoiceActor(ChoiceActor<String> ca)
	{
		// ignore
	}

	@Override
	public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}

	@Override
	public void applyChoice(CDOMObject owner, String st, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, "");
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				applyChoice(owner, pc, ca);
			}
		}
	}

	private void applyChoice(CDOMObject owner, PlayerCharacter pc,
		ChooseSelectionActor<String> ca)
	{
		ca.applyChoice(owner, "", pc);
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner, String choice)
	{
		pc.removeAssoc(owner, AssociationListKey.CHOOSE_NOCHOICE, "");
		pc.removeAssociation(owner, "");
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, "", pc);
			}
		}
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		String choice)
	{
		pc.addAssoc(owner, AssociationListKey.CHOOSE_NOCHOICE, "");
		pc.addAssociation(owner, "");
	}

	@Override
	public List<String> getCurrentlySelected(CDOMObject owner,
		PlayerCharacter pc)
	{
		return pc.getAssocList(owner, AssociationListKey.CHOOSE_NOCHOICE);
	}

	@Override
	public CharSequence getDisplay(PlayerCharacter pc, CDOMObject owner)
	{
		StringBuilder sb = new StringBuilder();
		List<String> list =
				pc.getAssocList(owner, AssociationListKey.CHOOSE_NOCHOICE);
		int count = (list == null) ? 0 : list.size();
		if (count > 1)
		{
			sb.append(count);
			sb.append("x");
		}
		return sb;
	}

}
