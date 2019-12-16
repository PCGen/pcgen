/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.startpack;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * VISIBLE token for KitsStartpack
 */
public class VisibleToken extends AbstractNonEmptyToken<Kit> implements CDOMPrimaryToken<Kit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	public Class<Kit> getTokenClass()
	{
		return Kit.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Kit kit, String value)
	{
		Visibility vis;
        switch (value)
        {
            case "QUALIFY":
                vis = Visibility.QUALIFY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            default:
                return new ParseResult.Fail("Can't understand Visibility: " + value);
        }
		kit.put(ObjectKey.VISIBILITY, vis);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Kit kit)
	{
		Visibility vis = kit.get(ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.QUALIFY))
		{
			visString = "QUALIFY";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for a Kit");
			return null;
		}
		return new String[]{visString};
	}

}
