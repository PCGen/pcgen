/*
 * InstallLoader.java
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
 * Created on 26/12/2007
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import pcgen.core.InstallableCampaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * <code>InstallLoader</code> handles parsing the Install.lst file which 
 * defines how a data set should be installed into an existing PCGen 
 * installation.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class InstallLoader extends LstLineFileLoader
{
	private InstallableCampaign campaign = null;
	private Map<String, String> sourceMap = new HashMap<String, String>();

	/**
	 * Creates a new instance of InstallLoader
	 */
	public InstallLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#loadLstFile(java.net.URI)
	 */
	@Override
	public void loadLstString(URI fileName, String lstData) throws PersistenceLayerException
	{
		campaign = new InstallableCampaign();

		super.loadLstString(fileName, lstData);

		finishCampaign();
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URI)
	 */
	@Override
	public void parseLine(String inputLine, URI sourceURI)
		throws PersistenceLayerException
	{
		final int idxColon = inputLine.indexOf(':');
		if (idxColon < 0)
		{
			Logging.errorPrint("Unparsed line: " + inputLine + " in "
				+ sourceURI.toString());
			return;
		}
		final String key = inputLine.substring(0, idxColon);
		final String value = inputLine.substring(idxColon + 1);
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(InstallLstToken.class);
		InstallLstToken token = (InstallLstToken) tokenMap.get(key);

		if (token != null)
		{
			LstUtils.deprecationCheck(token, campaign, value);
			if (!token.parse(campaign, value, sourceURI))
			{
				Logging.errorPrint("Error parsing install "
					+ campaign.getDisplayName() + ':' + inputLine);
			}
		}
		else if (inputLine.startsWith("SOURCE")) //$NON-NLS-1$
		{
			sourceMap.putAll(SourceLoader.parseLine(inputLine, sourceURI));
		}
		else
		{
			Logging.errorPrint("Unparsed line: " + inputLine + " in "
				+ sourceURI.toString());
		}
	}

	/**
	 * This method finishes the campaign being loaded by saving its section 15
	 * information as well as adding it to Globals, if it has not already been
	 * loaded.
	 */
	protected void finishCampaign() throws PersistenceLayerException
	{
		if (sourceMap != null)
		{
			try
			{
				campaign.setSourceMap(sourceMap);
			}
			catch (ParseException e)
			{
				throw new PersistenceLayerException(e.toString());
			}
		}
	}
	
	public InstallableCampaign getCampaign()
	{
		return campaign;
	}
}
