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
package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ACHECK Token
 */
public class AcheckToken extends AbstractNonEmptyToken<Skill> implements CDOMPrimaryToken<Skill>
{

	@Override
	public String getTokenName()
	{
		return "ACHECK";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Skill skill, String value)
	{
		SkillArmorCheck aCheck;
		try
		{
			aCheck = SkillArmorCheck.valueOf(value);
		}
		catch (IllegalArgumentException iae)
		{
			/*
			 * TODO516 turn on deprecation
			 */
			// Logging.deprecationPrint("Misunderstood " + getTokenName() + ": "
			// + value + " is not an abbreviation");
			char first = value.charAt(0);
			switch (first)
			{
				case 'N':
					// Logging.deprecationPrint(" please use NONE");
					aCheck = SkillArmorCheck.NONE;
					break;
				case 'Y':
					// Logging.deprecationPrint(" please use YES");
					aCheck = SkillArmorCheck.YES;
					break;
				case 'P':
					// Logging.deprecationPrint(" please use NONPROF");
					aCheck = SkillArmorCheck.NONPROF;
					break;
				case 'D':
					// Logging.deprecationPrint(" please use DOUBLE");
					aCheck = SkillArmorCheck.DOUBLE;
					break;
				case 'W':
					// Logging.deprecationPrint(" please use WEIGHT");
					aCheck = SkillArmorCheck.WEIGHT;
					break;
				default:
					return new ParseResult.Fail("Skill " + getTokenName() + " Did not understand: " + value);
			}
		}

		context.getObjectContext().put(skill, ObjectKey.ARMOR_CHECK, aCheck);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Skill skill)
	{
		SkillArmorCheck sac = context.getObjectContext().getObject(skill, ObjectKey.ARMOR_CHECK);
		if (sac == null)
		{
			return null;
		}
		return new String[]{sac.toString()};
	}

	@Override
	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
