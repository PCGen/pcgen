/*
 * UnitSetLoader.java
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
 * Created on November 20, 2003
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * <code>UnitSetLoader</code>.
 *
 * @author Stefan Radermacher <stefan@zaister.de>
 * @version $Revision$
 */
public final class UnitSetLoader extends LstLineFileLoader
{
	/** Creates a new instance of UnitSetLoader */
	public UnitSetLoader()
	{
		// Empty Constructor
	}

	/**
	 * @param lstLine 
	 * @param sourceURL 
	 * @deprecated This is the old style, to be removed in 5.11.1  
	 */
	public void parseLine(String lstLine, URI sourceURI)
	{
		Logging
			.errorPrint("Warning: unitset.lst deprecated. use UNITSET in miscinfo.lst instead (GameMode: "
				+ getGameMode() + ")");
		UnitSet unitSet = null;

		final StringTokenizer aTok = new StringTokenizer(lstLine, "\t");
		int iCount = 0;

		while (aTok.hasMoreElements())
		{
			final String colString = (String) aTok.nextElement();

			try
			{
				switch (iCount)
				{
					case 0:
						unitSet =
								SystemCollections.getUnitSet(colString,
									getGameMode());
						unitSet.setName(colString);

						break;

					case 1:
						unitSet.setHeightUnit(colString);

						break;

					case 2:
						unitSet.setHeightFactor(Double.parseDouble(colString));

						break;

					case 3:
						unitSet.setHeightDisplayPattern(colString);

						break;

					case 4:
						unitSet.setDistanceUnit(colString);

						break;

					case 5:
						unitSet
							.setDistanceFactor(Double.parseDouble(colString));

						break;

					case 6:
						unitSet.setDistanceDisplayPattern(colString);

						break;

					case 7:
						unitSet.setWeightUnit(colString);

						break;

					case 8:
						unitSet.setWeightFactor(Double.parseDouble(colString));

						break;

					case 9:
						unitSet.setWeightDisplayPattern(colString);

						break;

					default:
						Logging.errorPrint("Unexpected token '" + colString
							+ "' in " + sourceURI.toString());

						break;
				}
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Illegal unit set info '" + lstLine
					+ "' in " + sourceURI.toString());
			}

			iCount += 1;
		}
	}

	/**
	 * Parse the UNITSET tag
	 * @param gameModeIn
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameModeIn, String lstLine, URI source)
		throws PersistenceLayerException
	{
		StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		UnitSet unitSet = null;
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(UnitSetLstToken.class);
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
				// TODO Deal with Exception
			}
			UnitSetLstToken token = (UnitSetLstToken) tokenMap.get(key);

			if (key.equals("UNITSET"))
			{
				final String value = colString.substring(idxColon + 1).trim();
				unitSet =
						SystemCollections.getUnitSet(value, gameModeIn
							.getName());
				unitSet.setName(value);
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Unit Set", source, value);
				if (!token.parse(unitSet, value))
				{
					Logging.errorPrint("Error parsing unit set:"
						+ "miscinfo.lst from the " + gameModeIn.getName()
						+ " Game Mode" + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Invalid sub tag " + token
					+ " on UNITSET line");
				throw new PersistenceLayerException("Invalid sub tag " + token
					+ " on UNITSET line");
			}
		}
	}
}
