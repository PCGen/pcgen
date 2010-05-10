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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Globals;
import pcgen.core.Race;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;

public class RaceToken extends AbstractQualifiedChooseToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "RACE";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	protected Class<Race> getChooseClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDefaultTitle()
	{
		return "Race choice";
	}

	@Override
	protected AssociationListKey<Race> getListKey()
	{
		return AssociationListKey.CHOOSE_RACE;
	}

	public Race decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Race.class, s);
	}

	public String encodeChoice(Race choice)
	{
		return choice.getKeyName();
	}
}
