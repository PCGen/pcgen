/**
 * pcgen.core.term.PCMaxCastableTermEvaluator.java
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
 * Created 09-Aug-2008 22:55:19
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.CharacterDomain;
import pcgen.core.PCClass;

public class PCMaxCastableDomainTermEvaluator
		extends BasePCTermEvaluator implements TermEvaluator
{
	private final String domainKey;

	public PCMaxCastableDomainTermEvaluator(String originalText, String domainKey)
	{
		this.domainKey = domainKey;
		this.originalText = originalText;
	}

	public Float resolve(PlayerCharacter pc)
	{
		CharacterDomain domain = pc.getCharacterDomainForDomain(domainKey);

		if (domain != null)
		{
			String classKey = domain.getObjectName();
			PCClass spClass = pc.getClassKeyed(classKey);
			int cutoff      = spClass.getHighestLevelSpell();

			Float max = 0f;

			if (spClass.hasCastList())
			{
				for (int i = 0; i < cutoff; i++) {
					if (spClass.getCastForLevel(i, pc) != 0)
					{
						max = Math.max(max, i);
					}
				}
			}
			else
			{
				for (int i = 0; i < cutoff; i++) {
					if (spClass.getKnownForLevel(i, pc) != 0)
					{
						max = Math.max(max, i);
					}
				}
			}
			return max;
		}

		return 0f;
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
