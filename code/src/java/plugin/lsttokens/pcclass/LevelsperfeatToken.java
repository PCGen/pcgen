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
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		int pipeLoc = value.indexOf('|');
		String numLevels;
		if (pipeLoc == -1)
		{
			numLevels = value;
			/*
			 * CONSIDER This could stand to see some improvement. The challenge
			 * here is that some tokens use this syntax (setting to null) to
			 * identify .CLEAR (vs. a remove like ObjectKey possesses). This
			 * needs similar behavior, but needs to not unparse .CLEAR - the
			 * result being that the unparse method of this token needs to
			 * explicitly check LEVEL_TYPE against .CLEAR to ensure that
			 * (invalid) string is not unparsed.
			 */
			context.getObjectContext()
					.put(pcc, StringKey.LEVEL_TYPE, null);
		}
		else
		{
			if (pipeLoc != value.lastIndexOf('|'))
			{
				Logging.log(Logging.LST_ERROR, getTokenName()
						+ " must be of the form: " + getTokenName()
						+ ":<int> or " + getTokenName()
						+ ":<int>|LEVELTYPE=<string>" + " Got "
						+ getTokenName() + ":" + value);
				return false;
			}
			numLevels = value.substring(0, pipeLoc);
			String levelTypeTag = value.substring(pipeLoc + 1);
			if (!levelTypeTag.startsWith("LEVELTYPE="))
			{
				Logging.log(Logging.LST_ERROR, "If " + getTokenName()
						+ " has a | it must be of the form: " + getTokenName()
						+ ":<int>|LEVELTYPE=<string>" + " Got "
						+ getTokenName() + ":" + value);
				return false;
			}
			String levelType = levelTypeTag.substring(10);
			if (levelType == null || levelType.length() == 0)
			{
				Logging.log(Logging.LST_ERROR, "If " + getTokenName()
						+ " has a | it must be of the form: " + getTokenName()
						+ ":<int>|LEVELTYPE=<string>"
						+ " Got an empty leveltype");
				return false;
			}
			context.getObjectContext()
					.put(pcc, StringKey.LEVEL_TYPE, levelType);
		}

		try
		{
			Integer in = Integer.valueOf(numLevels);
			if (in.intValue() < 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName()
						+ " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(pcc, IntegerKey.LEVELS_PER_FEAT, in);
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int> or " + getTokenName()
					+ ":<int>|LEVELTYPE=<string>");
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.rules.persistence.token.CDOMPrimaryToken#unparse(pcgen.rules.context.LoadContext,
	 *      java.lang.Object)
	 */
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lpf = context.getObjectContext().getInteger(pcc,
				IntegerKey.LEVELS_PER_FEAT);
		String levelType = context.getObjectContext().getString(pcc,
				StringKey.LEVEL_TYPE);
		if (lpf == null)
		{
			if (levelType != null)
			{
				context.addWriteMessage(getTokenName()
						+ " found level type, but no levels per feat value");
			}
			return null;
		}
		if (lpf.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append(lpf);
		if (levelType != null && levelType.length() > 0
				&& !levelType.equals(Constants.LST_DOT_CLEAR))
		{
			result.append("|LEVELTYPE=");
			result.append(levelType);
		}
		return new String[] { result.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
