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
 *
 */
package pcgen.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.filters.CharacterFilter;
import pcgen.io.filters.OutputFilter;
import pcgen.io.filters.PatternFilter;

/**
 * {@code FileAccess}.
 */
public final class FileAccess
{
    private static OutputFilter outputFilter = null;
    private static int maxLength = -1;

    private FileAccess()
    {
    }

    /**
     * Filter the supplied string according to the current output filter. This
     * can do things such as escaping HTML entities.
     *
     * @param aString The string to be filtered
     * @return The filtered string.
     */
    public static String filterString(String aString)
    {
        String outputString = aString;
        if (outputFilter != null)
        {
            outputString = outputFilter.filterString(aString);
        }
        return outputString;
    }

    /**
     * Set the current output filter. The current output filter will be
     * chosen based on the extension of the passed in template file name.
     *
     * @param templateFilename (used to create instance of CharacterFilter)
     */
    public static void setCurrentOutputFilter(String templateFilename)
    {
        try
        {
            outputFilter = new PatternFilter(templateFilename);
        } catch (IOException e)
        {
            outputFilter = new CharacterFilter(templateFilename);
        }
    }

    /**
     * Set the current output filter
     *
     * @param filter
     */
    public static void setCurrentOutputFilter(OutputFilter filter)
    {
        outputFilter = filter;
    }

    /**
     * Write, but with encoding
     *
     * @param output
     * @param aString
     */
    public static void encodeWrite(Writer output, String aString)
    {
        write(output, filterString(aString));
    }

    /**
     * Set the max length
     *
     * @param anInt
     */
    public static void maxLength(int anInt)
    {
        maxLength = anInt;
    }

    /**
     * Write a newline
     *
     * @param output
     */
    public static void newLine(BufferedWriter output)
    {
        try
        {
            output.newLine();
        } catch (IOException exception)
        {
            ShowMessageDelegate.showMessageDialog(exception.getMessage(), Constants.APPLICATION_NAME,
                    MessageType.ERROR);
        }
    }

    /**
     * Write to the output
     *
     * @param output
     * @param aString
     */
    public static void write(Writer output, String aString)
    {

        String outputString = aString;

        // If there is nothing to write, then return gracefully
        if (aString == null)
        {
            return;
        }

        // Trim the string to the length of maxLength
        if ((maxLength > 0) && (aString.length() > maxLength))
        {
            outputString = aString.substring(0, maxLength);
        }

        try
        {
            output.write(outputString);
        } catch (IOException exception)
        {
            ShowMessageDelegate.showMessageDialog(exception.getMessage(), Constants.APPLICATION_NAME,
                    MessageType.ERROR);
        }
    }
}
