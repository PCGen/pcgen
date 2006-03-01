/*
 * UDam.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.10 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/13 20:01:22 $
 *
 */
package plugin.bonustokens;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.util.Logging;

/**
 * <code>UDam</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class UDam extends BonusObj
{
	private static final String[] bonusHandled =
		{
			"UDAM"
		};

	protected boolean parseToken(final String token)
	{
		if (token.startsWith("CLASS=") || token.startsWith("CLASS."))
		{
			final String className = token.substring(6);
			final PCClass aClass = Globals.getClassNamed(className);

			if (aClass != null)
			{
				addBonusInfo(aClass);

				return true;
			}
			addBonusInfo(className);
			Logging.errorPrint("Could not find class '" + className + "' for UDAM token");
		}

		return false;
	}

	protected String unparseToken(final Object obj)
	{
		if (obj instanceof String)
		{
			final PCClass aClass = Globals.getClassNamed((String) obj);
			if (aClass != null)
			{
				replaceBonusInfo(obj, aClass);
			}
			return "CLASS." + obj;
		}
		return "CLASS." + ((PCClass) obj).getName();
	}

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
