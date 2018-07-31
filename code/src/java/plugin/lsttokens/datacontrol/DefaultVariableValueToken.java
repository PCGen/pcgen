/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.DefaultVarValue;
import pcgen.cdom.formula.local.ModifierDecoration;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ModifierFactory;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DEFAULTVARIABLEVALUE Token
 */
public class DefaultVariableValueToken extends AbstractNonEmptyToken<DefaultVarValue>
		implements CDOMPrimaryToken<DefaultVarValue>
{

	@Override
	public String getTokenName()
	{
		return "DEFAULTVARIABLEVALUE";
	}

	@Override
	public Class<DefaultVarValue> getTokenClass()
	{
		return DefaultVarValue.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, DefaultVarValue dvv, String value)
	{
		char separator = '|';
		int pipeLoc = value.indexOf(separator);
		if (pipeLoc == 0)
		{
			return new ParseResult.Fail(getTokenName() + " arguments may not start with " + separator + " : " + value);
		}
		if (value.indexOf(String.valueOf(new char[]{separator, separator})) != -1)
		{
			return new ParseResult.Fail(
				getTokenName() + " arguments uses double separator " + separator + separator + " : " + value);
		}
		if (value.lastIndexOf(separator) != pipeLoc)
		{
			return new ParseResult.Fail(getTokenName() + " requires only a type and a value, found: " + value);
		}
		String formatName = value.substring(0, pipeLoc);
		String formatValue;
		if ((pipeLoc + 1) == value.length())
		{
			formatValue = "";
		}
		else
		{
			formatValue = value.substring(pipeLoc + 1);
		}
		FormatManager<?> fmtManager;
		try
		{
			fmtManager = context.getReferenceContext().getFormatManager(formatName);
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			return new ParseResult.Fail(getTokenName() + " found an unsupported format: " + formatName);
		}
		dvv.setFormatManager(fmtManager);
		return subProcess(context, dvv, formatValue, fmtManager);
	}

	private <T> ParseResult subProcess(LoadContext context, DefaultVarValue dvv, String defaultValue,
		FormatManager<T> fmtManager)
	{
		Class<T> cl = fmtManager.getManagedClass();
		ModifierFactory<T> m = TokenLibrary.getModifier(cl, "SET");
		if (m == null)
		{
			return new ParseResult.Fail("ModifierType " + fmtManager.getIdentifierType() + " requires a SET modifier");
		}
		FormulaModifier<T> defaultModifier;
		try
		{
			defaultModifier =
					context.getVariableContext().getModifier("SET", defaultValue, context.getActiveScope(), fmtManager);
		}
		catch (IllegalArgumentException e)
		{
			return new ParseResult.Fail(
				"ModifierType " + fmtManager.getIdentifierType() + " could not be initialized to a default value of: "
					+ defaultValue + " due to " + e.getLocalizedMessage());
		}
		defaultModifier.addAssociation("PRIORITY=0");
		dvv.setModifier(defaultModifier);
		context.getVariableContext().addDefault(cl, new ModifierDecoration<>(defaultModifier));
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, DefaultVarValue dvv)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(dvv.getFormatManager().getIdentifierType());
		sb.append(Constants.PIPE);
		sb.append(dvv.getModifier().getInstructions());
		return new String[]{sb.toString()};
	}
}
