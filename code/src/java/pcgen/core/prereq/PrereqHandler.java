/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.core.prereq;

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * This class tests if the character passes the prerequisites for the caller.
 */
public final class PrereqHandler
{

    /**
     * empty private constructor prevents instantiation.
     */
    private PrereqHandler()
    {
    }

    /**
     * Test if the character passes the prerequisites for the caller. The caller
     * is used to check if prereqs can be bypassed by either preferences or via
     * Qualifies statements in templates or other objects applied to the
     * character.
     *
     * @param prereqList The list of prerequisites to be tested.
     * @param aPC        The character to be checked.
     * @param caller     The object that we are testing qualification for.
     * @return True if the character passes all prereqs.
     */
    public static boolean passesAll(final Collection<Prerequisite> prereqList, final PlayerCharacter aPC,
            final Object caller)
    {
        if (prereqList == null || prereqList.isEmpty())
        {
            return true;
        }

        if ((caller instanceof Ability) && (AbilityUtilities.isFeat(caller))
                && Globals.checkRule(RuleConstants.FEATPRE))
        {
            return true;
        }

        if (caller instanceof CDOMObject && aPC != null)
        {
            // Check for QUALIFY:
            if (aPC.checkQualifyList((CDOMObject) caller))
            {
                return true;
            }
        }

        for (Prerequisite prereq : prereqList)
        {
            if (!passes(prereq, aPC, caller))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean passesAll(PrereqObject prereqObject, PlayerCharacter aPC, Object caller)
    {
        if (!prereqObject.isAvailable(aPC) || !prereqObject.isActive(aPC))
        {
            return false;
        }
        Collection<Prerequisite> prereqList = prereqObject.getPrerequisiteList();
        if (prereqList == null || prereqList.isEmpty())
        {
            return true;
        }

        if ((caller instanceof Ability) && (AbilityUtilities.isFeat(caller))
                && Globals.checkRule(RuleConstants.FEATPRE))
        {
            return true;
        }

        if (caller instanceof CDOMObject && aPC != null)
        {
            // Check for QUALIFY:
            if (aPC.checkQualifyList((CDOMObject) caller))
            {
                return true;
            }
        }

        for (Prerequisite prereq : prereqList)
        {
            if (!passes(prereq, aPC, caller))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param prereqList The list of prerequisites to be tested.
     * @param equip      The Equipment that is the source of the prerequisite.
     * @param aPC        The character to be checked.
     * @return true if all of the prerequisites pss.
     */
    public static boolean passesAll(final Collection<Prerequisite> prereqList, final Equipment equip,
            PlayerCharacter aPC)
    {
        if (prereqList == null)
        {
            return true;
        }
        for (Prerequisite prereq : prereqList)
        {
            if (!passes(prereq, equip, aPC))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean passesAll(PrereqObject prereqObject, Equipment equip, PlayerCharacter aPC)
    {
        List<Prerequisite> prereqList = prereqObject.getPrerequisiteList();
        if (prereqList == null)
        {
            return true;
        }
        for (Prerequisite prereq : prereqList)
        {
            if (!passes(prereq, equip, aPC))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the character passes the prereq.
     *
     * @param prereq The prerequisite to test.
     * @param aPC    The character to test against
     * @param caller The CDOMObject that is calling this method
     * @return true if the character passes the prereq
     */
    public static boolean passes(final Prerequisite prereq, final PlayerCharacter aPC, final Object caller)
    {
        if (aPC == null && prereq.isCharacterRequired())
        {
            return true;
        }
        final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
        final PrerequisiteTest test = factory.getTest(prereq.getKind());

        if (test == null)
        {
            Logging.errorPrintLocalised("PrereqHandler.Unable_to_find_implementation", prereq.toString()); //$NON-NLS-1$
            return false;
        }

        final boolean overrideQualify = prereq.isOverrideQualify();
        boolean autoQualifies = false;
        int total = 0;

        if (caller instanceof CDOMObject && aPC != null && aPC.checkQualifyList((CDOMObject) caller)
                && (!overrideQualify))
        {
            autoQualifies = true;
        }
        if (autoQualifies)
        {
            return true;
        }
        try
        {
            CDOMObject cdomCaller = (caller instanceof CDOMObject) ? (CDOMObject) caller : null;
            total = test.passes(prereq, aPC, cdomCaller);
        } catch (PrerequisiteException pe)
        {
            Logging.errorPrintLocalised("PrereqHandler.Exception_in_test", pe); //$NON-NLS-1$
        } catch (Exception e)
        {
            final String callerString = (caller != null) ? " for " + String.valueOf(caller) : Constants.EMPTY_STRING;

            Logging.errorPrint("Problem encountered when testing PREREQ " + String.valueOf(prereq) + callerString
                    + ". See following trace for details.", e);
        }
        return total > 0;
    }

    /**
     * @param preReq The prerequisite to test.
     * @param equip  The Equipment that is the source of the prerequisite.
     * @param aPC    The character to be checked.
     * @return Whether the prerequisite passes.
     */
    public static boolean passes(final Prerequisite preReq, final Equipment equip, PlayerCharacter aPC)
    {
        if (equip == null)
        {
            return true;
        }
        final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
        final PrerequisiteTest test = factory.getTest(preReq.getKind());

        if (test == null)
        {
            final String message = "PrereqHandler.Unable_to_find_implementation"; //$NON-NLS-1$
            Logging.errorPrintLocalised(message, preReq.toString());
            return false;
        }
        int total = 0;
        try
        {
            total = test.passes(preReq, equip, aPC);
        } catch (PrerequisiteException pe)
        {
            final String message = "PrereqHandler.Exception_in_test"; //$NON-NLS-1$
            Logging.errorPrintLocalised(message, pe);
        }
        return total > 0;
    }

    /**
     * Generates an HTML representation of a list of PreRequisite objects.
     *
     * @param anArrayList the list of PreRequisite objects to be represented.
     * @return An HTML representation of the input.
     */
    public static String toHtmlString(final Collection<Prerequisite> anArrayList)
    {
        if (anArrayList == null || anArrayList.isEmpty())
        {
            return Constants.EMPTY_STRING;
        }

        final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();

        final StringBuilder pString = new StringBuilder(anArrayList.size() * 20);

        String delimiter = Constants.EMPTY_STRING;

        for (Prerequisite preReq : anArrayList)
        {
            final PrerequisiteTest preReqTest = factory.getTest(preReq.getKind());

            if (preReqTest == null)
            {
                final String message = "PrereqHandler.No_known_formatter"; //$NON-NLS-1$
                Logging.errorPrintLocalised(message, preReq.getKind());
            } else
            {
                pString.append(delimiter);
                pString.append(preReqTest.toHtmlString(preReq));

                final String property = "PrereqHandler.HTML_prerequisite_delimiter"; //$NON-NLS-1$
                delimiter = LanguageBundle.getString(property);
            }
        }

        return pString.toString();
    }

}
