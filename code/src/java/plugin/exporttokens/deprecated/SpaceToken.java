/*
 * SpaceToken.java
 * Copyright 2008 (C) PCGen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens.deprecated;

import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * SpaceToken - Used to provide a breaking space character (e.g. ASCII 32)
 * character) for use in a MANUASLWHITESPACE section as
 * the MANUALWHITESPACE token removes all whitespace and an HTML &nbsp; is
 * sometimes not desirable (as it doesn't naturally line break).
 */
public class SpaceToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "SPACE";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        return " ";
    }
}
