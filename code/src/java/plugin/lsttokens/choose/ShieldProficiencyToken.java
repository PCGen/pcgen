/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Globals;
import pcgen.core.ShieldProf;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;

public class ShieldProficiencyToken extends
		AbstractQualifiedChooseToken<ShieldProf>
{

	private static final Class<ShieldProf> SHIELDPROF_CLASS = ShieldProf.class;

	@Override
	public String getTokenName()
	{
		return "SHIELDPROFICIENCY";
	}

	@Override
	protected Class<ShieldProf> getChooseClass()
	{
		return SHIELDPROF_CLASS;
	}

	@Override
	protected String getDefaultTitle()
	{
		return "Shield Proficiency choice";
	}

	public ShieldProf decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				SHIELDPROF_CLASS, s);
	}

	public String encodeChoice(ShieldProf choice)
	{
		return choice.getKeyName();
	}

	@Override
	protected AssociationListKey<ShieldProf> getListKey()
	{
		return AssociationListKey.CHOOSE_SHIELDPROFICIENCY;
	}
}
