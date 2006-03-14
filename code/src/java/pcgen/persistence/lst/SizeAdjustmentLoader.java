/*
 * SizeAdjustmentLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
final class SizeAdjustmentLoader extends LstLineFileLoader
{
	/** Prevent creation of a new instance of SizeAdjustmentLoader */
	public SizeAdjustmentLoader()
	{
	    // TODO: Exception needs to be handled
	}

	public void loadLstFile(String fileName, String gameModeIn) throws PersistenceLayerException
	{
		SystemCollections.getGameModeNamed(gameModeIn).clearSizeAdjustmentList();
		super.loadLstFile(fileName, gameModeIn);
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		SizeAdjustment sa = new SizeAdjustment();

		lstLine.trim();
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(SizeAdjustmentLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			SizeAdjustmentLstToken token = (SizeAdjustmentLstToken) tokenMap.get(key);

			if (colString.startsWith("SIZENAME:"))
			{
				final String value = colString.substring(idxColon + 1);
				sa = SystemCollections.getGameModeNamed(getGameMode()).getSizeAdjustmentNamed(value);

				if (sa == null)
				{
					sa = new SizeAdjustment();
					sa.setName(value);
					SystemCollections.getGameModeNamed(getGameMode()).addToSizeAdjustmentList(sa);
				}
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, sa, value);
				if (!token.parse(sa, value))
				{
					Logging.errorPrint("Error parsing size adjustment " + sa.getName() + ':' + sourceURL.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(sa, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal size info '" + lstLine + "' in " + sourceURL.toString());
			}
		}
	}
}
