/*
 * Copyright 2016 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.cdom.util;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.rules.context.LoadContext;

public final class ControlUtilities
{

	private ControlUtilities()
	{
		//Do not instantiate Utility class
	}

	public static String getControlToken(LoadContext context, String command)
	{
		CodeControl controller =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(
					CodeControl.class, "Controller");
		if (controller != null)
		{
			return controller.get(ObjectKey.getKeyFor(String.class, "*"
				+ command));
		}
		return null;
	}

	public static boolean hasControlToken(LoadContext context, String command)
	{
		CodeControl controller =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(
					CodeControl.class, "Controller");
		if (controller != null)
		{
			return controller.get(ObjectKey.getKeyFor(String.class, "*"
				+ command)) != null;
		}
		return false;
	}
}
