/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

import java.io.File;
import java.net.URI;

import pcgen.cdom.base.Constants;

/**
 * Common I/O methods specific to files.
 */
public final class PCGFile
{
    /**
     * prevent instantiation of this utility class.
     */
    private PCGFile()
    {
    }

    /**
     * Checks if the given {@code file} is a PCGen character file based on the
     * file extension.
     *
     * @param file the file to test
     * @return {{@code true}} if the file exists and is a PCGen character file
     * @see Constants#EXTENSION_CHARACTER_FILE
     */
    public static boolean isPCGenCharacterFile(final File file)
    {
        // A directory strangely named "fred.pcg" is not a character file.
        if (!file.isFile())
        {
            return false;
        }

        return getWindowsSafeFilename(file).endsWith(Constants.EXTENSION_CHARACTER_FILE);
    }

    /**
     * Checks if the given {@code file} is a PCGen party file based on the file
     * extension.
     *
     * @param file the file to test
     * @return {{@code true}} if the file exists and is a PCGen party file
     * @see Constants#EXTENSION_PARTY_FILE
     */
    public static boolean isPCGenPartyFile(final File file)
    {
        // A directory strangely named "fred.pcp" is not a party file.
        if (!file.isFile())
        {
            return false;
        }

        return getWindowsSafeFilename(file).endsWith(Constants.EXTENSION_PARTY_FILE);
    }

    /**
     * Checks if the given {@code file} is a PCGen campaign file based on the
     * file extension.
     *
     * @param file the file to test
     * @return {{@code true}} if a PCGen campaign file
     * @see Constants#EXTENSION_CAMPAIGN_FILE
     */
    private static boolean isPCGenCampaignFile(final File file)
    {
        // A directory strangely named "fred.pcc" is not a campaign file.
        if (!file.isFile())
        {
            return false;
        }

        return getWindowsSafeFilename(file).endsWith(Constants.EXTENSION_CAMPAIGN_FILE);
    }

    /**
     * Checks if the given {@code uri} is a PCGen campaign file based on the
     * file extension.
     *
     * @param uri the uri to test
     * @return {{@code true}} if a PCGen campaign file
     * @see Constants#EXTENSION_CAMPAIGN_FILE
     */
    public static boolean isPCGenCampaignFile(final URI uri)
    {
        if ("file".equals(uri.getScheme()))
        {
            return isPCGenCampaignFile(new File(uri));
        }

        return uri.getPath() != null && uri.getPath().toLowerCase().endsWith(Constants.EXTENSION_CAMPAIGN_FILE);
    }

    /**
     * Checks if the given {@code file} is a PCGen list file based on the file
     * extension.
     *
     * @param file the file to test
     * @return {{@code true}} if a PCGen list file
     * @see Constants#EXTENSION_LIST_FILE
     */
    public static boolean isPCGenListFile(final File file)
    {
        // A directory strangely named "fred.lst" is not a list file.
        if (!file.isFile())
        {
            return false;
        }

        return getWindowsSafeFilename(file).endsWith(Constants.EXTENSION_LIST_FILE);
    }

    /**
     * Checks if the given {@code file} is a PCGen character or party file based
     * on the file extension.
     *
     * @param file the file to test
     * @return {{@code true}} if a PCGen character or party file
     * @see Constants#EXTENSION_CHARACTER_FILE
     * @see Constants#EXTENSION_PARTY_FILE
     */
    public static boolean isPCGenCharacterOrPartyFile(final File file)
    {
        // A directory strangely named "fred.pcg" is not a character file.
        if (file.isDirectory())
        {
            return false;
        }

        final String name = getWindowsSafeFilename(file);

        return name.endsWith(Constants.EXTENSION_CHARACTER_FILE) || name.endsWith(Constants.EXTENSION_PARTY_FILE);
    }

    /**
     * It may turn out to be the case that this should be optimized further to
     * pull out the extension, but there doesn't seem to be such high use of
     * filename checking to warrant further tinkering.
     *
     * @param file The filename to sanitise.
     * @return windows safe file name
     */
    private static String getWindowsSafeFilename(final File file)
    {
        return file.getName().toLowerCase();
    }
}
