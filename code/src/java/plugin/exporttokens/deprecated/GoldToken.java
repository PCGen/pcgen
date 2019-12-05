/*
 * GoldToken.java
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
package plugin.exporttokens.deprecated;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Handle the GOLD token which outputs the amount of unallocated wealth
 * that the character has.
 */
public class GoldToken extends Token
{
    @Override
    public String getTokenName()
    {
        return "GOLD";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        NumberFormat decimalFormat = new DecimalFormat("#,##0.##", decimalFormatSymbols);
        return decimalFormat.format(getGoldToken(pc));
    }

    /**
     * Retrieve the amount of money that the character has.
     *
     * @param pc The character to be queried.
     * @return The amount of gold
     */
    public static BigDecimal getGoldToken(PlayerCharacter pc)
    {
        return pc.getGold();
    }
}
