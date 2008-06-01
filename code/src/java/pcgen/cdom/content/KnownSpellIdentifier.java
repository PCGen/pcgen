/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.content;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.spell.Spell;

public class KnownSpellIdentifier extends ConcretePrereqObject implements
		LSTWriteable
{

	private final CDOMReference<Spell> ref;

	private final Integer spellLevel;

	public KnownSpellIdentifier(CDOMReference<Spell> sr, Integer levelLimit)
	{
		if (sr == null)
		{
			throw new IllegalArgumentException("Spell Reference cannot be null");
		}
		ref = sr;
		spellLevel = levelLimit;
	}

	public boolean matchesFilter(Spell s, int testSpellLevel)
	{
		return ref.contains(s)
				&& (spellLevel == null || testSpellLevel == spellLevel);
	}

	public CDOMReference<Spell> getLimit()
	{
		return ref;
	}

	public Integer getSpellLevel()
	{
		return spellLevel;
	}

	@Override
	public int hashCode()
	{
		return spellLevel == null ? ref.hashCode() : spellLevel.intValue()
				* ref.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof KnownSpellIdentifier))
		{
			return false;
		}
		KnownSpellIdentifier other = (KnownSpellIdentifier) o;
		if (spellLevel == null)
		{
			return other.spellLevel == null && ref.equals(other.ref);
		}
		return ((spellLevel == null && other.spellLevel == null) || spellLevel
				.equals(other.spellLevel))
				&& ref.equals(other.ref);
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}
}
