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
package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ISOGL Token
 */
public class IsoglToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "ISOGL";
	}

	public boolean parse(LoadContext context, Campaign pcc, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(pcc, ObjectKey.IS_OGL, set);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign pcc)
	{
		Boolean isM = context.getObjectContext().getObject(pcc,
				ObjectKey.IS_OGL);
		if (isM == null)
		{
			return null;
		}
		return new String[] { isM.booleanValue() ? "YES" : "NO" };
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
