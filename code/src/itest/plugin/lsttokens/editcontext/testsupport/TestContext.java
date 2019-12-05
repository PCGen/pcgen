/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.editcontext.testsupport;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.persistence.lst.CampaignSourceEntry;

public class TestContext
{

    private Map<URI, List<String>> map = new HashMap<>();

    public void putText(URI testCampaign, String... str)
    {
        map.put(testCampaign, str == null ? null : Arrays.asList(str));
    }

    public List<String> getText(URI cse)
    {
        return map.get(cse);
    }

    public int getSize()
    {
        return map.size();
    }

    /*
     * TODO Today this is LinkedHashMap to preserve order; but that shouldn't be
     * necessary.
     */
    private Map<URI, CampaignSourceEntry> cm =
            new LinkedHashMap<>();

    public void putCampaign(URI uri, CampaignSourceEntry testCampaign)
    {
        cm.put(uri, testCampaign);
    }

    public CampaignSourceEntry getCampaign(URI uri)
    {
        return cm.get(uri);
    }

    public Set<URI> getURIs()
    {
        return cm.keySet();
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + map.toString();
    }
}
