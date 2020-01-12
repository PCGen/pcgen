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

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public abstract class AbstractRestrictedSpellPrimitive implements PrimitiveToken<Spell>, PrimitiveFilter<Spell>
{
	private static final Class<Spell> SPELL_CLASS = Spell.class;
	private Restriction restriction;
	private CDOMReference<Spell> allSpells;

	public boolean initialize(LoadContext context, String args)
	{
		allSpells = context.getReferenceContext().getCDOMAllReference(SPELL_CLASS);
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
				if (!levelMax.isValid())
				{
					Logging
						.errorPrint("LEVELMAX Formula in " + getTokenName() + " was not valid: " + levelMax.toString());
					return null;
				}
			}
			else if (tok.startsWith("LEVELMIN="))
			{
				levelMin = FormulaFactory.getFormulaFor(tok.substring(9));
				if (!levelMin.isValid())
				{
					Logging
						.errorPrint("LEVELMIN Formula in " + getTokenName() + " was not valid: " + levelMin.toString());
					return null;
				}
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
				Logging.errorPrint("Unknown restriction: " + tok + " in CHOOSE:SPELLS");
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

		public String getLSTformat()
		{
			StringBuilder sb = new StringBuilder();
			if (knownRequired != null)
			{
				sb.append("KNOWN=");
				sb.append(knownRequired ? "YES" : "NO");
			}
			if (maxLevel != null)
			{
				if (sb.length() > 0)
				{
					sb.append(';');
				}
				sb.append("LEVELMAX=");
				sb.append(maxLevel);
			}
			if (minLevel != null)
			{
				if (sb.length() > 0)
				{
					sb.append(';');
				}
				sb.append("LEVELMIN=");
				sb.append(minLevel);
			}
			return sb.toString();
		}

		@Override
		public int hashCode()
		{
			int prime = 31;
			int result = 1;
			result = prime * result + ((knownRequired == null) ? 0 : knownRequired.hashCode());
			result = prime * result + ((maxLevel == null) ? 0 : maxLevel.hashCode());
			result = prime * result + ((minLevel == null) ? 0 : minLevel.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Restriction other = (Restriction) obj;
			if (knownRequired == null)
			{
				if (other.knownRequired != null)
				{
					return false;
				}
			}
			else if (!knownRequired.equals(other.knownRequired))
			{
				return false;
			}
			if (maxLevel == null)
			{
				if (other.maxLevel != null)
				{
					return false;
				}
			}
			else if (!maxLevel.equals(other.maxLevel))
			{
				return false;
			}
			if (minLevel == null)
			{
                return other.minLevel == null;
			}
			else
			{
				return minLevel.equals(other.minLevel);
			}
        }
	}

	@Override
	public Class<Spell> getReferenceClass()
	{
		return SPELL_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return String.valueOf(getPrimitiveLST());
	}

	public abstract CharSequence getPrimitiveLST();

	public boolean allow(PlayerCharacter pc, int level, String source, Spell spell, CDOMList<Spell> optionalList)
	{
		if (restriction != null)
		{
			Formula maxLevel = restriction.maxLevel;
			if (maxLevel != null && (level > maxLevel.resolve(pc, source).intValue()))
			{
				return false;
			}
			Formula minLevel = restriction.minLevel;
			if (minLevel != null && (level < minLevel.resolve(pc, source).intValue()))
			{
				return false;
			}
			if (restriction.knownRequired != null)
			{
				String defaultbook = Globals.getDefaultSpellBook();
				boolean known = restriction.knownRequired;
				boolean found = false;
				for (PCClass cl : pc.getClassSet())
				{
					if (optionalList != null)
					{
						/*
						 * This may not be a precise test of intent, but given
						 * the weirdness we have on lists and the use of
						 * SPELLLIST tag in data to share lists between classes,
						 * this is probably the closest we can get
						 */
						if (!pc.hasSpellList(cl, optionalList))
						{
							continue;
						}
					}
					List<CharacterSpell> csl = pc.getCharacterSpells(cl, spell, defaultbook, -1);
					if (!csl.isEmpty())
					{
						/*
						 * Going to assume here that the level doesn't need to
						 * be rechecked... ?? - thpr Feb 26, 08
						 */
						found = true;
					}
				}
                return found == known;
			}
		}
		return true;
	}

	public boolean equalsRestrictedPrimitive(AbstractRestrictedSpellPrimitive other)
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

	public String getRestrictionLST()
	{
		return restriction == null ? "" : ('[' + restriction.getLSTformat() + ']');
	}

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Spell, R> c)
	{
		return c.convert(allSpells, this);
	}
}
