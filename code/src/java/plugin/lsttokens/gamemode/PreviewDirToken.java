/*
 * PreviewDirToken.java
 * Copyright 2007 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * This class handles the PREVIEWDIR game mode token.
 */
public class PreviewDirToken implements GameModeLstToken
{

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setPreviewDir(value);
        return true;
    }

    /**
     * Returns the name of the token this class handles.
     */
    @Override
    public String getTokenName()
    {
        return "PREVIEWDIR"; //$NON-NLS-1$
    }

}
