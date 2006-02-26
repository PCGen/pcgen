/*
 * BaseMovementToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision: 1.5 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:58 $
 *
 */
package plugin.exporttokens;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;

/**
 * BASEMOVEMENT related stuff
 * possible tokens are
 * BASEMOVEMENT.type.load.flag
 * where
 * type    := "WALK" and other Movement Types|a numeric value
 * so 0 is the first movement type etc.
 * load     := "LIGHT"|"MEDIUM"|"HEAVY"|"OVERLOAD"
 * flag     := "TRUE"|"FALSE"
 * TRUE = Add Movement Measurement type to String.
 * FALSE = Dont Add Movement Measurement type to String
 * del     := "."
 * <p/>
 * i.e. BASEMOVEMENT.0.LIGHT.TRUE
 * Would output 30' for a normal human
 * and    BASEMOVEMENT.0.LIGHT.FALSE
 * Would output 30 for the same human.
 * <p/>
 */
public class BaseMovementToken extends Token
{
	/** Name of Token */
	public static final String TOKENNAME = "BASEMOVEMENT";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";
		if ((pc.getRace() != null) && !pc.getRace().equals(Globals.s_EMPTYRACE))
		{
			StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
			aTok.nextToken(); //clear BASEMOVEMENT Token
			String moveType = "WALK";
			int load = Constants.LIGHT_LOAD;
			boolean flag = true;

			//Move Type
			if(aTok.hasMoreElements()) {
				moveType = aTok.nextToken();
				try
				{
					int movNum = Integer.parseInt(moveType);
					if (movNum < pc.getNumberOfMovements())
					{
						moveType = pc.getMovementType(movNum);
					}
				}
				catch (NumberFormatException e)
				{
				    // Delibrately ignore exception, means movetype is not am index
				}
			}

			//Encumberance Level
			if(aTok.hasMoreElements()) {
				String loadName = aTok.nextToken();
				if (Constants.s_LOAD_MEDIUM.equals(loadName))
				{
					load = Constants.MEDIUM_LOAD;
				}
				else if (Constants.s_LOAD_HEAVY.equals(loadName))
				{
					load = Constants.HEAVY_LOAD;
				}
				else if (Constants.s_LOAD_OVERLOAD.equals(loadName))
				{
					load = Constants.OVER_LOAD;
				}
			}

			//Display Movement Measurement type?
			if(aTok.hasMoreElements()) {
				flag =  "TRUE".equals((aTok.nextToken()).toUpperCase());
			}
			retString = getBaseMovementToken(pc, moveType, load, flag);
		}
		return retString;
	}

	/**
	 * Get the base movement token
	 * @param pc
	 * @param moveType
	 * @param loadType
	 * @param displayFlag
	 * @return The base movement token
	 */
	public static String getBaseMovementToken(PlayerCharacter pc, String moveType, int loadType, boolean displayFlag)
	{
		for (int i = 0; i < pc.getNumberOfMovements(); i++)
		{
			if (pc.getMovementType(i).toUpperCase().equals(moveType.toUpperCase()))
			{
				if (displayFlag)
				{
					return moveType +
					 " " +
					 Globals.getGameModeUnitSet().displayDistanceInUnitSet(pc.basemovement(i, loadType)) + Globals.getGameModeUnitSet().getDistanceUnit();
				}
				return Globals.getGameModeUnitSet().displayDistanceInUnitSet(pc.basemovement(i, loadType));
			}
		}
		return "";
	}
}

