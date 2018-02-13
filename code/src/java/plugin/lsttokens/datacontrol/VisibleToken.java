/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import pcgen.cdom.content.ContentDefinition;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

public class VisibleToken extends AbstractNonEmptyToken<ContentDefinition>
		implements CDOMPrimaryToken<ContentDefinition>
{

	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		ContentDefinition factDef, String value)
	{
		Visibility vis;
		if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else if (value.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (value.equals("EXPORT"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else
		{
			return new ParseResult.Fail("Unable to understand "
				+ getTokenName() + " tag: " + value);
		}
		factDef.setVisibility(vis);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, ContentDefinition factDef)
	{
		Visibility vis = factDef.getVisibility();
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
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a Fact Definition");
			return null;
		}
		return new String[]{visString};
	}

	@Override
	public Class<ContentDefinition> getTokenClass()
	{
		return ContentDefinition.class;
	}
}
