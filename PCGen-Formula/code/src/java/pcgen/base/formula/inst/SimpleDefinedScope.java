/*
 * Copyright 2014-20 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formula.base.DefinedScope;

/**
 * A SimpleDefinedScope is a simple implementation of DefinedScope.
 */
public class SimpleDefinedScope implements DefinedScope
{
	/**
	 * The name of this DefinedScope.
	 */
	private final String name;

	/**
	 * Constructs a new DefinedScope with the given name.
	 * 
	 * @param name
	 *            The name of this SimpleDefinedScope
	 */
	public SimpleDefinedScope(String name)
	{
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	/*
	 * Note: Really DON'T WANT to implement .equals here. The reason this uses
	 * DefinedScope rather than just Strings is to force instance equality for two
	 * DefinedScope items with the same name.
	 * 
	 * Basically, we want to allow A.B.C and A.D.C to be legal DefinedScopes, meaning
	 * there are two DefinedScope objects called "C".
	 */
}
