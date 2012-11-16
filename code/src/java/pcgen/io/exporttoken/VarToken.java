/*
 * VarToken.java
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
 */
package pcgen.io.exporttoken;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * <code>VarToken</code> produces the output for the output token VAR.
 * Possible tag formats are:<pre>
 * VAR.x
 * VAR.x.INTVAL|.MINVAL|.NOSIGN
 * </pre>
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon Jones <soulcatcher@evilsoft.org>
 * @version $Revision$
 */
public class VarToken extends Token
{
	/** The name of the token handled by this class. */
	public static final String TOKENNAME = "VAR";

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
		boolean isMin = tokenSource.lastIndexOf(".MINVAL") >= 0;
		boolean isInt = tokenSource.lastIndexOf(".INTVAL") >= 0;
		boolean isSign = (tokenSource.lastIndexOf(".SIGN") >= 0);
		if (tokenSource.lastIndexOf(".NOSIGN") >= 0)
		{
			isSign = false;
			Logging
				.errorPrint(".NOSIGN in output token " + tokenSource
					+ " is deprecated. "
					+ "The default output format is unsigned.");
		}

		String workingSource = tokenSource;
		// clear out the gunk
		if (isMin)
		{
			workingSource = workingSource.replaceAll(".MINVAL", "");
		}
		if (isInt)
		{
			workingSource = workingSource.replaceAll(".INTVAL", "");
		}
		workingSource = workingSource.replaceAll(".NOSIGN", "");
		workingSource = workingSource.replaceAll(".SIGN", "");

		StringTokenizer aTok = new StringTokenizer(workingSource, ".");
		aTok.nextToken(); //this should be VAR

		StringBuilder varName = new StringBuilder();

		if (aTok.hasMoreElements())
		{
			varName.append(aTok.nextToken());
		}
		while (aTok.hasMoreElements())
		{
			varName.append(".").append(aTok.nextToken());
		}

		if (isInt)
		{
			if (isSign)
			{
				return Delta.toString(getIntVarToken(pc, varName.toString(),
					isMin));
			}
			return getIntVarToken(pc, varName.toString(), isMin) + "";
		}
		if (isSign)
		{
			return Delta.toString(getVarToken(pc, varName.toString(), isMin));
		}
		return getVarToken(pc, varName.toString(), isMin) + "";
	}

	/**
	 * Get the value of the variable as a float. Where the PC has multiple 
	 * values for the variable (different classes etc) the fucntion can 
	 * return either the minimum or maximum of these values.
	 *  
	 * @param pc The character being output.
	 * @param varName The name of the variable.
	 * @param isMin Do we want the minimum if there are multiple values.
	 * @return The variable's value.
	 */
	public static float getVarToken(PlayerCharacter pc, String varName,
		boolean isMin)
	{
		return pc.getVariable(varName, !isMin);
	}

	/**
	 * Get the value of the variable as an integer. Where the PC has multiple 
	 * values for the variable (different classes etc) the fucntion can 
	 * return either the minimum or maximum of these values.
	 *  
	 * @param pc The character being output.
	 * @param varName The name of the variable.
	 * @param isMin Do we want the minimum if there are multiple values.
	 * @return The variable's value.
	 */
	public static int getIntVarToken(PlayerCharacter pc, String varName,
		boolean isMin)
	{
		return pc.getVariable(varName, !isMin).intValue();
	}
}
