/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Language;
import pcgen.core.QualifiedObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class LangautoLst extends AbstractTokenWithSeparator<CDOMObject> implements DeprecatedToken,
		CDOMPrimaryToken<CDOMObject> {

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	@Override
	public String getTokenName()
	{
		return "LANGAUTO";
	}

	@Override
	protected char separator()
	{
		return ',';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		Logging.deprecationPrint(getTokenName() + " has been deprecated, please use AUTO:LANG");
		boolean firstToken = true;
		boolean foundAny = false;
		boolean foundOther = false;

		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!firstToken)
				{
					return new ParseResult.Fail("Non-sensical situation was " + "encountered while parsing "
							+ getTokenName() + ": When used, .CLEAR must be the first argument");
				}
				context.getObjectContext().removeList(obj, ListKey.AUTO_LANGUAGES);
			} else
			{
				CDOMReference<Language> ref;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				} else
				{
					foundOther = true;
					ref = TokenUtilities.getTypeOrPrimitive(context, LANGUAGE_CLASS, tokText);
				}
				if (ref == null)
				{
					return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
				}
				context.getObjectContext().addToList(obj, ListKey.AUTO_LANGUAGES,
						new QualifiedObject<CDOMReference<Language>>(ref));
			}
			firstToken = false;
		}
		if (foundAny && foundOther)
		{
			return new ParseResult.Fail("Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: "
					+ value);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<QualifiedObject<CDOMReference<Language>>> changes = context.getObjectContext().getListChanges(obj,
				ListKey.AUTO_LANGUAGES);
		Collection<QualifiedObject<CDOMReference<Language>>> added = changes.getAdded();
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		if (changes.includesGlobalClear())
		{
			sb.append(Constants.LST_DOT_CLEAR);
			needComma = true;
		} else if (added == null || added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		if (added != null)
		{
			for (QualifiedObject<CDOMReference<Language>> lw : added)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				needComma = true;
				sb.append(lw.getRawObject().getLSTformat(false));
			}
		}
		return new String[] { sb.toString() };
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public String getMessage(CDOMObject obj, String value)
	{
		return getTokenName() + " has been deprecated, please use AUTO:LANG";
	}
}
