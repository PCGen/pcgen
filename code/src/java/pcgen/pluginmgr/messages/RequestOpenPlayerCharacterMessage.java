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

import java.io.File;

import pcgen.core.PlayerCharacter;
import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class {@code RequestOpenPlayerCharacterMessage} encapsulates a
 * message requesting that a specific character be loaded. The requester may
 * ask that the character be added to the message for later use, however this
 * should not be assumed to be present until all message processing is completed.
 */
@SuppressWarnings("serial")
public class RequestOpenPlayerCharacterMessage extends PCGenMessage
{
    private final File file;
    private final boolean blockLoadedMessage;
    private PlayerCharacter playerCharacter;

    /**
     * Create a new instance of RequestOpenPlayerCharacterMessage
     *
     * @param source             The object requesting the open.
     * @param file               The character file to be opened.
     * @param blockLoadedMessage Should the character loaded message be blocked?
     */
    public RequestOpenPlayerCharacterMessage(Object source, File file, boolean blockLoadedMessage)
    {
        super(source);
        this.file = file;
        this.blockLoadedMessage = blockLoadedMessage;
    }

    public File getFile()
    {
        return file;
    }

    public boolean isBlockLoadedMessage()
    {
        return blockLoadedMessage;
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter)
    {
        this.playerCharacter = playerCharacter;
    }

    public PlayerCharacter getPlayerCharacter()
    {
        return playerCharacter;
    }

}
