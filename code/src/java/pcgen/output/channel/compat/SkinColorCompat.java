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
 * SkinColorCompat contains utility methods for communication of the PCs skin color
 * through a channel.
 */
public final class SkinColorCompat
{

	private SkinColorCompat()
	{
		//Do not instantiate utility class
	}

	/**
	 * Gets the current Skin Color for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Skin Color should be
	 *            returned
	 * @return The current Skin Color for the PC represented by the given CharID
	 */
	public static String getCurrentSkinColor(CharID id)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(),
			CControl.SKINCOLORINPUT);
		return (String) ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the current Skin Color for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Skin Color should be set
	 * @param skinColor
	 *            The Skin Color which should be set
	 */
	public static void setCurrentSkinColor(CharID id, String skinColor)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(),
			CControl.SKINCOLORINPUT);
		ChannelUtilities.setGlobalChannel(id, channelName, skinColor);
	}
}
