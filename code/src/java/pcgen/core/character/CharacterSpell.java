/*
 * CharacterSpell.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.character;

import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class CharacterSpell implements Comparable
{
	private final List infoList = new ArrayList();
	private final PObject owner; // PCClass/Race/etc. in whose list this object resides
	private final Spell spell;

	/**
	 * Constructor
	 * @param o
	 * @param aSpell
	 */
	public CharacterSpell(final PObject o, final Spell aSpell)
	{
		owner = o;
		spell = aSpell;
	}

	/**
	 * bookName = name of spellbook/list
	 * level = actual level of spell (adjusted by feats)
	 * specialty: -1 = inSpecialty insensitive
	 * specialty: 0 = inSpecialty==false
	 * specialty: 1 = inSpecialty==true
	 * Returns index of SpellInfo in infoList, or -1 if it doesn't exist
	 * @param bookName
	 * @param level
	 * @param specialty
	 * @return info index
	 */
	public int getInfoIndexFor(final String bookName, final int level, final int specialty)
	{
		if (infoList.isEmpty())
		{
			return -1;
		}

		boolean sp = specialty == 1;

		if (sp)
		{
			sp = isSpecialtySpell();
		}

		int i = 0;
		final Iterator x = infoList.iterator();

		while (x.hasNext())
		{
			final SpellInfo s = (SpellInfo) x.next();

			if (("".equals(bookName) || bookName.equals(s.getBook()))
				&& (level == -1 || s.getActualLevel() == level) && (specialty == -1 || sp))
			{
				return i;
			}

			i++;
		}

		return -1;
	}

	/**
	 * Get info list iterator
	 * @return info list iterator
	 */
	public Iterator getInfoListIterator()
	{
		return infoList.iterator();
	}

	/**
	 * Get Owner
	 * @return owner
	 */
	public PObject getOwner()
	{
		return owner;
	}

	/**
	 * is speciality spell
	 * @return TRUE if speciality spell
	 */
	public boolean isSpecialtySpell()
	{
		final boolean result;

		if (spell == null)
		{
			result = false;
		}
		else if (owner instanceof Domain)
		{
			result = true;
		}
		else if (owner instanceof PCClass)
		{
			final PCClass a = (PCClass) owner;
			result = a.isSpecialtySpell(spell);
		}
		else
		{
			result = false;
		}

		return result;
	}

	/**
	 * Get spell
	 * @return spell
	 */
	public Spell getSpell()
	{
		return spell;
	}

	/**
	 * Get the spell info
	 * @param bookName
	 * @param level
	 * @param specialty
	 * @return SpellInfo
	 */
	public SpellInfo getSpellInfoFor(final String bookName, final int level, final int specialty)
	{
		return getSpellInfoFor(bookName, level, specialty, null);
	}

	/**
	 * Get the Spell info
	 * @param bookName
	 * @param level
	 * @param specialty
	 * @param featList
	 * @return Spell Info
	 */
	public SpellInfo getSpellInfoFor(final String bookName, final int level, final int specialty, final List featList)
	{
		if (infoList.isEmpty())
		{
			return null;
		}

		boolean sp = specialty == 1;

		if (sp)
		{
			sp = isSpecialtySpell();
		}

		final Iterator x = infoList.iterator();

		while (x.hasNext())
		{
			final SpellInfo s = (SpellInfo) x.next();

			if (("".equals(bookName) || bookName.equals(s.getBook()))
				&& (level == -1 || s.getActualLevel() == level) && (specialty == -1 || sp)
				&& (featList == null
				|| featList.isEmpty() && (s.getFeatList() == null || s
					.getFeatList().isEmpty())
				|| s.getFeatList() != null && featList.toString()
					.equals(s.getFeatList().toString())))
			{
				return s;
			}
		}

		return null;
	}

	/**
	 * Add Spell info
	 * @param level
	 * @param times
	 * @param book
	 * @return SpellInfo
	 */
	public SpellInfo addInfo(final int level, final int times, final String book)
	{
		return addInfo(level, times, book, null);
	}

	/**
	 * Add Spell info
	 * @param level
	 * @param times
	 * @param book
	 * @param featList
	 * @return SpellInfo
	 */
	public SpellInfo addInfo(final int level, final int times,
			final String book, final List featList)
	{
		final SpellInfo si = new SpellInfo(this, level, times, book);

		if (featList != null)
		{
			si.addFeatsToList(featList);
		}

		infoList.add(si);

		return si;
	}

	/**
	 * Compares with another object. The implementation compares the CharacterSpell's contained spell object with the
	 * passed-in CharacterSpell's spell object.
	 *
	 * @param obj the CharacterSpell to compare with
	 * @return a negative integer, zero, or a positive integer as this object
	 *         is less than, equal to, or greater than the specified object.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(final Object obj)
	{
		// this should throw a ClassCastException for
		// non-CharacterSpell just like the Comparable
		// interface calls for BUT IT DOESN'T!!!
		return spell.compareTo(((CharacterSpell) obj).spell);
	}

	/**
	 * returns true if
	 * obj.getName() equals this.getName()
	 * or obj == this
	 * @param obj
	 * @return true if equal
	 */
	public boolean equals(final Object obj)
	{
		return obj != null && obj instanceof CharacterSpell && ((CharacterSpell) obj).getName().equals(getName());
	}

	/**
	 * this method is used the same as equals() but for hash tables
	 * @return hash code
	 */
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * Remove spell info
	 * @param x
	 */
	public void removeSpellInfo(final SpellInfo x)
	{
		if (x != null)
		{
			infoList.remove(x);
		}
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	/**
	 * Returns the Spell's Name for Tree's display
	 *
	 * @return the Spell's Name for Tree's display
	 */
	public String toString()
	{
		final String result;

		if (spell == null)
		{
			result = "";
		}
		else
		{
			result = spell.getDisplayName();
		}

		return result;
	}

	/**
	 * Returns the name of the spell for this Character Spell
	 * @return name
	 */
	private String getName()
	{
		final StringBuffer buf = new StringBuffer(owner.toString());

		if (spell != null)
		{
			buf.append(':').append(spell.getDisplayName());
		}
		return buf.toString();
	}
}
