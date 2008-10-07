/**
 * pcgen.core.term.PCStatModTermEvaluator.java
 * Copyright © 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 11-Sep-2008 02:30:27
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.StatList;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;

public class PCStatModTermEvaluator
		extends BasePCTermEvaluator implements TermEvaluator
{
	private final String statAbbrev;
	
	public PCStatModTermEvaluator(String originalText, String statAbbrev)
	{
		this.originalText = originalText;
		this.statAbbrev   = statAbbrev;
	}

	public Float resolve(PlayerCharacter pc)
	{
		final StatList sl   = pc.getStatList();
		final GameMode gm   = SettingsHandler.getGame();
		final int statIndex = gm.getStatFromAbbrev(statAbbrev);
		final int statNum   = sl.getTotalStatFor(statAbbrev);

		return (float) sl.getModForNumber(statNum, statIndex);
	}

	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
