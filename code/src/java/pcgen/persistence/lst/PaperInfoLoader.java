/*
 * PaperInfoLoader.java
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
 * Created on February 27, 2002, 7:42 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
final class PaperInfoLoader extends LstLineFileLoader
{
	/** Creates a new instance of PaperInfoLoader. */
	public PaperInfoLoader()
	{
		// Empty Constructor
	}

	@Override
	public void loadLstFile(LoadContext context, URI fileName) throws PersistenceLayerException
	{
		// We cannot clear the global list as we are only setting one 
		// game mode at a time now.
		//SystemCollections.clearPaperInfoList();
		super.loadLstFile(context, fileName);
		Globals.selectPaper(SettingsHandler.getPCGenOption("paperName", "A4"));
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		final PaperInfo psize = new PaperInfo();

		final StringTokenizer colToken = new StringTokenizer(lstLine, "\t");
		int iCount = 0;

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PaperInfoLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// TODO Handle Exception
			}
			PaperInfoLstToken token = (PaperInfoLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, psize.toString(), sourceURI, value);
				if (!token.parse(psize, value))
				{
					Logging.errorPrint("Error parsing equip slots "
						+ psize.toString() + ':' + sourceURI + ':' + colString
						+ "\"");
				}
			}
			else
			{
				LstUtils
					.deprecationWarning("Using deprecated style of paperinfo.lst.  Please consult the docs for information about the new style paperinfo.lst");
				try
				{
					psize.setPaperInfo(iCount, colString);
				}
				catch (IndexOutOfBoundsException e)
				{
					Logging.errorPrint("Illegal paper size info '" + lstLine
						+ "' in " + sourceURI.toString());
				}

				iCount += 1;
			}
		}

		SystemCollections.addToPaperInfoList(psize, gameMode);
	}
}
