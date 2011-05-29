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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * New chooser plugin, handles Domain.
 */
public class DomainToken extends AbstractQualifiedChooseToken<Domain>
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public String getTokenName()
	{
		return "DOMAIN";
	}

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	public ParseResult parseTokenWithSeparator(LoadContext context,
			CDOMObject obj, String value)
	{
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(value, "|,", true);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			if ("QUALIFY".equals(tok))
			{
				Logging.deprecationPrint("CHOOSE:DOMAIN argument "
						+ "QUALIFY has been deprecated, "
						+ "please use QUALIFIED,!PC "
						+ "to achieve the same effect");
				tok = "QUALIFIED,!PC";
			}
			sb.append(tok);
		}
		return super.parseTokenWithSeparator(context, context.ref
				.getManufacturer(DOMAIN_CLASS), obj, sb.toString());
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	protected String getDefaultTitle()
	{
		return "Domain choice";
	}

	@Override
	protected AssociationListKey<Domain> getListKey()
	{
		return AssociationListKey.CHOOSE_DOMAIN;
	}

	public Domain decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				DOMAIN_CLASS, s);
	}

	public String encodeChoice(Domain choice)
	{
		return choice.getKeyName();
	}
}
