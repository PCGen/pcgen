/*
 * Copyright 2016-18 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.formula;

/**
 * AssociationUtilities is a set of utility functions related to associations (placed on
 * formulas). The most common of these associations is PRIORITY=x
 */
public final class AssociationUtilities
{
    private AssociationUtilities()
    {
        //Do not instantiate utility class
    }

    /**
     * Processes the user priority (PRIORITY=x) from the given String. Note this assumes
     * the String is well-structured, meaning starts with "PRIORITY=" and is followed by
     * an integer >= 0.
     * <p>
     * If the input String is not well structured, and exception will be thrown.
     */
    public static int processUserPriority(String assocInstructions)
    {
        int equalLoc = assocInstructions.indexOf("=");
        String assocName = assocInstructions.substring(0, equalLoc);
        String assocValue = assocInstructions.substring(equalLoc + 1);
        if ("PRIORITY".equalsIgnoreCase(assocName))
        {
            int priorityNumber;
            try
            {
                priorityNumber = Integer.parseInt(assocValue);
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException(
                        "Priority must be an integer: " + assocValue + " was not an integer", e);
            }
            if (priorityNumber < 0)
            {
                throw new IllegalArgumentException(
                        "Priority must be an integer >= 0. " + priorityNumber + " was not positive");
            }
            return priorityNumber;
        }
        throw new IllegalArgumentException("Association " + assocName + " not recognized");
    }

    /**
     * Unprocesses the user priority from the given user priority value. This reverses the
     * processing done in processUserPriority(String).
     */
    public static String unprocessUserPriority(int userPriority)
    {
        return "PRIORITY=" + userPriority;
    }

}
