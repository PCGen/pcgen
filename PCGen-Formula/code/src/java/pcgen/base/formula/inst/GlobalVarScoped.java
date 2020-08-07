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
package pcgen.base.formula.inst;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.VarScoped;

/**
 * A GlobalVarScoped is the object that represents the "Global" scope.
 * 
 * This is provided so that there is a known place for getting a Global VarScoped object.
 */
public class GlobalVarScoped implements VarScoped
{

	/**
	 * The name of this GlobalVarScoped.
	 */
	private final String name;

	/**
	 * Constructs a new GlobalVarScoped with the given (non-null) name.
	 * 
	 * @param name
	 *            The name of this GlobalVarScoped
	 */
	public GlobalVarScoped(String name)
	{
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String getKeyName()
	{
		return name;
	}

	@Override
	public Optional<String> getScopeName()
	{
		//Empty to indicate Global
		return Optional.empty();
	}

	@Override
	public Optional<VarScoped> getVariableParent()
	{
		//Empty to indicate Global
		return Optional.empty();
	}

	@Override
	public String toString()
	{
		return "Global Variable Scope (" + name + ")";
	}
}
