/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;

import pcgen.core.InstallableCampaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * {@code InstallLoader} handles parsing the Install.lst file which
 * defines how a data set should be installed into an existing PCGen
 * installation.
 */
public class InstallLoader extends LstLineFileLoader
{
    private InstallableCampaign campaign = null;

    @Override
    public void loadLstString(LoadContext context, URI fileName, String lstData) throws PersistenceLayerException
    {
        campaign = new InstallableCampaign();
        campaign.setSourceURI(fileName);
        super.loadLstString(context, fileName, lstData);
    }

    @Override
    public void parseLine(LoadContext context, String inputLine, URI sourceURI)
    {
        final int idxColon = inputLine.indexOf(':');
        if (idxColon < 0)
        {
            Logging.errorPrint("Unparsed line: " + inputLine + " in " + sourceURI.toString());
            return;
        }
        final String key = inputLine.substring(0, idxColon);
        final String value = inputLine.substring(idxColon + 1);
        Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(InstallLstToken.class);
        InstallLstToken token = (InstallLstToken) tokenMap.get(key);

        if (token != null)
        {
            LstUtils.deprecationCheck(token, campaign, value);
            if (!token.parse(campaign, new String(value), sourceURI))
            {
                Logging.errorPrint("Error parsing install " + campaign.getDisplayName() + ':' + inputLine);
            }
        } else
        {
            Logging.errorPrint("Unparsed line: " + inputLine + " in " + sourceURI.toString());
        }
    }

    public InstallableCampaign getCampaign()
    {
        return campaign;
    }
}
