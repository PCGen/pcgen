/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.template;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SIZE Token
 */
public class SizeToken extends AbstractNonEmptyToken<PCTemplate> implements
		CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "SIZE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		PCTemplate template, String value)
	{
		SizeAdjustment size = context.ref.getAbbreviatedObject(
				SizeAdjustment.class, value);
		Formula sizeFormula;
		if (size == null)
		{
			sizeFormula = FormulaFactory.getFormulaFor(value);
		}
		else
		{
			sizeFormula = new FixedSizeFormula(size);
		}
		if (!sizeFormula.isValid())
		{
			return new ParseResult.Fail("Size in " + getTokenName()
					+ " was not valid: " + sizeFormula.toString());
		}
		context.getObjectContext().put(template, FormulaKey.SIZE, sizeFormula);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Formula res = context.getObjectContext().getFormula(template,
				FormulaKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[] { res.toString() };
	}

	@Override
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
