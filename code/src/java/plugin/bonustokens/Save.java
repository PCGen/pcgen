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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.PCCheck;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class Save extends BonusObj
{
    @Override
    protected boolean parseToken(LoadContext context, final String argToken)
    {
        if (ControlUtilities.hasControlToken(context, CControl.TOTALSAVE))
        {
            Logging.errorPrint("BONUS:SAVE is disabled when TOTALSAVE code control is used: " + argToken, context);
            return false;
        }
        boolean isBase = false;
        final String token;

        if (argToken.startsWith("BASE."))
        {
            token = argToken.substring(Constants.SUBSTRING_LENGTH_FIVE);
            isBase = true;
        } else
        {
            token = argToken;
        }

        if ("%LIST".equals(token))
        {
            // Special case of:  BONUS:SAVE|%LIST|x
            addBonusInfo(listCheck);
        } else if ("ALL".equals(token))
        {
            // Special case of:  BONUS:SAVE|ALL|x
            /*
             * TODO Prohibit use in Game Mode, or alternately, all areas where
             * SAVE are established need to test both SAVE|Blah and
             * SAVE|ALL
             */
            for (PCCheck check : context.getReferenceContext().getConstructedCDOMObjects(PCCheck.class))
            {
                addBonusInfo(new CheckInfo(CDOMDirectSingleRef.getRef(check), isBase));
            }
        } else
        {
            CDOMReference<PCCheck> aCheck = context.getReferenceContext().getCDOMReference(PCCheck.class, token);
            //Invalid name is caught by Unconstructed Reference system
            addBonusInfo(new CheckInfo(aCheck, isBase));
        }

        return true;
    }

    /**
     * Unparse the bonus token.
     *
     * @param obj The object to unparse
     * @return The unparsed string.
     */
    @Override
    protected String unparseToken(final Object obj)
    {

        if (obj.equals(listCheck))
        {
            return Constants.LST_PERCENT_LIST;
        }

        String token = ((CheckInfo) obj).isBase() ? "BASE." : Constants.EMPTY_STRING;

        return token + ((CheckInfo) obj).getPobj().getLSTformat(false);
    }

    /**
     * Deals with the CheckInfo.
     */
    public static class CheckInfo
    {
        private final CDOMReference<PCCheck> pobj;

        private final boolean isBase;

        /**
         * Constructor.
         *
         * @param argPobj   The PObject.
         * @param argIsBase Whether this is a base check.
         */
        public CheckInfo(final CDOMReference<PCCheck> argPobj, final boolean argIsBase)
        {
            pobj = argPobj;
            isBase = argIsBase;
        }

        /**
         * The PObject.
         *
         * @return the stored PObject.
         */
        public CDOMReference<PCCheck> getPobj()
        {
            return pobj;
        }

        /**
         * Whether this is a base check.
         *
         * @return True or False.
         */
        public boolean isBase()
        {
            return isBase;
        }
    }

    private static CheckInfo listCheck = new CheckInfo(null, false);

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "SAVE";
    }
}
