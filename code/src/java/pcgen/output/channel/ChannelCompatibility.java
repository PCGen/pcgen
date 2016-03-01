/*
 * Copyright (c) Thomas Parker, 2016.
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
package pcgen.output.channel;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.output.channel.compat.StatAdapter;

public class ChannelCompatibility
{

	public static WriteableReferenceFacade<Integer> getStatScore(CharID id,
		PCStat stat)
	{
		if (ControlUtilities.hasControlToken(Globals.getContext(),
			CControl.STATSCORE))
		{
			return (WriteableReferenceFacade<Integer>) ChannelUtilities
				.getChannel(id, stat, stat.getKeyName());
		}
		else
		{
			return StatAdapter.generate(id, stat);
		}
	}
}
