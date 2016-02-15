/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.analysis;

import pcgen.base.formula.base.DependencyKey;

/**
 * DependencyKeyUtilities is a storage location for keys within the
 * PCGen-Formula infrastructure.
 * 
 * External storage helps avoid dependency loops.
 */
public final class DependencyKeyUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private DependencyKeyUtilities()
	{
	}

	/**
	 * Key for Argument dependencies
	 */
	public static final DependencyKey<ArgumentDependencyManager> DEP_ARGUMENT =
			new DependencyKey<>();

	/**
	 * Key for Variable dependencies
	 */
	public static final DependencyKey<VariableDependencyManager> DEP_VARIABLE =
			new DependencyKey<>();

}
