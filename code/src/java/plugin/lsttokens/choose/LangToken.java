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
import pcgen.core.Language;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;

public class LangToken extends AbstractQualifiedChooseToken<Language>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	@Override
	public String getTokenName()
	{
		return "LANG";
	}

	@Override
	protected Class<Language> getChooseClass()
	{
		return LANGUAGE_CLASS;
	}

	@Override
	protected String getDefaultTitle()
	{
		return "Template choice";
	}

	public Language decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				LANGUAGE_CLASS, s);
	}

	public String encodeChoice(Object choice)
	{
		return ((Language) choice).getKeyName();
	}

	@Override
	protected AssociationListKey<Language> getListKey()
	{
		return AssociationListKey.CHOOSE_LANGAUGE;
	}
}
