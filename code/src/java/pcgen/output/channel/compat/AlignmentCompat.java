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
import pcgen.core.PCAlignment;
import pcgen.output.channel.ChannelUtilities;

public class AlignmentCompat
{

	/**
	 * Gets the current Alignment for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Alignment should be
	 *            returned
	 * @return The current Alignment for the PC represented by the given CharID
	 */
	public static PCAlignment getCurrentAlignment(CharID id)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(), CControl.ALIGNMENTINPUT);
		return (PCAlignment) ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the current alignment for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Alignment should be set
	 * @param align
	 *            The Alignment which should be set
	 */
	public static void setCurrentAlignment(CharID id, PCAlignment align)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(), CControl.ALIGNMENTINPUT);
		ChannelUtilities.setGlobalChannel(id, channelName, align);
	}

}
