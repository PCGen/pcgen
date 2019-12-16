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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken extends AbstractNonEmptyToken<Skill> implements CDOMPrimaryToken<Skill>
{

	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Skill skill, String value)
	{
		String visString = value;
		int pipeLoc = value.indexOf(Constants.PIPE);
		boolean readOnly = false;
		if (pipeLoc != -1)
		{
			if (value.substring(pipeLoc + 1).equals("READONLY"))
			{
				visString = value.substring(0, pipeLoc);
				readOnly = true;
			}
			else
			{
				return new ParseResult.Fail("Misunderstood text after pipe on Tag: " + value);
			}
		}
		Visibility vis;
        switch (visString)
        {
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            case "ALWAYS":
                vis = Visibility.DEFAULT;
                break;
            case "DISPLAY":
                vis = Visibility.DISPLAY_ONLY;
                break;
            case "GUI":
                vis = Visibility.DISPLAY_ONLY;
                break;
            case "EXPORT":
                vis = Visibility.OUTPUT_ONLY;
                break;
            case "CSHEET":
                vis = Visibility.OUTPUT_ONLY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            default:
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Unexpected value used in " + getTokenName() + " in Skill");
                cpr.addErrorMessage(' ' + value + " is not a valid value for " + getTokenName());
                cpr.addErrorMessage(" Valid values in Skill are YES, ALWAYS, NO, DISPLAY, GUI, EXPORT, CSHEET");
                return cpr;
        }
		context.getObjectContext().put(skill, ObjectKey.VISIBILITY, vis);
		if (readOnly)
		{
			if (vis.equals(Visibility.OUTPUT_ONLY))
			{
				return new ParseResult.Fail("|READONLY suffix not valid with " + getTokenName() + " EXPORT or CSHEET");
			}
			context.getObjectContext().put(skill, ObjectKey.READ_ONLY, Boolean.TRUE);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Skill skill)
	{
		Visibility vis = context.getObjectContext().getObject(skill, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		if (!vis.equals(Visibility.DEFAULT) && !vis.equals(Visibility.DISPLAY_ONLY)
			&& !vis.equals(Visibility.OUTPUT_ONLY))
		{
			context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for a Skill");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(vis.getLSTFormat());
		Boolean readOnly = context.getObjectContext().getObject(skill, ObjectKey.READ_ONLY);
		if (readOnly != null)
		{
			if (!vis.equals(Visibility.OUTPUT_ONLY))
			{
				/*
				 * Don't barf if OUTPUT and READONLY as .MOD will cause that to
				 * happen
				 */
				sb.append('|').append("READONLY");
			}
		}
		return new String[]{sb.toString()};
	}

	@Override
	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
