/*
 * VarToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * {@code VarToken} produces the output for the output token VAR.
 * Possible tag formats are:<pre>
 * VAR.x
 * VAR.x.INTVAL|.MINVAL|.NOSIGN
 * </pre>
 */
public class VarToken extends Token
{
    /**
     * The name of the token handled by this class.
     */
    public static final String TOKENNAME = "VAR";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        boolean isMin = tokenSource.lastIndexOf(".MINVAL") >= 0;
        boolean isInt = tokenSource.lastIndexOf(".INTVAL") >= 0;
        boolean isSign = (tokenSource.lastIndexOf(".SIGN") >= 0);
        if (tokenSource.lastIndexOf(".NOSIGN") >= 0)
        {
            isSign = false;
            Logging.errorPrint(".NOSIGN in output token " + tokenSource + " is deprecated. "
                    + "The default output format is unsigned.");
        }

        String workingSource = tokenSource;
        // clear out the gunk
        if (isMin)
        {
            workingSource = workingSource.replaceAll(".MINVAL", "");
        }
        if (isInt)
        {
            workingSource = workingSource.replaceAll(".INTVAL", "");
        }
        workingSource = workingSource.replaceAll(".NOSIGN", "");
        workingSource = workingSource.replaceAll(".SIGN", "");

        StringTokenizer aTok = new StringTokenizer(workingSource, ".");
        aTok.nextToken(); //this should be VAR

        StringBuilder varName = new StringBuilder();

        if (aTok.hasMoreElements())
        {
            varName.append(aTok.nextToken());
        }
        while (aTok.hasMoreElements())
        {
            varName.append('.').append(aTok.nextToken());
        }

        if (isInt)
        {
            if (isSign)
            {
                return Delta.toString(pc.getVariable(varName.toString(), !isMin).intValue());
            }
            return String.valueOf(pc.getVariable(varName.toString(), !isMin).intValue());
        }
        if (isSign)
        {
            return Delta.toString((float) pc.getVariable(varName.toString(), !isMin));
        }
        return String.valueOf(pc.getVariable(varName.toString(), !isMin));
    }
}
