/*
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
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.StringTokenizer;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Deals with BONUS token
 */
public class BonusToken extends Token
{
    /**
     * Name of token
     */
    public static final String TOKENNAME = "BONUS";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    /**
     * TODO: Bonuses need to be stripped out, and there need to be methods for the various types.
     */
    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        return getBonusToken(tokenSource, pc);
    }

    /**
     * Get the bonus token
     *
     * @param tokenSource
     * @param pc
     * @return bonus token
     */
    public static String getBonusToken(String tokenSource, PlayerCharacter pc)
    {
        StringTokenizer bonusTok = new StringTokenizer(tokenSource, ".", false);

        // tokenSource should follow this format:
        //  BONUS.COMBAT.AC.TOTAL
        // or
        //  BONUS.COMBAT.AC.Armor
        // or
        //  BONUS.COMBAT.AC.TOTAL.!BASE.!Armor.!Ability.!Size
        // First token should be: BONUS
        bonusTok.nextToken();

        // next should be category of bonus: COMBAT
        final String aType = bonusTok.nextToken();

        // next should be name of bonus: AC
        final String aName = bonusTok.nextToken();

        double total = 0;
        int decimals = 0;
        double lastValue = 0;
        int signIt = 1;

        while (bonusTok.hasMoreTokens())
        {
            String bucket = bonusTok.nextToken();

            if (CoreUtility.doublesEqual(total, 0.0) && "LISTING".equals(bucket))
            {
                return pc.listBonusesFor(aType, aName);
            }
            if (PreParserFactory.isPreReqString(bucket))
            {
                if (Logging.isDebugMode())
                {
                    Logging.debugPrint(
                            "Why is this not parsed in loading: " + bucket + " rather than in BonusToken.getBonusToken()");
                }
                Prerequisite prereq = null;
                try
                {
                    PreParserFactory factory = PreParserFactory.getInstance();
                    prereq = factory.parse(bucket);
                } catch (PersistenceLayerException ple)
                {
                    Logging.errorPrint(ple.getMessage(), ple);
                }

                if (!PrereqHandler.passes(prereq, pc, null))
                {
                    total -= lastValue * signIt;
                    lastValue = 0;
                }

                continue;
            }

            if (bucket.startsWith("MIN="))
            {
                double x = Float.parseFloat(bucket.substring(4));

                if (lastValue < x)
                {
                    total -= (lastValue - x);
                }

                continue;
            } else if (bucket.startsWith("MAX="))
            {
                double x = Float.parseFloat(bucket.substring(4));
                x = Math.min(x, lastValue);
                total -= (lastValue - x);
                lastValue = 0;

                continue;
            }

            signIt = 1;

            if ((!bucket.isEmpty()) && (bucket.charAt(0) == '!'))
            {
                signIt = -1;
                bucket = bucket.substring(1);
            }

            if (bucket.equals("EQTYPE") && bonusTok.hasMoreTokens())
            {
                bucket += ("." + bonusTok.nextToken());
            }

            if ("TOTAL".equals(bucket))
            {
                lastValue = pc.getTotalBonusTo(aType, aName);
            } else if (bucket.startsWith("DEC="))
            {
                decimals = Integer.parseInt(bucket.substring(4));
            } else if (bucket.startsWith("TYPE=") || bucket.startsWith("EQTYPE."))
            {
                lastValue = 0;

                String restOfBucket;

                if (bucket.startsWith("TYPE="))
                {
                    restOfBucket = bucket.substring(5);
                } else
                {
                    restOfBucket = bucket.substring(7);
                }

                for (Equipment eq : pc.getEquipmentOfType(restOfBucket, "", 1))
                {
                    lastValue += eq.bonusTo(pc, aType, aName, true);
                }
            } else
            {
                lastValue = pc.getBonusDueToType(aType, aName, bucket);
            }

            total += (lastValue * signIt);
        }

        return String.valueOf((int) (total * Math.pow(10, decimals)) / (int) Math.pow(10, decimals));
    }
}
