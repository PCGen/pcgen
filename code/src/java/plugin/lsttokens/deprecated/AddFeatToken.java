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
package plugin.lsttokens.deprecated;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class AddFeatToken extends AbstractNonEmptyToken<CDOMObject> implements CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "ADD";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String addType = sep.next();
		if (!"FEAT".equals(addType))
		{
			return new ParseResult.Fail("Incompatible with ADD:FEAT:" + value);
		}
		String activeValue = sep.next();
		Formula count;
		if (!sep.hasNext())
		{
			count = FormulaFactory.ONE;
		}
		else
		{
			count = FormulaFactory.getFormulaFor(activeValue);
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
			}
			if (count.isStatic() && count.resolveStatic().doubleValue() <= 0)
			{
				return new ParseResult.Fail("Count in ADD:FEAT must be > 0");
			}
			activeValue = sep.next();
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail("ADD:FEAT had too many pipe separated items: " + value);
		}
			if (!context.processToken(obj, "ADD", "ABILITY|" + count.toString() + "|FEAT|NORMAL|" + activeValue))
			{
				Logging.replayParsedMessages();
				return new ParseResult.Fail("Delegation Error from ADD:FEAT");
			}

		return ParseResult.SUCCESS;
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public int compatibilityLevel()
	{
		return 6;
	}

	@Override
	public int compatibilitySubLevel()
	{
		return 4;
	}

	@Override
	public int compatibilityPriority()
	{
		return 0;
	}
}
