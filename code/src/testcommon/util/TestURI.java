/*
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
package util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This is a URI for use across the test classes
 */
public final class TestURI
{
    /**
     * The (lazily instantiated) Test URI
     */
    private static URI uri;

    private TestURI()
    {
        //Private for Utility Class
    }

    /**
     * Returns the test URI.
     *
     * @return The test URI
     */
    public static URI getURI()
    {
        if (uri == null)
        {
            try
            {
                uri = new URI("file:/TESTCASE");
            } catch (URISyntaxException e)
            {
                throw new IllegalStateException(e);
            }
        }
        return uri;
    }

}
