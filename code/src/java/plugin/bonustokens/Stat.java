/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
package plugin.bonustokens;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.MissingObject;
import pcgen.rules.context.LoadContext;

import java.util.Objects;

/**
 * This is the class that implements the Stat bonuses.
 */
public final class Stat extends BonusObj
{
    private static final String[] BONUS_TAGS = {"BASESPELLSTAT", "BASESPELLKNOWNSTAT"};

    @Override
    protected boolean parseToken(LoadContext context, final String token)
    {
        for (int i = 0;i < BONUS_TAGS.length;++i)
        {
            if (BONUS_TAGS[i].equals(token))
            {
                addBonusInfo(i);
                return true;
            }
        }

        if (token.startsWith("CAST=") || token.startsWith("CAST."))
        {
            PCStat stat = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class,
                    token.substring(Constants.SUBSTRING_LENGTH_FIVE));

            if (stat != null)
            {
                addBonusInfo(new CastStat(stat));

                return true;
            }
        } else
        {
            PCStat stat = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, token);

            if (stat != null)
            {
                addBonusInfo(stat);
            } else
            {
                final PCClass aClass =
                        context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, token);

                addBonusInfo(Objects.requireNonNullElseGet(aClass, () -> new MissingObject(token)));
            }

            return true;
        }

        return false;
    }

    @Override
    protected String unparseToken(final Object obj)
    {
        if (obj instanceof Integer)
        {
            return BONUS_TAGS[(Integer) obj];
        } else if (obj instanceof CastStat)
        {
            return "CAST." + ((CastStat) obj).getStat().getKeyName();
        } else if (obj instanceof PCClass)
        {
            return ((PCClass) obj).getKeyName();
        } else if (obj instanceof MissingObject)
        {
            return ((MissingObject) obj).getObjectName();
        }

        return ((PCStat) obj).getKeyName();
    }

    /**
     * Deals with the Stat for casting.
     */
    public static class CastStat
    {
        private final PCStat stat;

        /**
         * Constructor.
         *
         * @param argStat The spell casting stat.
         */
        public CastStat(final PCStat argStat)
        {
            stat = argStat;
        }

        /**
         * Get the spell casting stat.
         *
         * @return The spell casting stat.
         */
        public PCStat getStat()
        {
            return stat;
        }
    }

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "STAT";
    }

    @Override
    public String getDescription()
    {
        final PCStat pcstat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class,
                getBonusInfo());
        if (pcstat != null)
        {
            return pcstat.getDisplayName();
        }
        return super.getDescription();
    }

}
