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
import pcgen.rules.context.LoadContext;

/**
 * ChannelCompatibility is a class used to get the appropriate WriteableReferenceFacade
 * objects based on whether a code control is operated (or not).
 */
public final class ChannelCompatibility
{

    private ChannelCompatibility()
    {
        //Do not instantiate Utility Class
    }

    /**
     * Returns the acting WriteableReferenceFacade based on whether the STATINPUT code
     * control has been used.
     *
     * @param id   The CharID representing the PlayerCharacter for which the
     *             WriteableReferenceFacade will operate
     * @param stat The PCStat for which the WriteableReferenceFacade will operate
     * @return The acting WriteableReferenceFacade based on whether the STATINPUT code
     * control has been used
     */
    public static WriteableReferenceFacade<Number> getStatScore(CharID id, PCStat stat)
    {
        LoadContext context = Globals.getContext();
        String channelName = ControlUtilities.getControlToken(context, CControl.STATINPUT);
        if (channelName == null)
        {
            return StatAdapter.generate(id, stat);
        } else
        {
            return (WriteableReferenceFacade<Number>) context.getVariableContext().getChannel(id, stat, channelName);
        }
    }
}
