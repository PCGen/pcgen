/*
 * ClassToken.java
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

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Deal with tokens below
 * CLASS.x
 * CLASS.x.LEVEL
 * CLASS.x.SALIST
 */
public class ClassToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "CLASS";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		int i = 0;

		if (aTok.hasMoreTokens())
		{
			i = Integer.parseInt(aTok.nextToken());
		}

		if (aTok.hasMoreTokens())
		{
			String subToken = aTok.nextToken();

			if ("LEVEL".equals(subToken))
			{
				int level = getLevelToken(pc, i);

				if (level > 0)
				{
					return Integer.toString(level);
				}
				return "";
			}
			else if ("SALIST".equals(subToken))
			{
				return getSAListToken(pc, i);
			}
			else if ("TYPE".equals(subToken))
			{
				return getClassType(pc, i);
			}
		}

		return getClassToken(pc, i);
	}

	/**
	 * Get the token
	 * @param pc
	 * @param classNumber
	 * @return token
	 */
	public static String getClassToken(PlayerCharacter pc, int classNumber)
	{
		String retString = "";

		if (pc.getClassList().size() > classNumber)
		{
			PCClass pcClass = pc.getClassList().get(classNumber);

			if (Constants.s_NONE.equals(pcClass.getSubClassKey()) || "".equals(pcClass.getSubClassKey()))
			{
				//FileAccess.encodeWrite(output, aClass.getName());
				retString = pcClass.getOutputName();
			}
			else
			{
				retString = pcClass.getSubClassKeyed(pcClass.getSubClassKey()).getDisplayName();
			}
		}

		return retString;
	}

	/**
	 * Get Level part of the class token
	 * @param pc
	 * @param classNumber
	 * @return level token
	 */
	public static int getLevelToken(PlayerCharacter pc, int classNumber)
	{
		if (pc.getClassList().size() > classNumber)
		{
			PCClass pcClass = pc.getClassList().get(classNumber);

			return pcClass.getLevel();
		}

		return 0;
	}

	/**
	 * Get Level part of the class token
	 * @param pc
	 * @param classNumber
	 * @return level token
	 */
	public static String getSAListToken(PlayerCharacter pc, int classNumber)
	{
		if (pc.getClassList().size() > classNumber)
		{
			PCClass pcClass = pc.getClassList().get(classNumber);
			List<String> saList = getClassSpecialAbilityList(pcClass, pc);
			return CoreUtility.join(saList, ", ");
		}

		return "";
	}


	public static List<String> getClassSpecialAbilityList(PCClass pcclass,
			final PlayerCharacter aPC) {
		final List<String> aList = new ArrayList<String>();
		final List<String> formattedList = new ArrayList<String>();
		final List<SpecialAbility> abilityList = pcclass.getListFor(ListKey.SPECIAL_ABILITY);

		//
		// Determine the list of abilities from this class
		// that the character is eligable for
		//
		if (abilityList == null || abilityList.isEmpty()) {
			return aList;
		}

		for (SpecialAbility saAbility : abilityList) {
			final String aString = saAbility.toString();

			if (aList.contains(aString)) {
				break;
			}

			if (saAbility.pcQualifiesFor(aPC)) {
				aList.add(aString);
			}
		}

		// From the list of allowed SAs, format the output strings
		// to include all of the variables
		for (String str : aList) {
			StringTokenizer varTok = new StringTokenizer(str, Constants.PIPE);
			final String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();

			if (varCount != 0) {
				varValue = new int[varCount];

				for (int j = 0; j < varCount; ++j) {
					// Get the value for each variable
					final String vString = varTok.nextToken();
					varValue[j] = aPC.getVariable(vString, true, true, "", "",
							0).intValue();
				}
			}

			final StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;

			boolean isZero = false;

			// Fill in each % with the value of the appropriate token
			while (varTok.hasMoreTokens()) {
				final String nextTok = varTok.nextToken();

				if ("%".equals(nextTok)) {
					if (varCount == 0) {
						// If this is the first token, then set the count of
						// successfull token replacements to 0
						isZero = true;
					}

					if ((varValue != null) && (varCount < varValue.length)) {
						final int thisVar = varValue[varCount++];

						// Update isZero if this token has a value of anything
						// other than 0
						isZero &= (thisVar == 0);
						newAbility.append(thisVar);
					} else {
						newAbility.append('%');
					}
				} else {
					newAbility.append(nextTok);
				}
			}

			if (!isZero) {
				// If all of the tokens for this ability were 0 then we do not
				// show it,
				// otherwise we add it to the return list.
				formattedList.add(newAbility.toString());
			}
		}

		return formattedList;
	}
	
	/**
	 * @param pc
	 * @param classNumber
	 * @return class Type
	 */
	public static String getClassType(PlayerCharacter pc, int classNumber)
	{
		if (pc.getClassList().size() > classNumber)
		{
			return pc.getClassList().get(classNumber).getType();
		}
		return "";
	}
}
