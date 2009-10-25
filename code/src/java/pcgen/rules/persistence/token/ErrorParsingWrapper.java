/*
 * Copyright (c) 2009 Mark Jeffries <motorviper@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.rules.persistence.token;

import pcgen.rules.context.LoadContext;

/**
 * Temporary class to simplify the transition to using ParseResult/CDOMParseToken<T>.
 * See plugin.lsttokens.race.HandsToken and pcgen.rules.persistence.token.AbstractNonEmptyToken for use.
 * @author Mark
 */
public abstract class ErrorParsingWrapper<T> implements CDOMParserToken<T>
{
	static public <T> boolean parseToken(CDOMParserToken<T> token, LoadContext context, T obj, String value)
	{
		ParseResult pr = token.parseToken(context, obj, value);
		if (!pr.passed())
		{
			pr.addMessagesToLog();
		}
		return pr.passed();
	}

	public boolean parse(LoadContext context, T obj, String value)
	{
		return parseToken(this, context, obj, value);
	}
}
