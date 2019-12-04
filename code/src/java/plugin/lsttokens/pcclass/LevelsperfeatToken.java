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
package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
	{
		int pipeLoc = value.indexOf('|');
		String numLevels;
		if (pipeLoc == -1)
		{
			numLevels = value;
			/*
			 * Some tokens use remove to do a .CLEAR. That ISN'T what this is
			 * doing. This needs to not unparse .CLEAR - so setting null is OK.
			 * But this is required since this token overwrites previous
			 * instances
			 */
			context.getObjectContext().put(pcc, StringKey.LEVEL_TYPE, null);
		}
		else
		{
			if (pipeLoc != value.lastIndexOf('|'))
			{
				return new ParseResult.Fail(
					getTokenName() + " must be of the form: " + getTokenName() + ":<int> or " + getTokenName()
						+ ":<int>|LEVELTYPE=<string>" + " Got " + getTokenName() + Constants.COLON + value);
			}
			numLevels = value.substring(0, pipeLoc);
			String levelTypeTag = value.substring(pipeLoc + 1);
			if (!levelTypeTag.startsWith("LEVELTYPE="))
			{
				return new ParseResult.Fail(
					"If " + getTokenName() + " has a | it must be of the form: " + getTokenName()
						+ ":<int>|LEVELTYPE=<string>" + " Got " + getTokenName() + Constants.COLON + value);
			}
			String levelType = levelTypeTag.substring(10);
			if (levelType == null || levelType.isEmpty())
			{
				return new ParseResult.Fail("If " + getTokenName() + " has a | it must be of the form: "
					+ getTokenName() + ":<int>|LEVELTYPE=<string>" + " Got an empty leveltype");
			}
			context.getObjectContext().put(pcc, StringKey.LEVEL_TYPE, levelType);
		}

		try
		{
			int in = Integer.parseInt(numLevels);
			if (in < 0)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer >= 0");
			}
			context.getObjectContext().put(pcc, IntegerKey.LEVELS_PER_FEAT, in);
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(getTokenName() + " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int> or " + getTokenName() + ":<int>|LEVELTYPE=<string>");
		}

		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lpf = context.getObjectContext().getInteger(pcc, IntegerKey.LEVELS_PER_FEAT);
		String levelType = context.getObjectContext().getString(pcc, StringKey.LEVEL_TYPE);
		if (lpf == null)
		{
			if (levelType != null)
			{
				context.addWriteMessage(getTokenName() + " found level type, but no levels per feat value");
			}
			return null;
		}
		if (lpf < 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append(lpf);
		if (levelType != null && !levelType.isEmpty())
		{
			result.append("|LEVELTYPE=");
			result.append(levelType);
		}
		return new String[]{result.toString()};
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
