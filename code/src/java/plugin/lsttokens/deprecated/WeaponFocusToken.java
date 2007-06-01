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
package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.util.Logging;

public class WeaponFocusToken implements ChooseLstToken, DeprecatedToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		Logging.errorPrint("CHOOSE:WEAPONFOCUS is deprecated: " + value);
		Logging.errorPrint("  Please use CHOOSE:FEAT=???");
		return false;
	}

	public String getTokenName()
	{
		return "WEAPONFOCUS";
	}

	public String getMessage(PObject obj, String value)
	{
		return "CHOOSE:WEAPONFOCUS is deprecated.\nPlease use CHOOSE:FEAT=???";
	}
}
