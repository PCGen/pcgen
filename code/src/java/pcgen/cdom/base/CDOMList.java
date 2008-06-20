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
public interface CDOMList<T extends PrereqObject> extends PrereqObject
{
	/**
	 * Returns the Class of Object this CDOMList will identify
	 * 
	 * @return the Class of Object this CDOMList will identify
	 */
	public Class<T> getListClass();
}
