/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula;

import java.util.List;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.VarContainer;

/**
 * A PCGenScoped is an extension of VarScoped specialized for PCGen to have the ability
 * for an object to have children of various types.
 */
public interface PCGenScoped extends VarContainer, VarScoped
{
	/**
	 * Returns the local child of the given child type and child name. Returns null if no
	 * such type or no child of that type with the given name exists.
	 * 
	 * @param childType
	 *            The child type for which the child should be returned
	 * @param childName
	 *            The name of the child of the given type that should be returned
	 * @return The local child of the given child type and child name
	 */
    PCGenScoped getLocalChild(String childType, String childName);

	/**
	 * Returns the List of child types that this PCGenScoped contains.
	 * 
	 * Contract for implementations of this method: Will not return null (return an empty
	 * list instead).
	 * 
	 * @return The List of child types that this PCGenScoped contains
	 */
    List<String> getChildTypes();

	/**
	 * Returns the List of children of the given child type. Returns null if this
	 * PCGenScoped has no children of the given type.
	 * 
	 * @param childType
	 *            The child type for which the list of children should be returned
	 * @return The List of children of the given child type
	 */
    List<PCGenScoped> getChildren(String childType);
}
