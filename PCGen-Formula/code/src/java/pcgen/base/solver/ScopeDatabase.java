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
package pcgen.base.solver;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.inst.ScopeInformation;

/**
 * ScopeDatabase is a utility class providing a database that maps VariableScope
 * objects to ScopeInformation objects.
 */
public final class ScopeDatabase
{

	/**
	 * The map that associates VariableScope objects to ScopeInformation
	 * objects.
	 */
	private Map<ScopeInstance, ScopeInformation> map =
			new HashMap<ScopeInstance, ScopeInformation>();

	/**
	 * Returns the ScopeInformation Object for the given VariableScope.
	 * 
	 * If a ScopeInformation has not yet been created, a new ScopeInformation is
	 * build and initialized with the given VariableScope.
	 * 
	 * @param fm
	 *            The FormulaManager to be used to initialize a new
	 *            ScopeInformation, if necessary
	 * @param scope
	 *            The VariableScope for which the ScopeInformation should be
	 *            returned
	 * @return The ScopeInformation for the given VariableScope
	 */
	public ScopeInformation getScopeInformation(FormulaManager fm,
		ScopeInstance scope)
	{
		ScopeInformation scopeInfo = map.get(scope);
		if (scopeInfo == null)
		{
			scopeInfo = new ScopeInformation(fm, scope);
			map.put(scope, scopeInfo);
		}
		return scopeInfo;
	}
}
