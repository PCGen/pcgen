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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deal with tokens below CLASS.x CLASS.x.LEVEL, CLASS.x.TYPE, CLASS.x.SALIST
 */
public class ClassToken extends Token {
	/** Token name */
	public static final String TOKENNAME = "CLASS";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName() {
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String,
	 *      pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
			ExportHandler eh) {
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		int i = 0;

		if (aTok.hasMoreTokens()) {
			i = Integer.parseInt(aTok.nextToken());
		}

		if (aTok.hasMoreTokens()) {
			String subToken = aTok.nextToken();

			if ("LEVEL".equals(subToken)) {
				int level = getLevelToken(pc, i);

				if (level > 0) {
					return Integer.toString(level);
				}
				return "";
			} else if ("SALIST".equals(subToken)) {
				return getSAListToken(pc, i);
			} else if ("TYPE".equals(subToken)) {
				return getClassType(pc, i);
			}
		}

		return getClassToken(pc, i);
	}

	/**
	 * Get the token
	 * 
	 * @param pc
	 * @param classNumber
	 * @return token
	 */
	public static String getClassToken(PlayerCharacter pc, int classNumber) {
		String retString = "";

		if (pc.getClassCount() > classNumber) {
			PCClass pcClass = pc.getClassList().get(classNumber);

			String subClassKey = pc.getSubClassName(pcClass);
			if (subClassKey == null || Constants.NONE.equals(subClassKey)
					|| "".equals(subClassKey)) {
				retString = OutputNameFormatting.getOutputName(pcClass);
			} else {
				retString = pcClass.getSubClassKeyed(subClassKey)
						.getDisplayName();
			}
		}

		return retString;
	}

	/**
	 * Get Level part of the class token
	 * 
	 * @param pc
	 * @param classNumber
	 * @return level token
	 */
	public static int getLevelToken(PlayerCharacter pc, int classNumber) {
		if (pc.getClassCount() > classNumber) {
			PCClass pcClass = pc.getClassList().get(classNumber);

			return pc.getLevel(pcClass);
		}

		return 0;
	}

	/**
	 * Get Level part of the class token
	 * 
	 * @param pc
	 * @param classNumber
	 * @return level token
	 */
	public static String getSAListToken(PlayerCharacter pc, int classNumber) {
		if (pc.getClassCount() > classNumber) {
			PCClass pcClass = pc.getClassList().get(classNumber);
			List<String> saList = getClassSpecialAbilityList(pcClass, pc);
			return StringUtil.join(saList, ", ");
		}

		return "";
	}

	/**
	 * Get the list of Special Abilities for a class that the PC is eligible
	 * for.
	 * 
	 * @param pcclass
	 *            The class to get the special abilities for
	 * @param aPC
	 *            The PC
	 * @return List of special abilities
	 */
	public static List<String> getClassSpecialAbilityList(PCClass pcclass,
			final PlayerCharacter aPC) {
		final List<String> formattedList = new ArrayList<String>();
		
		final List<SpecialAbility> saList = new ArrayList<SpecialAbility>();
		saList.addAll(aPC.getResolvedUserSpecialAbilities(pcclass));
		saList.addAll(aPC.getResolvedSpecialAbilities(pcclass));
		for (int i = 1; i <= aPC.getLevel(pcclass); i++)
		{
			PCClassLevel pcl = aPC.getActiveClassLevel(pcclass, i);
			saList.addAll(aPC.getResolvedUserSpecialAbilities(pcl));
			saList.addAll(aPC.getResolvedSpecialAbilities(pcl));
		}

		if (saList.isEmpty())
		{
			return formattedList;
		}
		Collections.sort(saList);
		
		// From the list of allowed SAs, format the output strings
		// to include all of the variables
		for (SpecialAbility sa : saList) {
			String str = sa.getDisplayName();
			if (str == null || str.length() == 0)
			{
				continue;
			}
			StringTokenizer varTok = new StringTokenizer(str, Constants.PIPE);
			final String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();

			if (varCount != 0) {
				varValue = new int[varCount];

				for (int j = 0; j < varCount; ++j) {
					// Get the value for each variable
					final String vString = varTok.nextToken();
					varValue[j] = aPC.getVariable(vString, true).intValue();
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
						// successful token replacements to 0
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
	public static String getClassType(PlayerCharacter pc, int classNumber) {
		if (pc.getClassCount() > classNumber) {
			return pc.getClassList().get(classNumber).getType();
		}
		return "";
	}
}
