/*
 * Copyright 2018 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.base;

import pcgen.base.util.FormatManager;

/**
 * An ObjectManager is an extension of a FormatManager used in PCGen to serve as a
 * "Groupable" FormatManager.
 * 
 * @param <T>
 *            The format of object for which this GroupableFormatManager provides services
 */
public interface GroupableFormatManager<T extends Loadable> extends FormatManager<T>
{
	/**
	 * Returns true if this GroupableFormatManager contains the given object.
	 * 
	 * Note that this is testing *object* presence. This will not return true if a
	 * reference for the given object has been requested; it will only return true if the
	 * object has actually been constructed by or imported into this
	 * GroupableFormatManager.
	 * 
	 * @param o
	 *            The object to be checked if it is present in this
	 *            GroupableFormatManager.
	 * @return true if this GroupableFormatManager contains the object; false otherwise.
	 */
	public boolean containsObject(Object o);
}
