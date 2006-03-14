/*
 * PointBuyLoader.java
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
 *
 * Created on October 08, 2003, 12:00 PM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.PointBuyCost;
import pcgen.core.PointBuyMethod;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * This class is a LstFileLoader used to load point-buy methods.
 *
 * <p>
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 * @author ad9c15
 */
public class PointBuyLoader extends LstLineFileLoader
{
	/**
	 * Constructor for PointBuyLoader.
	 */
	public PointBuyLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		GameMode gameMode = SystemCollections.getGameModeNamed(getGameMode());
		final int idxColon = lstLine.indexOf(':');
		if (idxColon < 0)
		{
			return;
		}

		final String key = lstLine.substring(0, idxColon);
		final String value = lstLine.substring(idxColon + 1);
		Map tokenMap = TokenStore.inst().getTokenMap(PointBuyLstToken.class);
		PointBuyLstToken token = (PointBuyLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, gameMode.getName(), sourceURL.toString(), value);
			if (!token.parse(gameMode, value))
			{
				Logging.errorPrint("Error parsing point buy method " + gameMode.getName() + '/' + sourceURL.toString() + ':' + " \"" + lstLine + "\"");
			}
		}
		else
		{
			Logging.errorPrint("Illegal point buy method info " + gameMode.getName() + '/' + sourceURL.toString() + ':' +  " \"" + lstLine + "\"");
		}
	}

	public static boolean parseStatLine(GameMode gameMode, String lstLine)
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		int statValue;
		try
		{
			statValue = Integer.parseInt(colToken.nextToken());
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("NumberFormatException in Point Buy Line:" + Constants.s_LINE_SEP, exc);
			return true;
		}

		PointBuyCost pbc = new PointBuyCost(statValue);

		Map tokenMap = TokenStore.inst().getTokenMap(PointBuyStatLstToken.class);
		while(colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			PointBuyStatLstToken token = (PointBuyStatLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, gameMode.getName(), "pointbuymethod.lst", value);
				if (!token.parse(pbc, value))
				{
					Logging.errorPrint("Error parsing point buy method " + gameMode.getName() + ':' + colString + "\"");
				}
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE") || colString.startsWith("RESTRICT:"))
			{
				if (colString.toUpperCase().equals("PRE:.CLEAR"))
				{
					pbc.clearPreReq();
				}
				else
				{
					try
					{
						PreParserFactory factory = PreParserFactory.getInstance();
						pbc.addPreReq(factory.parse(colString));
					}
					catch (PersistenceLayerException ple)
					{
						Logging.errorPrint("PersistenceLayerException in Point Buy Line:" + Constants.s_LINE_SEP, ple);
						return false;
					}
				}
			}
			else
			{
				return false;
			}
		}

		gameMode.addPointBuyStatCost(pbc);

		return true;
	}

	public static boolean parseMethodLine(GameMode gameMode, String lstLine)
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		PointBuyMethod pbm = new PointBuyMethod(colToken.nextToken(), "0");

		Map tokenMap = TokenStore.inst().getTokenMap(PointBuyMethodLstToken.class);
		while(colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			PointBuyMethodLstToken token = (PointBuyMethodLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, gameMode.getName(), "pointbuymethod.lst", value);
				if (!token.parse(pbm, value))
				{
					Logging.errorPrint("Error parsing point buy method " + gameMode.getName() + ':' + colString + "\"");
				}
			}
			else
			{
				return false;
			}
		}
		gameMode.addPurchaseModeMethod(pbm);
		return true;
	}
}
