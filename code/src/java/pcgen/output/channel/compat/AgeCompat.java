/*
 * Copyright (c) Thomas Parker, 2018.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.output.channel.compat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.output.channel.ChannelUtilities;

/**
 * AgeCompat contains utility methods for communication of the PCs age through a channel.
 */
public final class AgeCompat
{

	private AgeCompat()
	{
		//Do not instantiate utility class
	}

	/**
	 * Gets the current Age for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Age should be returned
	 * @return The current Age for the PC represented by the given CharID
	 */
	public static Integer getCurrentAge(CharID id)
	{
		String channelName =
				ControlUtilities.getControlToken(Globals.getContext(), CControl.AGEINPUT);
		return (Integer) ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the current Age for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Age should be set
	 * @param age
	 *            The Age which should be set
	 */
	public static void setCurrentAge(CharID id, Integer age)
	{
		String channelName =
				ControlUtilities.getControlToken(Globals.getContext(), CControl.AGEINPUT);
		ChannelUtilities.setGlobalChannel(id, channelName, age);
	}
}
