/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PObject.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class OutputNameFormatting
{

	/**
	 * Parse the output name to get a useable Name token
	 * @param aString
	 * @param aPC
	 * @return the output name to get a useable Name token
	 */
	public static String parseOutputName(final String aString, final PlayerCharacter aPC)
	{
		final int varIndex = aString.indexOf('|');
	
		if (varIndex <= 0)
		{
			return (aString);
		}
	
		final StringTokenizer varTokenizer = new StringTokenizer(aString, "|");
	
		final String preVarStr = varTokenizer.nextToken();
	
		final ArrayList<Float> varArray = new ArrayList<Float>();
		final ArrayList<String> tokenList = new ArrayList<String>();
	
		while (varTokenizer.hasMoreElements())
		{
			final String token = varTokenizer.nextToken();
			tokenList.add(token.toUpperCase());
			varArray.add(aPC.getVariableValue(token, ""));
		}
	
		final StringBuffer result = new StringBuffer();
		int varCount = 0;
		int subIndex = preVarStr.indexOf('%');
		int lastIndex = 0;
	
		while (subIndex >= 0)
		{
			if (subIndex > 0)
			{
				result.append(preVarStr.substring(lastIndex, subIndex));
			}
	
			final String token = tokenList.get(varCount);
			final Float val = varArray.get(varCount);
	
			if (token.endsWith(".INTVAL"))
			{
				result.append(String.valueOf(val.intValue()));
			}
			else
			{
				result.append(val.toString());
			}
	
			lastIndex = subIndex + 1;
			varCount++;
			subIndex = preVarStr.indexOf('%', lastIndex);
		}
	
		if (preVarStr.length() > lastIndex)
		{
			result.append(preVarStr.substring(lastIndex));
		}
	
		return (result.toString());
	}

	/**
	 * Returns the Product Identity string (with or without the header)
	 * @param useHeader
	 * @return the Product Identity string (with or without the header)
	 */
	public static String piString(PObject po, final boolean useHeader)
	{
		String aString = po.toString();
	
		if (SettingsHandler.guiUsesOutputNameEquipment())
		{
			aString = po.getOutputName();
		}
	
		if (po.getSafe(ObjectKey.NAME_PI))
		{
			final StringBuffer sb = new StringBuffer(aString.length() + 30);
	
			if (useHeader)
			{
				sb.append("<html>");
			}
	
			sb.append("<b><i>").append(aString).append("</i></b>");
	
			if (useHeader)
			{
				sb.append("</html>");
			}
	
			return sb.toString();
		}
	
		return aString;
	}

}
