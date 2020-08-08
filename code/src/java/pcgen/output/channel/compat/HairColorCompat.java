/*
 * Copyright 2018 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.output.channel.compat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.output.channel.ChannelUtilities;

/**
 * HairColorCompat contains utility methods for communication of the PCs hair color
 * through a channel.
 */
public final class HairColorCompat
{

	private HairColorCompat()
	{
		//Do not instantiate utility class
	}

	/**
	 * Gets the current Hair Color for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Hair Color should be
	 *            returned
	 * @return The current Hair Color for the PC represented by the given CharID
	 */
	public static String getCurrentHairColor(CharID id)
	{
		String channelName = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.HAIRCOLORINPUT);
		return (String) ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the current Hair Color for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Hair Color should be set
	 * @param hairColor
	 *            The Hair Color which should be set
	 */
	public static void setCurrentHairColor(CharID id, String hairColor)
	{
		String channelName = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.HAIRCOLORINPUT);
		ChannelUtilities.setGlobalChannel(id, channelName, hairColor);
	}
}
