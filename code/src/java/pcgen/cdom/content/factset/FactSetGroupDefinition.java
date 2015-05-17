/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.factset;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.GroupDefinition;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;

/**
 * A FactSetGroupDefinition is a GroupDefinition built around a Fact Set,
 * specifically relying upon a FactSetInfo. This is effectively a factory that
 * can generate an appropriate FactSetGroup for a given value, based upon the
 * FactSetInfo provided at construction.
 * 
 * @param <T>
 *            The Type of object upon which this FactSetGroupDefinition can be
 *            used (the host of the fact set)
 * @param <F>
 *            The Type of object this FactSetGroupDefinition contains (the
 *            content of the fact set)
 */
public class FactSetGroupDefinition<T extends CDOMObject, F> implements
		GroupDefinition<T>
{

	/**
	 * The underlying FactSetInfo indicating static information about the Fact
	 * for which this FactSetGroupDefinition can create Primitives
	 */
	private final FactSetInfo<T, F> def;

	/**
	 * Constructs a new FactSetGroupDefinition with the given FactSetInfo.
	 * 
	 * @param fsi
	 *            The FactSetInfo underlying this FactSetGroupDefinition
	 * @throws IllegalArgumentException
	 *             if the given FactSetInfo is null
	 */
	public FactSetGroupDefinition(FactSetInfo<T, F> fsi)
	{
		if (fsi == null)
		{
			throw new IllegalArgumentException("Fact Definition cannot be null");
		}
		def = fsi;
	}

	/**
	 * @see pcgen.cdom.base.GroupDefinition#getPrimitiveName()
	 */
	@Override
	public String getPrimitiveName()
	{
		return def.getFactSetName();
	}

	/**
	 * @see pcgen.cdom.base.GroupDefinition#getGroupingState()
	 */
	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	/**
	 * @see pcgen.cdom.base.GroupDefinition#getReferenceClass()
	 */
	@Override
	public Class<T> getReferenceClass()
	{
		return def.getUsableLocation();
	}

	/**
	 * @see pcgen.cdom.base.GroupDefinition#getFormatManager()
	 */
	@Override
	public FormatManager<F> getFormatManager()
	{
		return def.getFormatManager();
	}

	/**
	 * @see pcgen.cdom.base.GroupDefinition#getPrimitive(pcgen.rules.context.LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public ObjectContainer<T> getPrimitive(LoadContext context, String value)
	{
		return new FactSetGroup<T, F>(context, def, value);
	}
}
