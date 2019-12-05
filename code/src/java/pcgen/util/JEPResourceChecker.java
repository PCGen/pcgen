/*
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 *
 */
package pcgen.util;

/**
 * {@code JEPResourceChecker}
 */
public final class JEPResourceChecker
{
    private static int missingResourceCount;
    private static StringBuilder resourceBuffer;

    static
    {
        missingResourceCount = 0;

        //optimize StringBuilder initial size (0 should be right length. Hopefully we don't get an error. :)
        resourceBuffer = new StringBuilder(0);
        checkResource();
    }

    private JEPResourceChecker()
    {
    }

    /**
     * Get the number of missing resources
     *
     * @return the number of missing resources
     */
    public static int getMissingResourceCount()
    {
        return missingResourceCount;
    }

    private static void checkResource()
    {
        if (!ResourceChecker.hasResource("org.nfunk.jep.JEP", "jep-2.3.1.jar", resourceBuffer))
        {
            ++missingResourceCount;
        }
    }
}
