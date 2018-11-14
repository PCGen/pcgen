/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.channel.compat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.output.channel.ChannelUtilities;

/**
 * DeityCompat contains utility methods for communication of the PCs Deity through a
 * channel.
 */
public final class DeityCompat
{
	private DeityCompat()
	{
		//Do not construct utility class
	}

	/**
	 * Gets the current Deity for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Deity should be returned
	 * @return The current Deity for the PC represented by the given CharID
	 */
	public static Deity getCurrentDeity(CharID id)
	{
		return (Deity) ChannelUtilities.readControlledChannel(id, CControl.DEITYINPUT);
	}

	/**
	 * Sets the current Deity for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Deity should be set
	 * @param deity
	 *            The Deity which should be set
	 */
	public static void setCurrentDeity(CharID id, Deity deity)
	{
		ChannelUtilities.setControlledChannel(id, CControl.DEITYINPUT, deity);
	}

}
