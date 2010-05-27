/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.rules.persistence.token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

public abstract class AbstractRestrictedSpellPrimitive implements
		PrimitiveToken<Spell>
{
	private static final Class<Spell> SPELL_CLASS = Spell.class;
	private Restriction restriction;

	public boolean initialize(String args)
	{
		if (args != null)
		{
			restriction = getRestriction(args);
			return restriction != null;
		}
		return true;
	}

	private Restriction getRestriction(String restrString)
	{
		StringTokenizer restr = new StringTokenizer(restrString, ";");
		Formula levelMax = null;
		Formula levelMin = null;
		Boolean known = null;
		while (restr.hasMoreTokens())
		{
			String tok = restr.nextToken();
			if (tok.startsWith("LEVELMAX="))
			{
				levelMax = FormulaFactory.getFormulaFor(tok.substring(9));
			}
			else if (tok.startsWith("LEVELMIN="))
			{
				levelMin = FormulaFactory.getFormulaFor(tok.substring(9));
			}
			else if ("KNOWN=YES".equals(tok))
			{
				known = Boolean.TRUE;
			}
			else if ("KNOWN=NO".equals(tok))
			{
				known = Boolean.FALSE;
			}
			else
			{
				Logging.errorPrint("Unknown restriction: " + tok
						+ " in CHOOSE:SPELLS");
				return null;
			}
		}
		return new Restriction(levelMin, levelMax, known);
	}

	private static class Restriction
	{
		public final Formula minLevel;
		public final Formula maxLevel;
		public final Boolean knownRequired;

		public Restriction(Formula levelMin, Formula levelMax, Boolean known)
		{
			minLevel = levelMin;
			maxLevel = levelMax;
			knownRequired = known;
		}
	}

	public Class<Spell> getReferenceClass()
	{
		return SPELL_CLASS;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getPrimitiveLST());
		if (restriction != null)
		{
			sb.append('[').append(restriction.toString()).append(']');
		}
		return sb.toString();
	}

	public abstract CharSequence getPrimitiveLST();

	public boolean allow(PlayerCharacter pc, int level, String source,
			Spell spell)
	{
		String defaultbook = Globals.getDefaultSpellBook();
		if (restriction != null)
		{
			Formula maxLevel = restriction.maxLevel;
			if (maxLevel != null
					&& (level > maxLevel.resolve(pc, source).intValue()))
			{
				return false;
			}
			Formula minLevel = restriction.minLevel;
			if (minLevel != null
					&& (level < minLevel.resolve(pc, source).intValue()))
			{
				return false;
			}
			if (restriction.knownRequired != null)
			{
				boolean known = restriction.knownRequired.booleanValue();
				boolean found = false;
				for (PCClass cl : pc.getClassSet())
				{
					List<CharacterSpell> csl = pc.getCharacterSpells(cl, spell,
							defaultbook, -1);
					if (csl != null && !csl.isEmpty())
					{
						/*
						 * Going to assume here that the level doesn't need to
						 * be rechecked... ?? - thpr Feb 26, 08
						 */
						found = true;
					}
				}
				if (found != known)
				{
					return false;
				}
			}
		}
		return true;
	}

	public Set<Spell> getSet(PlayerCharacter pc)
	{
		HashSet<Spell> spellSet = new HashSet<Spell>();
		for (Spell spell : Globals.getContext().ref
				.getConstructedCDOMObjects(SPELL_CLASS))
		{
			if (allow(pc, spell))
			{
				spellSet.add(spell);
			}
		}
		return spellSet;
	}

	public boolean equalsRestrictedPrimitive(
			AbstractRestrictedSpellPrimitive other)
	{
		if (other == this)
		{
			return true;
		}
		if (restriction == null)
		{
			return other.restriction == null;
		}
		return restriction.equals(other.restriction);
	}

	public boolean hasRestriction()
	{
		return restriction != null;
	}
}
