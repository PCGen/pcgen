/**
 * pcgen.core.term.BasePCTermEvaluator.java
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
 * Created 07-Aug-2008 20:49:05
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.Equipment;
import pcgen.core.spell.Spell;

public abstract class BasePCTermEvaluator
{
	protected String originalText;

	public String evaluate(PlayerCharacter pc) {
		return Integer.toString(resolve(pc).intValue());
	}

	public String evaluate(PlayerCharacter pc,  final Spell aSpell) {
		return Integer.toString(resolve(pc, aSpell).intValue());	
	}

	public abstract Float resolve(PlayerCharacter pc);

	public Float resolve(PlayerCharacter pc, final Spell aSpell) {
		return resolve(pc);
	}

	public Float resolve(
			Equipment equipment,
			boolean primary,
			PlayerCharacter pc) {
		return resolve(pc);
	}

	public Float getDefault ()
	{
		return 1.0f;
	}

	public String getOriginal ()
	{
		return originalText;
	}

	public int getLength ()
	{
		return originalText.length();
	}
}
