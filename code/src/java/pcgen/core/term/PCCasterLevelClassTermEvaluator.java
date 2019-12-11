/**
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
 * Created 04-Aug-2008 02:27:58
 */

package pcgen.core.term;

import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;

public class PCCasterLevelClassTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

	private final String source;

	public PCCasterLevelClassTermEvaluator(String originalText, String source)
	{
		this.originalText = originalText;
		this.source = source;
	}

	// Makes no sense without a spell
	@Override
	public Float resolve(PlayerCharacter pc)
	{
		return 0.0f;
	}

	@Override
	public Float resolve(PlayerCharacter pc, final CharacterSpell aSpell)
	{

		// check if this is a domain spell
		Domain domain =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Domain.class, source);
		final ClassSource cs = pc.getDomainSource(domain);

		// If source is a domain, get the Domain source (e.g, "Cleric"),
		// otherwise just go with the original varSource
		final String varSource = (cs != null) ? cs.getPcclass().getKeyName() : source;

		final PCClass spClass =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, varSource);

		String spellType = Constants.NONE;
		if ((spClass != null) && (!spClass.getSpellType().equals(Constants.NONE)))
		{
			spellType = spClass.getSpellType();
		}

		final double d1 = pc.getTotalBonusTo("PCLEVEL", varSource);
		final int pcBonus = (int) d1;

		// does the class have a Casterlevel?
		final double d2 = pc.getTotalBonusTo("CASTERLEVEL", varSource);
		final int castBonus = (int) d2;

		// If no CASTERLEVEL has been defined for this class then
		// use total class level instead
		final int iClass = (spClass != null && castBonus == 0) ? pc.getDisplay().getLevel(spClass) : 0;

		return (float) pc.getTotalCasterLevelWithSpellBonus(aSpell, aSpell.getSpell(), spellType, varSource,
			iClass + pcBonus);
	}

	@Override
	public boolean isSourceDependant()
	{
		return true;
	}

	public boolean isStatic()
	{
		return false;
	}
}
