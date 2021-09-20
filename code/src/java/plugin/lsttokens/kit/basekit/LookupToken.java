/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.basekit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.NamedFormula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.kit.KitGear;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * LOOKUP token for base kits
 */
public class LookupToken extends AbstractToken implements CDOMPrimaryToken<KitGear>
{

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "LOOKUP";
	}

	@Override
	public Class<KitGear> getTokenClass()
	{
		return KitGear.class;
	}

	@Override
	public ParseResult parseToken(LoadContext context, KitGear kitGear, String value)
	{
		ParsingSeparator sep = new ParsingSeparator(value, ',');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String first = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail("Token must contain separator ','");
		}
		String second = sep.next();
		if (sep.hasNext())
		{
			return new ParseResult.Fail("Token cannot have more than one separator ','");
		}
		Formula formula = FormulaFactory.getFormulaFor(second);
		if (!formula.isValid())
		{
			return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
		}
		kitGear.loadLookup(first, formula);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitGear kitGear)
	{
		Collection<NamedFormula> lookups = kitGear.getLookups();
		if (lookups == null)
		{
			return null;
		}
		List<String> list = new ArrayList<>();
		for (NamedFormula nf : lookups)
		{
			list.add(nf.getName() + ',' + nf.getFormula().toString());
		}
		return list.toArray(new String[0]);
	}
}
