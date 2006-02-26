/*
 * SkillRankTextToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 6, 2005, 8:58 PM
 *
 * Current Ver: $Revision: 1.2 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006/02/16 01:03:16 $
 *
 */
package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

import java.util.StringTokenizer;


/**
 * <code>SkillRankTextToken</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class SkillRankTextToken implements GameModeLstToken {

	public String getTokenName() {
		return "SKILLRANKTEXT";
	}

	//
	// SKILLRANKTEXT:<rank_val> <tab> <display_text>
	//
	public boolean parse(GameMode gameMode, String value) {
		final StringTokenizer tok = new StringTokenizer(value, "\t");
		if (tok.countTokens() == 2) {
			try {
				final int rankValue = Integer.parseInt(tok.nextToken());
				String rankText = tok.nextToken();
				if ("&nbsp;".equals(rankText)) {
					rankText = "";
				}
				gameMode.addSkillRankDisplayText(rankValue, rankText);
				return true;
			}
			catch (NumberFormatException exc) {
				//returns false
			}
		}
		return false;
	}
}
