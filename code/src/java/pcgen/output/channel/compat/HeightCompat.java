/*
 * Copyright (c) Thomas Parker, 2018-9.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.output.channel.compat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.output.channel.ChannelUtilities;

/**
 * HeightCompat contains utility methods for communication of the PCs Height through a
 * channel.
 */
public final class HeightCompat
{

	private HeightCompat()
	{
		//Do not instantiate utility class
	}

	/**
	 * Gets the current Height in inches for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Height in inches should be
	 *            returned
	 * @return The current Height in inches for the PC represented by the given CharID
	 */
	public static Integer getCurrentHeight(CharID id)
	{
		return (Integer) ChannelUtilities.readControlledChannel(id,
			CControl.HEIGHTINPUT);
	}

	/**
	 * Sets the current Height in inches for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Height in inches should be
	 *            set
	 * @param align
	 *            The Height in inches which should be set
	 */
	public static void setCurrentHeight(CharID id, Integer align)
	{
		ChannelUtilities.setControlledChannel(id, CControl.HEIGHTINPUT, align);
	}

}
