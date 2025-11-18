/**
 * IconToken.java
 * Copyright James Dempsey, 2011
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

import java.net.URI;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.persistence.lst.URIFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The Class {@code IconToken} processes the ICON token. This
 * allows an icon to be specified for the item of equipment.
 */
public class IconToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "ICON";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
	{
		URI uri = new URIFactory(eq.getSourceURI(), value).getURI();
		if (uri == URIFactory.FAILED_URI)
		{
			return new ParseResult.Fail(getTokenName() + " must be a valid URI.");
		}
		context.getObjectContext().put(eq, StringKey.ICON, value);
		context.getObjectContext().put(eq, ObjectKey.ICON_URI, uri);
		return ParseResult.SUCCESS;

	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		String icon = context.getObjectContext().getString(eq, StringKey.ICON);
		if (icon == null)
		{
			return null;
		}
		return new String[]{icon};
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
