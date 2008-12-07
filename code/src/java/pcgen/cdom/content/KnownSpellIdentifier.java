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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.spell.Spell;

/**
 * A KnownSpellIdentifier is an object that identifies Spell objects that are 
 * known at a specific spell level.  The Spell objects are stored by reference.
 */
public class KnownSpellIdentifier extends ConcretePrereqObject
{

	/**
	 * The underlying reference indicating the Spells that are part of this
	 * KnownSpellsIdentifier (subject to level limitations imposed by
	 * spellLevel)
	 */
	private final CDOMReference<Spell> ref;

	/**
	 * The spell level contained in this KnownSpellIdentifier. May be null if
	 * this KnownSpellIdentifier is not limited by level.
	 */
	private final Integer spellLevel;

	/**
	 * Creates a new KnownSpellIdentifier, containing spells from the given
	 * CDOMReference. The spells contained in this KnownSpellIdentifier will be
	 * limited by the given levelLimit, or null can be used to indicate no level
	 * limit on the spells in the reference.
	 * 
	 * @param sr
	 *            The CDOMReference containing the spells to be part of this
	 *            KnownSpellIdentifier.  May be null if
	 *            this KnownSpellIdentifier is not limited by Spell key.
	 * @param levelLimit
	 *            The spell level of this KnownSpellIdentifier. May be null if
	 *            this KnownSpellIdentifier is not limited by level.
	 * @throws IllegalArgumentException
	 *             if the given CDOMReference is null
	 */
	public KnownSpellIdentifier(CDOMReference<Spell> sr, Integer levelLimit)
	{
		if (sr == null && levelLimit == null)
		{
			throw new IllegalArgumentException(
				"Known Spell Identifier cannot have null spell reference and level limit");
		}
		ref = sr;
		spellLevel = levelLimit;
	}

	/**
	 * Returns true if the given Spell is contained in this
	 * KnownSpellIdentifier.
	 * 
	 * A Spell is contained in this KnownSpellIdentifier when it is part of the
	 * CDOMReference provided during construction of the KnownSpellIdentifier
	 * and either the levelLimit provided at construction is null or the given
	 * testSpellLevel matches the levelLimit provided at construction.
	 * 
	 * @param s
	 *            The spell to be tested to determine if it is contained in the
	 *            KnownSpellIdentifier
	 * @param testSpellLevel
	 *            The level of the spell for purposes of the test
	 * @return true if the given Spell is contained in this KnownSpellIdentifier
	 */
	public boolean matchesFilter(Spell s, int testSpellLevel)
	{
		return (ref == null || ref.contains(s))
			&& (spellLevel == null || testSpellLevel == spellLevel);
	}

	/**
	 * Returns the CDOMReference indicating the Spells that are part of this
	 * KnownSpellsIdentifier.
	 * 
	 * @return the CDOMReference indicating the Spells that are part of this
	 *         KnownSpellsIdentifier.
	 */
	public CDOMReference<Spell> getSpellReference()
	{
		return ref;
	}

	/**
	 * Returns the spell level contained in this KnownSpellIdentifier. May be
	 * null if this KnownSpellIdentifier is not limited by level.
	 * 
	 * @return The spell level contained in this KnownSpellIdentifier
	 */
	public Integer getSpellLevel()
	{
		return spellLevel;
	}

	public Collection<Spell> getContainedSpells(List<ClassSpellList> list)
	{
		if (ref == null)
		{
			return Globals.getSpellsIn(spellLevel, list);
		}
		List<Spell> spellList = new ArrayList<Spell>();
		for (Spell sp : ref.getContainedObjects())
		{
			HashMapToList<CDOMList<Spell>, Integer> hml =
					SpellLevel.getMasterLevelInfo(null, sp);
			for (CDOMList<Spell> cdomList : hml.getKeySet())
			{
				if (list.contains(cdomList))
				{
					if (spellLevel == null
						|| hml.getListFor(cdomList).contains(spellLevel))
					{
						spellList.add(sp);
					}
				}
			}
		}
		return spellList;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this KnownSpellIdentifier
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return spellLevel == null ? ref.hashCode() : spellLevel.intValue()
				* ref.hashCode();
	}

	/**
	 * Returns true if this KnownSpellIdentifier is equal to the given Object.
	 * Equality is defined as being another KnownSpellIdentifier object with
	 * equal spell level and underlying reference.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (ref == null)
		{
			return other.ref == null && spellLevel.equals(other.spellLevel);
		}
		return spellLevel.equals(other.spellLevel) && ref.equals(other.ref);
	}

	/**
	 * Returns a representation of this KnownSpellIdentifier, suitable for
	 * storing in an LST file.
	 */
	public String getLSTformat()
	{
		return ref.getLSTformat();
	}
}
