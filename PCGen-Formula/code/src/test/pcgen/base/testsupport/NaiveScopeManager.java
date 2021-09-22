/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.testsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ImplementedScopeManager;
import pcgen.base.formula.base.RelationshipManager;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.inst.SimpleImplementedScope;

/**
 * A NaiveScopeManager is a manager of scopes and ImplementedScope objects, but it
 * is extremely naive relative to error checking.
 * 
 * This will not check that a full set of scopes is loaded, it will not check for loops in
 * heritage, it will not even check if a requested ImplementedScope is valid. It is
 * designed as a test framework class.
 */
public class NaiveScopeManager implements ScopeImplementer, RelationshipManager,
		ImplementedScopeManager
{

	/**
	 * Defined Scopes mapped by parent
	 */
	private final Map<String, String> parents = new HashMap<>();
	
	/**
	 * Implemented scopes mapped by fully qualified name
	 */
	private final Map<String, ImplementedScope> implemented = new HashMap<>();
	
	public void registerScope(String parent, String child)
	{
		parents.put(child, parent);
	}

	@Override
	public boolean isRelated(ImplementedScope firstScope,
		ImplementedScope secondScope)
	{
		String name1 = firstScope.getName();
		String name2 = secondScope.getName();
		return name1.startsWith(name2) || name2.startsWith(name1);
	}

	@Override
	public ImplementedScope getImplementedScope(String string)
	{
		ImplementedScope scope = implemented.get(string);
		if (scope == null)
		{
			int dotLoc = string.lastIndexOf('.');
			Optional<ImplementedScope> implScope;
			if (dotLoc == -1)
			{
				implScope = Optional.empty();
			}
			else
			{
				String parentName = string.substring(0, dotLoc);
				implScope = Optional.of(getImplementedScope(parentName));
			}
			scope = new SimpleImplementedScope(string, implScope.isEmpty());
			addParents(scope, implScope);
			implemented.put(string, scope);
		}
		return scope;
	}

	private void addParents(ImplementedScope scope,
		Optional<ImplementedScope> implScope)
	{
		Optional<? extends ImplementedScope> workingScope = implScope;
		while (workingScope.isPresent())
		{
			ImplementedScope thisScope = workingScope.get();
			((SimpleImplementedScope) scope).drawsFrom(thisScope);
			String parentName = parents.get(thisScope.getName());
			if (parentName == null)
			{
				break;
			}
			workingScope = Optional.ofNullable(getImplementedScope(parentName));
		}
	}

	@Override
	public boolean recognizesScope(ImplementedScope s)
	{
		return s.equals(implemented.get(s.getName()));
	}
}
