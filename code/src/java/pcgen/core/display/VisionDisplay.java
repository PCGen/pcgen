/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;

public class VisionDisplay
{

	public static String getVision(final PlayerCharacter aPC, CDOMObject cdo)
	{
		if (aPC == null)
		{
			return "";
		}
		Collection<CDOMReference<Vision>> mods = cdo.getListMods(Vision.VISIONLIST);
		if (mods == null)
		{
			return "";
		}
	
		StringBuilder visionString = new StringBuilder(25);
		for (CDOMReference<Vision> ref : mods)
		{
			for (Vision v : ref.getContainedObjects())
			{
				if (visionString.length() > 0)
				{
					visionString.append(';');
				}
				visionString.append(v.toString(aPC));
			}
		}
	
		return visionString.toString();
	}

	public static String getVision(CharacterDisplay display)
	{
		final StringBuilder visionString = new StringBuilder();
	
		for (Vision vision : display.getVisionList())
		{
			if (visionString.length() > 0)
			{
				visionString.append(", ");
			}
	
			visionString.append(vision);
		}
	
		return visionString.toString();
	}

}
