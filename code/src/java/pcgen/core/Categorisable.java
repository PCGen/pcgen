/*
 * Categorisable.java
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
 * Current Version: $Revision: 1.1 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2005/09/12 22:04:49 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

/**
 * This is the interface for objects that can be stored in a
 * CategorisableStore
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.1 $
 */
public interface Categorisable
{
	/**
	 * Get the category of this ability
	 *
	 * @return  The category of this Ability
	 */
	public abstract String getCategory();

	/**
	 * Get the Key of this object.  Note the key must be combined with the
	 * Category in order to be unique
	 *
	 * @return  the key that identifies this object
	 */
	public abstract String getKeyName();

	/**
	 * Get the name of the Object
	 *
	 * @return  The name
	 */
	public abstract String getName();
}
