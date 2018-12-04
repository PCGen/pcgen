/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.gui2.util;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.facade.util.VetoableReferenceFacade;
import pcgen.rules.context.LoadContext;

/**
 * InterfaceChannelUtilities provides utility methods for Channels for the UI.
 */
public final class InterfaceChannelUtilities
{

	private InterfaceChannelUtilities()
	{
		//Do not instantiate utility class
	}

	/**
	 * Returns the ReferenceFacade for a the channel defined by a given CodeControl
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter on which the Channel is
	 *            located
	 * @param codeControl
	 *            The CodeControl indicating the channel for which the ReferenceFacade
	 *            should be returned
	 * @return The ReferenceFacade for a the channel defined by a given CodeControl
	 */
	public static <T> VetoableReferenceFacade<T> getReferenceFacade(CharID id,
		CControl codeControl)
	{
		LoadContext context = Globals.getContext();
		String channelName =
				ControlUtilities.getControlToken(context, codeControl);
		return (VetoableReferenceFacade<T>) context.getVariableContext()
			.getGlobalChannel(id, channelName);
	}

}
