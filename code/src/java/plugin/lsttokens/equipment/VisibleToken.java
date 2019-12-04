/*
 * Copyright 2015 (C) Stefan Radermacher
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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * VisibleToken handles the processing of the VISIBLE tag in the
 * definition of an Equipment object.
 * 
 */
public class VisibleToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{
	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
	{
		Visibility vis;
        switch (value) {
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            case "DISPLAY":
                vis = Visibility.DISPLAY_ONLY;
                break;
            case "EXPORT":
                vis = Visibility.OUTPUT_ONLY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            default:
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Unexpected value used in " + getTokenName() + " in Equipment");
                cpr.addErrorMessage(' ' + value + " is not a valid value for " + getTokenName());
                cpr.addErrorMessage(" Valid values in Equipment are YES, NO, DISPLAY, EXPORT");
                return cpr;
        }
		context.getObjectContext().put(eq, ObjectKey.VISIBILITY, vis);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		Visibility vis = context.getObjectContext().getObject(eq, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY_ONLY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.OUTPUT_ONLY))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for an Equipment");
			return null;
		}
		return new String[]{visString};
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
