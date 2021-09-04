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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Load;

public class UnencumberedmoveLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "UNENCUMBEREDMOVE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean hasArmor = false;
		boolean hasMove = false;

		Load loadMove = null;
		Load loadArmor = null;

		while (tok.hasMoreTokens())
		{
			String loadString = tok.nextToken();
			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				ParseResult pr = validateOnlyMove(hasMove);
				if (!pr.passed())
				{
					return pr;
				}
				loadMove = Load.MEDIUM;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				ParseResult pr = validateOnlyMove(hasMove);
				if (!pr.passed())
				{
					return pr;
				}
				loadMove = Load.HEAVY;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				ParseResult pr = validateOnlyMove(hasMove);
				if (!pr.passed())
				{
					return pr;
				}
				loadMove = Load.OVERLOAD;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				ParseResult pr = validateOnlyArmor(hasArmor);
				if (!pr.passed())
				{
					return pr;
				}
				loadArmor = Load.MEDIUM;
				hasArmor = true;
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				ParseResult pr = validateOnlyArmor(hasArmor);
				if (!pr.passed())
				{
					return pr;
				}
				loadArmor = Load.OVERLOAD;
				hasArmor = true;
			}
			else if (loadString.equalsIgnoreCase("LightLoad"))
			{
				ParseResult pr = validateOnlyMove(hasMove);
				if (!pr.passed())
				{
					return pr;
				}
				loadMove = Load.LIGHT;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("LightArmor"))
			{
				ParseResult pr = validateOnlyMove(hasArmor);
				if (!pr.passed())
				{
					return pr;
				}
				loadArmor = Load.LIGHT;
				hasArmor = true;
			}
			else
			{
				return new ParseResult.Fail("Invalid value of \"" + loadString + "\" for UNENCUMBEREDMOVE in \""
					+ obj.getDisplayName() + "\".");
			}
		}
		context.getObjectContext().put(obj, ObjectKey.UNENCUMBERED_LOAD, loadMove);
		context.getObjectContext().put(obj, ObjectKey.UNENCUMBERED_ARMOR, loadArmor);
		return ParseResult.SUCCESS;
	}

	private ParseResult validateOnlyArmor(boolean hasArmor)
	{
		if (hasArmor)
		{
			return new ParseResult.Fail(
				"Encountered Second Armor Load Type in " + getTokenName() + " this is not valid.");
		}
		return ParseResult.SUCCESS;
	}

	private ParseResult validateOnlyMove(boolean hasMove)
	{
		if (hasMove)
		{
			return new ParseResult.Fail(
				"Encountered Second Move Load Type in " + getTokenName() + " this is not valid.");
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Load load = context.getObjectContext().getObject(obj, ObjectKey.UNENCUMBERED_LOAD);
		Load at = context.getObjectContext().getObject(obj, ObjectKey.UNENCUMBERED_ARMOR);
		if (load == null && at == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (load != null)
		{
			switch (load)
			{
				case OVERLOAD -> sb.append("Overload");
				case HEAVY -> sb.append("HeavyLoad");
				case MEDIUM -> sb.append("MediumLoad");
				case LIGHT -> sb.append("LightLoad");
				default -> {
					context.addWriteMessage(getTokenName() + " encountered unknown Movement Load: " + load);
					return null;
				}
			}
		}
		if (at != null)
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			switch (at)
			{
				case OVERLOAD -> sb.append("HeavyArmor");
				case MEDIUM -> sb.append("MediumArmor");
				case LIGHT -> sb.append("LightArmor");
				default -> {
					context.addWriteMessage(getTokenName() + " encountered invalid Armor Load: " + load);
					return null;
				}
			}
		}
		return new String[]{sb.toString()};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
