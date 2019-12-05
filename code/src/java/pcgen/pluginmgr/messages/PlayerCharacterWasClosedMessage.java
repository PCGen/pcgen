/*
 * Copyright James Dempsey, 2014
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.pluginmgr.messages;

import pcgen.core.PlayerCharacter;
import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class {@code PlayerCharacterWasClosedMessage} encapsulates an
 * advisory message that a character was closed in the user interface of PCGen.
 */
@SuppressWarnings("serial")
public class PlayerCharacterWasClosedMessage extends PCGenMessage
{

    private final PlayerCharacter pc;

    /**
     * Create a new instance of PlayerCharacterWasClosedMessage
     *
     * @param source The object advising of the event.
     * @param pc     The character that was closed.
     */
    public PlayerCharacterWasClosedMessage(Object source, PlayerCharacter pc)
    {
        super(source);
        this.pc = pc;
    }

    /**
     * @return the character that was closed.
     */
    public PlayerCharacter getPC()
    {
        return pc;
    }

}
