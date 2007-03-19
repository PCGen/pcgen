/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class DotClearToken implements AddLstToken
{

	public boolean parse(PObject target, String value, int aLevel)
	{
		if (aLevel > 0)
		{
			Logging
				.errorPrint("Warning: You performed a Dangerous .CLEAR in a ADD: Token");
			Logging
				.errorPrint("  A non-level limited .CLEAR was used in a Class Level line");
			Logging
				.errorPrint("  Today, this performs a .CLEAR on the entire PCClass");
			Logging
				.errorPrint("  However, you are using undocumented behavior that is subject to change");
			Logging.errorPrint("  Hint: It will change after PCGen 5.12");
			Logging
				.errorPrint("  Please level limit the .CLEAR (e.g. .CLEAR.LEVEL2)");
			Logging
				.errorPrint("  ... or put the ADD:.CLEAR on a non-level Class line");
		}
		target.clearAdds();
		return true;
	}

	public String getTokenName()
	{
		return ".CLEAR";
	}
}
