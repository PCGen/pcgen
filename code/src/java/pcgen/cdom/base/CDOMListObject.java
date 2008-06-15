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
 * This is an abstract object intended to be used as a basis for "concrete"
 * CDOMList objects.
 * 
 * CDOMListObject provides basic equality of list
 * 
 * @param <T>
 *            The type of object contained in the CDOMList
 */
public abstract class CDOMListObject<T extends CDOMObject> extends CDOMObject
		implements CDOMList<T>
{

	/**
	 * Returns the consistent-with-equals hashCode for this CDOMListObject
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	/**
	 * Returns true if this CDOMListObject is equal to the given object.
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
		if (o instanceof CDOMListObject)
		{
			CDOMListObject<?> other = (CDOMListObject<?>) o;
			return o.getClass().equals(getClass())
					&& other.getListClass().equals(getListClass())
					&& getKeyName().equals(other.getKeyName());
		}
		return false;
	}
}
