/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.util;

/**
 * This utility class is used by the various resource checkers to see if pcgen has needed classes.
 */
final class ResourceChecker
{

    private ResourceChecker()
    {
    }

    /**
     * Return TRUE if the resource exists in the jar
     *
     * @param resourceName
     * @param jarName
     * @param sb
     * @return TRUE if the resource exists in the jar
     */
    static boolean hasResource(final String resourceName, final String jarName, StringBuilder sb)
    {
        try
        {
            Class.forName(resourceName);
            return true;
        } catch (ClassNotFoundException cnfex)
        {
            sb.append("Missing resource: ").append(jarName).append('\n');
        } catch (NoClassDefFoundError ncdfer)
        {
            sb.append("Missing dependency of resource: ").append(jarName).append('\n');
            Logging.errorPrint("Error loading class " + resourceName + ": " + ncdfer, ncdfer);
        }
        return false;
    }

}
