/**
 * pcgen.core.term.PCClassCasterLevelTermEvaluator.java
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
 * Created 04-Aug-2008 02:27:58
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.PCClass;
import pcgen.core.CharacterDomain;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.cdom.base.Constants;

public class PCCasterLevelClassTermEvaluator
		extends BasePCTermEvaluator implements TermEvaluator {

	private final String source;

	public PCCasterLevelClassTermEvaluator(
			String originalText, String source)
	{
		this.originalText = originalText;
		this.source = source;
	}

	// Makes no sense without a spell
	public Float resolve(PlayerCharacter pc) {
		return 0f;
	}

	public Float resolve(PlayerCharacter pc, final Spell aSpell) {

		// check if this is a domain spell
		final CharacterDomain aCD = pc.getCharacterDomainForDomain(source);
		
		// If source is a domain, get the Domain source (e.g, "Cleric"),
		// otherwise just go with the original varSource
		final String varSource = (aCD != null) ? aCD.getObjectName() : source;

		final PCClass spClass = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(PCClass.class, varSource);

		String spellType = Constants.s_NONE;
		if ((spClass != null) && (!spClass.getSpellType().equals(Constants.s_NONE)))
		{
			spellType = spClass.getSpellType();
		}

		final int pcBonus = (int) pc.getTotalBonusTo("PCLEVEL", varSource);

		// does the class have a Casterlevel?
		final int castBonus = (int) pc.getTotalBonusTo("CASTERLEVEL", varSource);

		// If no CASTERLEVEL has been defined for this class then
		// use total class level instead
		final int iClass = (castBonus == 0) ? spClass.getLevel() : 0;

		return (float) pc.getTotalCasterLevelWithSpellBonus(
				aSpell, spellType, varSource, iClass + pcBonus);
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
