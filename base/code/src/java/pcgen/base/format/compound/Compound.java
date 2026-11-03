/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format.compound;

import java.util.Collection;

import pcgen.base.util.FormatManager;
import pcgen.base.util.NamedIndirect;

/**
 * A Compound is a data type that has a primary value and some number of secondary
 * NamedIndirect values.
 * 
 * A useful example would be damage. The primary format could be dice (e.g. 1d6), and
 * there could be a secondary value of "Fire" indicating the damage type.
 */
public interface Compound
{
	/**
	 * Returns true if this Compound is compatible with the given FormatManager.
	 * 
	 * @param fm
	 *            The FormatManager to be checked if this Compound is compatible
	 * @return true if this Compound is compatible with the given FormatManager; false
	 *         otherwise
	 */
	public boolean isCompatible(FormatManager<?> fm);

	/**
	 * Adds a NamedIndirect to this Compound.
	 * 
	 * @param assoc
	 *            The NamedIndirect to be added to this Compound
	 */
	public void addSecondary(NamedIndirect<?> assoc);

	/**
	 * Returns the NamedIndirect in this Compound with the given name.
	 * 
	 * @param assocName
	 *            The name for which the NamedIndirect should be returned
	 * @return The NamedIndirect in this Compound with the given name
	 */
	public NamedIndirect<?> getSecondary(String assocName);

	/**
	 * Returns the primary value of the compound in persistent (String) format.
	 * 
	 * @return the primary value of the compound in persistent (String) format
	 */
	public String getPrimaryUnconverted();

	/**
	 * Returns the primary value of the compound.
	 * 
	 * @return the primary value of the compound
	 */
	public Object getPrimary();

	/**
	 * Returns a Collection of the names of the NamedIndirect objects in this Compound.
	 * 
	 * @return A Collection of the names of the NamedIndirect objects in this Compound
	 */
	public Collection<String> getSecondaryNames();
}
