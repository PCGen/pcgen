/**
 * pcgen.base.term.PCMaxCastableClassTermEvaluator.java
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
 * Created 09-Aug-2008 23:14:10
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.base.term;

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.PCClass;
import pcgen.cdom.base.Constants;

public class PCMaxCastableClassTermEvaluator 
		extends BasePCTermEvaluator implements TermEvaluator
{
	private final String classKey;

	public PCMaxCastableClassTermEvaluator(String originalText, String classKey)
	{
		this.originalText = originalText;
		this.classKey = classKey;
	}

	public Float resolve(PlayerCharacter pc)
	{
		Float max = -1f;
		for (PCClass spClass : pc.getClassList())
		{
			StringTokenizer st =
					new StringTokenizer(spClass.getSpellKey(pc),
										Constants.PIPE);
			while (st.hasMoreTokens())
			{
				String type = st.nextToken();
				//Doesn't need to be guarded - if this throws an exception the
				// problem is in getSpellKey()
				String key = st.nextToken();
				if ("CLASS".equals(type) && key.equalsIgnoreCase(classKey))
				{
					int cutoff = spClass.getHighestLevelSpell();
					if (spClass.hasCastList())
					{
						for (int i = 0; i < cutoff; i++) {
							if (spClass.getCastForLevel(i, pc) != 0)
							{
								max = Math.max(max,i);
							}
						}
					}
					else
					{
						for (int i = 0; i < cutoff; i++) {
							if (spClass.getKnownForLevel(i, pc) != 0)
							{
								max = Math.max(max,i);
							}
						}
					}
				}
			}
		}
		return max;
	}

	public boolean isSourceDependant()
	{
		return true;
	}

	public boolean isStatic()
	{
		return false;
	}
}
