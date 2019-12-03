/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

/**
 * A CDOMList is an identifier used to identify CDOMObject relationships.
 * 
 * It is intended to be used in situations where groups of CDOMObjects require
 * some form of additional information beyond mere presence. One example would
 * be the Spell Level of a given Spell in a CDOMList of Spells.
 * 
 * For grouping of objects without associations in the relationships, you should
 * consider using items in pcgen.cdom.reference.
 * 
 * @param <T>
 *            The type of object contained in the CDOMList
 */
public interface CDOMList<T extends CDOMObject> extends PrereqObject
{
	/**
	 * Returns the Class of Object this CDOMList will identify
	 * 
	 * @return the Class of Object this CDOMList will identify
	 */
    Class<T> getListClass();

	/**
	 * Returns the key name for this CDOMList. This is the unique identifier of
	 * the CDOMList. Theoretically, this is unique relative to the ListClass
	 * (from getListClass()), not globally unique. However, this "uniqueness" is
	 * not enforced (since this is merely an interface).
	 * 
	 * @return The key name for this CDOMList
	 */
    String getKeyName();

	/**
	 * Returns true if this CDOMList has the given Type. This test the CDOMList
	 * itself, not the contents of the CDOMList. A CDOMList can have a type, for
	 * example, because a ClassSpellList (A form of CDOMList) can be "Arcane" or
	 * "Divine".
	 * 
	 * @param type
	 *            The String representation of the Type that this CDOMList
	 *            should be tested for.
	 * @return true if this CDOMList has the given type; false otherwise.
	 */
    boolean isType(String type);

	/**
	 * Returns a representation of this CDOMList, suitable for storing in
	 * an LST file.
	 * 
	 * @return A representation of this CDOMList, suitable for storing in
	 *         an LST file.
	 */
    String getLSTformat();

}
