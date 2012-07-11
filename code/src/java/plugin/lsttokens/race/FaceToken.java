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
package plugin.lsttokens.race;

import java.math.BigDecimal;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.BigDecimalHelper;

/**
 * Class deals with FACE Token
 */
public class FaceToken extends AbstractNonEmptyToken<Race> implements
		CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "FACE";
	}

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
    {
		int commaLoc = value.indexOf(Constants.COMMA);
		if (commaLoc != value.lastIndexOf(Constants.COMMA))
		{
			return new ParseResult.Fail(getTokenName() + " must be of the form: "
					+ getTokenName() + ":<num>[,<num>]", context);
		}
		if (commaLoc > -1)
		{
			if (commaLoc == 0)
			{
				return new ParseResult.Fail(getTokenName()
								+ " should not start with a comma.  Must be of the form: "
								+ getTokenName() + ":<num>[,<num>]", context);
			}
			if (commaLoc == value.length() - 1)
			{
				return new ParseResult.Fail(getTokenName()
								+ " should not end with a comma.  Must be of the form: "
								+ getTokenName() + ":<num>[,<num>]", context);
			}
			try
			{
				String widthString = value.substring(0, commaLoc).trim();
				BigDecimal width = new BigDecimal(widthString);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					return new ParseResult.Fail("Cannot have negative width in "
							+ getTokenName() + ": " + value, context);
				}
				context.getObjectContext().put(race, ObjectKey.FACE_WIDTH,
						width);
			}
			catch (NumberFormatException nfe)
			{
				return new ParseResult.Fail("Misunderstood Double Width in Tag: "
						+ value, context);
			}

			try
			{
				String heightString = value.substring(commaLoc + 1).trim();
				BigDecimal height = new BigDecimal(heightString);
				if (height.compareTo(BigDecimal.ZERO) < 0)
				{
					return new ParseResult.Fail("Cannot have negative height in "
							+ getTokenName() + ": " + value, context);
				}
				context.getObjectContext().put(race, ObjectKey.FACE_HEIGHT,
						height);
			}
			catch (NumberFormatException ne)
			{
				return new ParseResult.Fail("Misunderstood Double Height in Tag: "
						+ value, context);
			}
		}
		else
		{
			try
			{
                BigDecimal width = new BigDecimal(value);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					return new ParseResult.Fail("Cannot have negative width in "
							+ getTokenName() + ": " + value, context);
				}
				context.getObjectContext().put(race, ObjectKey.FACE_WIDTH,
						width);
				context.getObjectContext().put(race, ObjectKey.FACE_HEIGHT,
						BigDecimal.ZERO);
			}
			catch (NumberFormatException nfe)
			{
				return new ParseResult.Fail("Misunderstood Double in Tag: " + value, context);
			}
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Race race)
	{
		BigDecimal width = context.getObjectContext().getObject(race,
				ObjectKey.FACE_WIDTH);
		BigDecimal height = context.getObjectContext().getObject(race,
				ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		if (width == null || height == null)
		{
			context.addWriteMessage("Must have both width and height in "
					+ getTokenName() + ": " + width + " " + height);
			return null;
		}
		if (width.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative width in "
					+ getTokenName() + ": " + width);
			return null;
		}
		if (height.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative height in "
					+ getTokenName() + ": " + height);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BigDecimal w = BigDecimalHelper.trimBigDecimal(width);
		sb.append(w);
		if (height.compareTo(BigDecimal.ZERO) != 0)
		{
			BigDecimal h = BigDecimalHelper.trimBigDecimal(height);
			sb.append(',').append(h);
		}
		return new String[] { sb.toString() };
	}

	@Override
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

}
