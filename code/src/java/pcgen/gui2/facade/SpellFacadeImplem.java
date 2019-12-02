/**
 * Copyright James Dempsey, 2011
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
package pcgen.gui2.facade;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.facade.core.SpellFacade;
import pcgen.util.SortKeyAware;

/**
 * The Class {@code SpellFacadeImplem} is a proxy for a spell used for
 * displaying the spell on the UI. 
 *
 * 
 */
public class SpellFacadeImplem implements SpellFacade, SortKeyAware
{
	private final PlayerCharacter pc;
	private final Spell spell;
	private final CharacterSpell charSpell;
	private final SpellInfo spellInfo;

	public SpellFacadeImplem(PlayerCharacter pc, Spell spell, CharacterSpell charSpell, SpellInfo spellInfo)
	{
		this.pc = pc;
		this.spell = spell;
		this.charSpell = charSpell;
		this.spellInfo = spellInfo;

	}

	@Override
	public String getSource()
	{
		return SourceFormat.getFormattedString(spell, Globals.getSourceDisplay(), true);
	}

	@Override
	public String getSourceForNodeDisplay()
	{
		return SourceFormat.getFormattedString(spell, SourceFormat.LONG, true);
	}

	@Override
	public String getKeyName()
	{
		return spell.getKeyName();
	}

	@Override
	public String getSchool()
	{
		return spell.getListAsString(ListKey.SPELL_SCHOOL);
	}

	@Override
	public String getSubschool()
	{
		return spell.getListAsString(ListKey.SPELL_SUBSCHOOL);
	}

	@Override
	public String[] getDescriptors()
	{
		List<String> descriptors = spell.getListFor(ListKey.SPELL_DESCRIPTOR);
		if (descriptors == null)
		{
			return new String[]{};
		}
		return descriptors.toArray(new String[0]);
	}

	@Override
	public String getComponents()
	{
		return spell.getListAsString(ListKey.COMPONENTS);
	}

	@Override
	public String getRange()
	{
		return pc.getSpellRange(charSpell, spellInfo);
	}

	@Override
	public String getDuration()
	{
		return pc.parseSpellString(charSpell, spell.getListAsString(ListKey.DURATION));
	}

	@Override
	public String getCastTime()
	{
		return spell.getListAsString(ListKey.CASTTIME);
	}

	/**
	 * @return the spell
	 */
	Spell getSpell()
	{
		return spell;
	}

	/**
	 * @return the charSpell
	 */
	@Override
	public CharacterSpell getCharSpell()
	{
		return charSpell;
	}

	/**
	 * @return the spellInfo
	 */
	@Override
	public SpellInfo getSpellInfo()
	{
		return spellInfo;
	}

	@Override
	public String toString()
	{
		StringBuilder buff = new StringBuilder();
		if (spell != null)
		{
			buff.append(spell);
		}
		else if (charSpell != null)
		{
			buff.append(charSpell);
		}
		if (charSpell != null && charSpell.getOwner() instanceof Domain)
		{
			buff.append(" [").append(charSpell.getOwner()).append("]");
		}
		if (spellInfo != null)
		{
			buff.append(spellInfo);
		}
		return buff.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((charSpell == null) ? 0 : charSpell.hashCode());
		result = prime * result + ((pc == null) ? 0 : pc.hashCode());
		result = prime * result + ((spell == null) ? 0 : spell.hashCode());
		result = prime * result + ((spellInfo == null) ? 0 : spellInfo.hashCode());
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
		SpellFacadeImplem other = (SpellFacadeImplem) obj;
		if (charSpell == null)
		{
			if (other.charSpell != null)
			{
				return false;
			}
		}
		else if (!charSpell.equals(other.charSpell))
		{
			return false;
		}
		if (pc == null)
		{
			if (other.pc != null)
			{
				return false;
			}
		}
		else if (!pc.equals(other.pc))
		{
			return false;
		}
		if (spell == null)
		{
			if (other.spell != null)
			{
				return false;
			}
		}
		else if (!spell.equals(other.spell))
		{
			return false;
		}
		if (spellInfo == null)
		{
            return other.spellInfo == null;
		}
		else return spellInfo.equals(other.spellInfo);
    }

	@Override
	public boolean isNamePI()
	{
		return spell.isNamePI();
	}

	@Override
	public String getType()
	{
		return spell.getType();
	}

	@Override
	public String getSortKey()
	{
		String sortKey = spell.get(StringKey.SORT_KEY);
		if (sortKey == null)
		{
			sortKey = spell.getDisplayName();
		}
		return sortKey;
	}

}
