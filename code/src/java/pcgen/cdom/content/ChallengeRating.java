/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;

public class ChallengeRating extends ConcretePrereqObject implements
		LSTWriteable
{

	public static final ChallengeRating ZERO = new ChallengeRating("0");
	
	private final Formula rating;

	public ChallengeRating(String string)
	{
		super();
		String crValue = string;
		String testString;
		if (crValue.startsWith("1/"))
		{
			testString = crValue.substring(2);
			crValue = "-" + testString;
		}
		else
		{
			testString = crValue;
		}
		try
		{
			int i = Integer.parseInt(testString);
			if (i < 0)
			{
				throw new IllegalArgumentException(
					"Challenge Rating cannot be negative");
			}
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException(
					"Challenge Rating must be a positive integer i or 1/i");
		}
		rating = FormulaFactory.getFormulaFor(crValue);
	}

	public Formula getRating()
	{
		return rating;
	}

	public String getLSTformat()
	{
		String str = rating.toString();
		if (str.startsWith("-"))
		{
			str = "1/" + str.substring(1);
		}
		return str;
	}

	@Override
	public int hashCode()
	{
		return rating.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof ChallengeRating
			&& ((ChallengeRating) o).rating.equals(rating);
	}
}
