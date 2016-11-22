/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.persistence.lst;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class SimplePrefixLoader<T extends Loadable> extends SimpleLoader<T>
{

	private final String prefixString;

	public SimplePrefixLoader(Class<T> cl, String prefix)
	{
		super(cl);
		if (Objects.requireNonNull(prefix).isEmpty())
		{
			throw new IllegalArgumentException("Prefix cannot be empty");
		}
		prefixString = prefix;
	}

	@Override
	protected String processFirstToken(LoadContext context, String token)
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: '"
					+ token + "' in " + getLoadClass().getSimpleName());
			return null;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: '" + token
					+ "' in " + getLoadClass().getSimpleName());
			return null;
		}
		else if (colonLoc == (token.length() - 1))
		{
			Logging.errorPrint("Invalid Token - "
					+ "ends with a colon (no value): '" + token + "' in "
					+ getLoadClass().getSimpleName());
			return null;
		}
		String key = token.substring(0, colonLoc);
		if (!prefixString.equals(key))
		{
			Logging.errorPrint("Invalid Token - expected '" + prefixString
					+ "' to be the first key in "
					+ getLoadClass().getSimpleName());
			return null;
		}
		String firstTokenValue = token.substring(colonLoc + 1);
		if (StringUtils.isNotBlank(firstTokenValue) && firstTokenValue.startsWith("in_"))
		{
			firstTokenValue = LanguageBundle.getString(firstTokenValue);
		}
		return super.processFirstToken(context, firstTokenValue);
	}
}
