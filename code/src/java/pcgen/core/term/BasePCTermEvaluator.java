/**
 * pcgen.core.term.BasePCTermEvaluator.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
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
 *
 */

package pcgen.core.term;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

public abstract class BasePCTermEvaluator
{
	protected String originalText;

	public String evaluate(PlayerCharacter pc) {
		return Integer.toString(resolve(pc).intValue());
	}

	public String evaluate(PlayerCharacter pc,  final Spell aSpell) {
		return evaluate(pc);	
	}

	public String evaluate(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc) {
		return evaluate(pc);
	}
	
	public abstract Float resolve(PlayerCharacter pc);

	public Float resolve(PlayerCharacter pc, final CharacterSpell aSpell) {
		return convertToFloat(originalText, evaluate(pc, aSpell == null ? null : aSpell.getSpell()));
	}

	public Float resolve(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc) {
		return convertToFloat(originalText, evaluate(eq, primary, pc));
	}

	protected Float convertToFloat(String element, String foo)
	{
		Float d = null;
		try
		{
			d = new Float(foo);
		}
		catch (NumberFormatException nfe)
		{
			// What we got back was not a number
		}

		Float retVal = null;
		if (d != null && !d.isNaN())
		{
			retVal = d;
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(new StringBuilder("Export variable for: '")
					.append(element).append("' = ").append(d).toString());
			}
		}

		return retVal;
	}

	public Float localToFloat(PlayerCharacter pc, String localVar,
		CDOMObject owner)
	{
		Object o = pc.getLocal(owner, localVar);
		if (o instanceof Float)
		{
			return (Float) o;
		}
		return ((Number) o).floatValue();
	}

}
