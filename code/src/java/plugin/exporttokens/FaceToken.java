/*
 * FaceToken.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Deal with Tokens:
 * 
 * FACE
 * FACE.SHORT
 * FACE.1
 * FACE.2
 */
public class FaceToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "FACE";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";

		if ("FACE".equals(tokenSource))
		{
			retString = getFaceToken(pc);
		}
		else if ("FACE.SHORT".equals(tokenSource))
		{
			retString = getShortToken(pc);
		}
		else if ("FACE.SQUARES".equals(tokenSource))
		{
			retString = getSquaresToken(pc);
		}
		else if ("FACE.1".equals(tokenSource))
		{
			retString = get1Token(pc);
		}
		else if ("FACE.2".equals(tokenSource))
		{
			retString = get2Token(pc);
		}

		return retString;
	}

	/**
	 * Get FACE Token
	 * @param pc
	 * @return FACE Token
	 */
	public static String getFaceToken(PlayerCharacter pc)
	{
		Point2D.Double face = pc.getFace();
		String retString = "";
		if (CoreUtility.doublesEqual(face.getY(), 0.0))
		{
			retString =
					Globals.getGameModeUnitSet().displayDistanceInUnitSet(
						face.getX())
						+ Globals.getGameModeUnitSet().getDistanceUnit();
		}
		else
		{
			retString =
					Globals.getGameModeUnitSet().displayDistanceInUnitSet(
						face.getX())
						+ Globals.getGameModeUnitSet().getDistanceUnit()
						+ " by "
						+ Globals.getGameModeUnitSet()
							.displayDistanceInUnitSet(face.getY())
						+ Globals.getGameModeUnitSet().getDistanceUnit();
		}
		return retString;
	}

	/**
	 * Get SHORT sub token
	 * @param pc
	 * @return SHORT sub toke
	 */
	public static String getShortToken(PlayerCharacter pc)
	{
		Point2D.Double face = pc.getFace();
		String retString = "";
		if (CoreUtility.doublesEqual(face.getY(), 0.0))
		{
			retString =
					Globals.getGameModeUnitSet().displayDistanceInUnitSet(
						face.getX())
						+ Globals.getGameModeUnitSet().getDistanceUnit();
		}
		else
		{
			retString =
					Globals.getGameModeUnitSet().displayDistanceInUnitSet(
						face.getX())
						+ Globals.getGameModeUnitSet().getDistanceUnit()
						+ " x "
						+ Globals.getGameModeUnitSet()
							.displayDistanceInUnitSet(face.getY())
						+ Globals.getGameModeUnitSet().getDistanceUnit();
		}
		return retString;
	}

	/**
	 * Get squares sub token
	 * @param pc
	 * @return squares sub token
	 */
	public static String getSquaresToken(PlayerCharacter pc)
	{
		Point2D.Double face = pc.getFace();
		String retString = "";
		double squareSize = SettingsHandler.getGame().getSquareSize();
		if (CoreUtility.doublesEqual(face.getY(), 0.0))
		{
			retString =
					new DecimalFormat("#.#").format(face.getX()
						/ squareSize);
		}
		else
		{
			retString =
					new DecimalFormat("#.#").format(face.getX()
						/ squareSize)
						+ " x "
						+ new DecimalFormat("#.#").format(face.getY()
							/ squareSize);
		}
		return retString;
	}

	/**
	 * Get 1 sub token
	 * @param pc
	 * @return 1 sub token
	 */
	public static String get1Token(PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(
			pc.getFace().getX());
	}

	/**
	 * Get 2 sub token
	 * @param pc
	 * @return 2 sub token
	 */
	public static String get2Token(PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(
			pc.getFace().getY());
	}
}
