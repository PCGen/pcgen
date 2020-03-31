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
package plugin.lsttokens.pcclass.level;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Processor;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.processor.HitDieFormula;
import pcgen.cdom.processor.HitDieLock;
import pcgen.cdom.processor.HitDieStep;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class HitdieLst extends AbstractToken implements CDOMPrimaryToken<PCClassLevel>
{

	@Override
	public String getTokenName()
	{
		return "HITDIE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCClassLevel level, String value)
	{
		try
		{
            int pipeLoc = value.indexOf(Constants.PIPE);
			if (pipeLoc != -1)
			{
				return new ParseResult.Fail(getTokenName() + " is invalid has a pipe: " + value);
			}
			Processor<HitDie> hdm;
			if (value.startsWith("%/"))
			{
				// HITDIE:%/num --- divides the classes hit die by num.
				int denom = Integer.parseInt(value.substring(2));
				if (denom <= 0)
				{
					return new ParseResult.Fail(getTokenName() + " was expecting a Positive Integer "
						+ "for dividing Lock, was : " + value.substring(2));
				}
				hdm = new HitDieFormula(new DividingFormula(denom));
			}
			else if (value.startsWith("%*"))
			{
				// HITDIE:%*num --- multiplies the classes hit die by num.
				int mult = Integer.parseInt(value.substring(2));
				if (mult <= 0)
				{
					return new ParseResult.Fail(getTokenName() + " was expecting a Positive "
						+ "Integer for multiplying Lock, was : " + value.substring(2));
				}
				hdm = new HitDieFormula(new MultiplyingFormula(mult));
			}
			else if (value.startsWith("%+"))
			{
				// possibly redundant with BONUS:HD MAX|num
				// HITDIE:%+num --- adds num to the classes hit die.
				int add = Integer.parseInt(value.substring(2));
				if (add <= 0)
				{
					return new ParseResult.Fail(getTokenName() + " was expecting a Positive "
						+ "Integer for adding Lock, was : " + value.substring(2));
				}
				hdm = new HitDieFormula(new AddingFormula(add));
			}
			else if (value.startsWith("%-"))
			{
				// HITDIE:%-num --- subtracts num from the classes hit die.
				// possibly redundant with BONUS:HD MAX|num if that will
				// take negative numbers.
				int sub = Integer.parseInt(value.substring(2));
				if (sub <= 0)
				{
					return new ParseResult.Fail(getTokenName() + " was expecting a Positive "
						+ "Integer for subtracting Lock, was : " + value.substring(2));
				}
				hdm = new HitDieFormula(new SubtractingFormula(sub));
			}
			else if (value.startsWith("%up"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(value.substring(3));
				if (steps <= 0)
				{
					return new ParseResult.Fail(
						"Invalid Step Count: " + steps + " in " + getTokenName() + " up (must be positive)");
				}
				if (steps >= 5)
				{
					return new ParseResult.Fail(
						"Invalid Step Count: " + steps + " in " + getTokenName() + " up (too large)");
				}

				hdm = new HitDieStep(steps, new HitDie(12));
			}
			else if (value.startsWith("%Hup"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(value.substring(4));
				if (steps <= 0)
				{
					return new ParseResult.Fail("Invalid Step Count: " + steps + " in " + getTokenName());
				}
				hdm = new HitDieStep(steps, null);
			}
			else if (value.startsWith("%down"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list d4,d6,d8,d10,d12. Stops at d4.

				int steps = Integer.parseInt(value.substring(5));
				if (steps <= 0)
				{
					return new ParseResult.Fail(
						"Invalid Step Count: " + steps + " in " + getTokenName() + " down (must be positive)");
				}
				if (steps >= 5)
				{
					return new ParseResult.Fail(
						"Invalid Step Count: " + steps + " in " + getTokenName() + " down (too large)");
				}

				hdm = new HitDieStep(-steps, new HitDie(4));
			}
			else if (value.startsWith("%Hdown"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list. No limit.
				int steps = Integer.parseInt(value.substring(6));
				if (steps <= 0)
				{
					return new ParseResult.Fail("Invalid Step Count: " + steps + " in " + getTokenName());
				}
				hdm = new HitDieStep(-steps, null);
			}
			else
			{
				int i = Integer.parseInt(value);
				if (i <= 0)
				{
					return new ParseResult.Fail("Invalid HitDie: " + i + " in " + getTokenName());
				}
				// HITDIE:num --- sets the hit die to num regardless of class.
				hdm = new HitDieLock(new HitDie(i));
			}

			context.getObjectContext().put(level, ObjectKey.HITDIE, hdm);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			ComplexParseResult pr = new ComplexParseResult();
			pr.addErrorMessage("Invalid Number in " + getTokenName() + ": " + nfe.getLocalizedMessage());
			pr.addErrorMessage("  Must be an Integer");
			return pr;
		}
	}

	@Override
	public String[] unparse(LoadContext context, PCClassLevel level)
	{
		Processor<HitDie> hdcf = context.getObjectContext().getObject(level, ObjectKey.HITDIE);
		if (hdcf == null)
		{
			return null;
		}
		return new String[]{hdcf.getLSTformat()};
	}

	@Override
	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}
}
