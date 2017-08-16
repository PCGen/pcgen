/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

import java.util.Collection;

import pcgen.base.util.Indirect;

/**
 * An AbstractPCGenModifier is a PCGenModifier that supports generalized management of
 * references.
 * 
 * @param <T>
 *            The format that this AbstractPCGenModifier acts upon
 */
public abstract class AbstractPCGenModifier<T> implements PCGenModifier<T>
{

	/**
	 * The object references referred to by the embedded AbstractPCGenModifier.
	 * 
	 * NOTE: DO NOT DELETE THIS EVEN THOUGH IT APPEARS UNUSED. Its use is holding the
	 * references so that they are not garbage collected.
	 */
	@SuppressWarnings("unused")
	private Collection<Indirect<?>> references;

	@Override
	public void addReferences(Collection<Indirect<?>> collection)
	{
		references = collection;
	}
}
