/*
 * Copyright 2016-18 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

import java.util.Collection;

import pcgen.base.solver.Modifier;
import pcgen.base.util.Indirect;

/**
 * PCGenModifier is a Modifier that has additional characteristics to support PCGen
 * 
 * @param <T>
 *            The format that this PCGenModifier acts upon
 */
public interface PCGenModifier<T> extends Modifier<T>
{

	/**
	 * Adds an Association to this PCGenModifier.
	 * 
	 * @param assocInstructions
	 *            The instructions of the Association to be added to this PCGenModifier
	 * 
	 * @throws IllegalArgumentException
	 *             if the given instructions are not valid or are not a supported
	 *             Association for this PCGenModifier
	 */
	public void addAssociation(String assocInstructions);

	/**
	 * Returns a Collection of the instructions (String format) for the Associations on
	 * this PCGenModifier.
	 * 
	 * Ownership of the returned Collection should be transferred to the calling object,
	 * and no reference to the underlying contents of the PCGenModifier should be
	 * maintained. (There should be no way for the PCGenModifier to alter this Collection
	 * after it is returned and no method for the returned Collection to modify the
	 * PCGenModifier)
	 * 
	 * @return the instructions (String format) for the Associations on this PCGenModifier
	 */
	public Collection<String> getAssociationInstructions();

	/**
	 * Add object references to this PCGenModifier. These are captured solely as
	 * dependency management.
	 * 
	 * @param collection
	 *            The Collection of Indirect objects that this PCGenModifier references.
	 */
	public void addReferences(Collection<Indirect<?>> collection);

}
