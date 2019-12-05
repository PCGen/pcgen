/**
 * IconToken.java
 * Copyright James Dempsey, 2011
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.EquipIconLstToken;
import pcgen.util.Logging;

/**
 * The Class {@code IconToken} is responsible for matching icon
 * paths to equipment types.
 */
public class IconToken implements EquipIconLstToken
{

    @Override
    public String getTokenName()
    {
        return "ICON";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE, false);

        if (aTok.countTokens() < 2)
        {
            Logging.log(Logging.LST_ERROR,
                    getTokenName() + " expecting '|', format is: " + "EquipType|IconPath was: " + value);
            return false;
        }

        if (aTok.countTokens() > 3)
        {
            Logging.log(Logging.LST_ERROR,
                    getTokenName() + " too many '|', format is: " + "EquipType|IconPath|Priority was: " + value);
            return false;
        }

        final String equipType = aTok.nextToken();
        final String iconPath = aTok.nextToken();
        int priority = 10;
        if (aTok.hasMoreElements())
        {
            String priorityToken = aTok.nextToken();
            try
            {
                priority = Integer.parseInt(priorityToken);
            } catch (NumberFormatException ex)
            {
                Logging.log(Logging.LST_ERROR,
                        getTokenName() + " expected an integer priority .  Found: " + priorityToken + " in " + value);
                return false;
            }

        }

        gameMode.setEquipTypeIcon(equipType.intern(), iconPath.intern(), priority);
        return true;
    }

}
