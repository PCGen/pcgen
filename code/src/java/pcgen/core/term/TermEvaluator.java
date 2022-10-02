package pcgen.core.term;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

/**
 * Copyright (c) 2008 Andrew Wilson &lt;nuance@users.sourceforge.net&gt;.
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
public interface TermEvaluator
{
	String evaluate(PlayerCharacter pc);

	String evaluate(PlayerCharacter pc, Spell aSpell);

	String evaluate(Equipment eq, boolean primary, PlayerCharacter pc);

	Float resolve(PlayerCharacter pc);

	Float resolve(PlayerCharacter pc, CharacterSpell aSpell);

	Float resolve(Equipment eq, boolean primary, PlayerCharacter pc);

	boolean isSourceDependant();

	//boolean isStatic();
}
