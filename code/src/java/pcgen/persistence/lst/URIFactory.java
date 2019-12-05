/*
 * derived from
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
package pcgen.persistence.lst;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import pcgen.base.lang.UnreachableError;
import pcgen.core.utils.CoreUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * Constructs a URI from a base URI and an offset, where the offset is defined
 * by the rules available in PCGen.
 */
public class URIFactory
{

    /**
     * A static URI to indicate a failure in creating a URI
     */
    public static final URI FAILED_URI;

    static
    {
        try
        {
            FAILED_URI = new URI("file:/FAIL");
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
    }

    /**
     * Stores the root URI used as the starting point to determine the final URI
     */
    private final URI rootURI;

    /**
     * Stores the offset from the root URI
     */
    private final String offset;

    /**
     * Constructs a new URIFactory with the given root URI and offset
     *
     * @param root   The root URI used as the starting point to determine the final
     *               URI
     * @param offset The offset from the root URI
     */
    public URIFactory(URI root, String offset)
    {
        Objects.requireNonNull(root, "root URI cannot be null");
        if (offset == null || offset.isEmpty())
        {
            throw new IllegalArgumentException("URI offset cannot be null");
        }
        rootURI = root;
        this.offset = offset;
    }

    /**
     * Returns the root URI for this URIFactory.
     *
     * @return The root URI for this URIFactory
     */
    public URI getRootURI()
    {
        return rootURI;
    }

    /**
     * Returns the offset for this URIFactory.
     *
     * @return The offset for this URIFactory
     */
    public String getOffset()
    {
        return offset;
    }

    /**
     * Returns the normalized URI resulting from the root URI and offset of this
     * URIFactory.
     *
     * @return The normalized URI resulting from the root URI and offset of this
     * URIFactory
     */
    public URI getURI()
    {
        return getNonNormalizedPathURI(rootURI, offset).normalize();
    }

    @Override
    public int hashCode()
    {
        return offset.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof URIFactory)
        {
            URIFactory other = (URIFactory) o;
            return offset.equals(other.offset) && rootURI.equals(other.rootURI);
        }
        return false;
    }

    /**
     * This method converts the provided filePath to either a URL or absolute
     * path as appropriate.
     *
     * @param pccPath  URL where the Campaign that contained the source was at
     * @param basePath String path that is to be converted
     * @return String containing the converted absolute path or URL (as
     * appropriate)
     */
    private static URI getNonNormalizedPathURI(URI pccPath, String basePath)
    {
        if (basePath.length() <= 0)
        {
            Logging.errorPrint("Empty Path to LST file in " + pccPath);
            return FAILED_URI;
        }

        /*
         * Figure out where the PCC file came from that we're processing, so
         * that we can prepend its path onto any LST file references (or PCC
         * refs, for that matter) that are relative. If the source line in
         * question already has path info, then don't bother
         */
        if (basePath.charAt(0) == '@')
        {
            String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
            String path = CoreUtility.fixFilenamePath(pathNoLeader);
            return new File(ConfigurationSettings.getPccFilesDir(), path).toURI();
        } else if (basePath.charAt(0) == '&')
        {
            String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
            String path = CoreUtility.fixFilenamePath(pathNoLeader);
            return new File(PCGenSettings.getVendorDataDir(), path).toURI();
        } else if (basePath.charAt(0) == '$')
        {
            String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
            String path = CoreUtility.fixFilenamePath(pathNoLeader);
            return new File(PCGenSettings.getHomebrewDataDir(), path).toURI();
        } else if (basePath.charAt(0) == '*')
        {
            String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
            String path = CoreUtility.fixFilenamePath(pathNoLeader);
            File pccFile = new File(PCGenSettings.getHomebrewDataDir(), path);
            if (pccFile.exists())
            {
                return pccFile.toURI();
            }
            pccFile = new File(PCGenSettings.getVendorDataDir(), path);
            if (pccFile.exists())
            {
                return pccFile.toURI();
            }
            return new File(ConfigurationSettings.getPccFilesDir(), path).toURI();
        } else if (basePath.indexOf(':') > 0)
        {
            try
            {
                // if it's a URL, then we are all done, just return a URI
                URL url = new URL(basePath);
                return new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
            } catch (URISyntaxException | MalformedURLException e)
            {
                //Something broke, so wasn't a URL
            }
        }

        /*
         * At this point, basePath is a relative path
         */
        String pathNoLeader = trimLeadingFileSeparator(basePath);

        //BUG This captures files that start with "data" :(
        /*
         * 1) If the path starts with '/data', assume it means the PCGen data
         * dir
         */
        if (pathNoLeader.startsWith("data"))
        {
            // substring 5 to eliminate the separator after data
            String path = CoreUtility.fixFilenamePath(pathNoLeader.substring(5));
            return new File(ConfigurationSettings.getPccFilesDir(), path).toURI();
        }

        /*
         * 2) Otherwise, assume that the path is relative to the current PCC
         * file URL
         */
        String path = pccPath.getPath();
        // URLs always use forward slash; take off the file name
        try
        {
            return new URI(pccPath.getScheme(), null,
                    (path.substring(0, path.lastIndexOf('/') + 1) + basePath.replace('\\', '/')), null);
        } catch (URISyntaxException e)
        {
            Logging.errorPrint("URIFactory failed to convert " + path.substring(0, path.lastIndexOf('/') + 1) + basePath
                    + " to a URI: " + e.getLocalizedMessage());
        }
        return FAILED_URI;
    }

    /**
     * This method trims the leading file separator or URL separator from the
     * front of a string.
     *
     * @param basePath String containing the base path to trim
     * @return String containing the trimmed path String
     */
    private static String trimLeadingFileSeparator(String basePath)
    {
        String pathNoLeader = basePath;

        if (pathNoLeader.startsWith("/") || pathNoLeader.startsWith(File.separator))
        {
            pathNoLeader = pathNoLeader.substring(1);
        }

        return pathNoLeader;
    }

}
