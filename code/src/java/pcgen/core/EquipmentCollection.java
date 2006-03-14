/*
 * EquipmentCollection.java
 * Copyright 2001 (C) Thomas G. W. Epperly
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
 * Created on April 7, 2001, 12:14 PM
 */
package pcgen.core;


/**
 * An interface for collections of equipment.
 * @author Thomas G. W. Epperly
 * @version $Revision$*
 */
interface EquipmentCollection
{
	/**
	 * Get the number of children.
	 * @return child count
	 */
	int getChildCount();

	/**
	 * Return the name of the equipment collection.
	 *
	 * @return the name of the equipment collection.
	 */
	String getName();

	/**
	 * Set the parent of this collection.
	 * @param parent
	 */
	void setParent(EquipmentCollection parent);

	/**
	 * Get the parent of this collection.
	 * @return parent
	 */
	EquipmentCollection getParent();

	/**
	 * Return <code>true</code> if this collection ever can accept
	 * children.
	 * @return TRUR or FALSE
	 */
	boolean acceptsChildren();

	/**
	 * Return <code>int 1</code> if this collection can take this object., otherwise a response dependant on why it can't
	 * @param aPC
	 * @param obj
	 * @return int
	 */
	int canContain(PlayerCharacter aPC, Object obj);

	/**
	 * Insert a child in the i'th position.
	 * @param aPC
	 * @param child
	 */
	void insertChild(PlayerCharacter aPC, Object child);

	/**
	 * Remove the i'th child.
	 * @param aPC
	 * @param i
	 */
	void removeChild(PlayerCharacter aPC, int i);
}
