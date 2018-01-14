/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
	 * Returns the user priority for this PCGenModifier.
	 * 
	 * @return The user priority for this PCGenModifier
	 */
	public int getUserPriority();

	/**
	 * Add object references to this PCGenModifier. These are captured solely as
	 * dependency management.
	 * 
	 * @param collection
	 *            The Collection of Indirect objects that this PCGenModifier references.
	 */
	public void addReferences(Collection<Indirect<?>> collection);
}
