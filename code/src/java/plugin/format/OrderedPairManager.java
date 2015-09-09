/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.format;

import pcgen.base.geom.OrderedPair;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.BasicObjectContainer;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;

/**
 * A OrderedPairManager is a FormatManager that provides services for
 * OrderedPair objects.
 */
public class OrderedPairManager implements FormatManager<OrderedPair>
{

	/**
	 * @see pcgen.rules.types.FormatManager#convert(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public OrderedPair convert(LoadContext context, String s)
	{
		return OrderedPair.valueOf(s);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertIndirect(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public Indirect<OrderedPair> convertIndirect(LoadContext context, String s)
	{
		return new BasicIndirect<OrderedPair>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertObjectContainer(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public ObjectContainer<OrderedPair> convertObjectContainer(
		LoadContext context, String s)
	{
		return new BasicObjectContainer<OrderedPair>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#unconvert(java.lang.Object)
	 */
	@Override
	public String unconvert(OrderedPair gp)
	{
		return gp.getPreciseX() + "," + gp.getPreciseY();
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getType()
	 */
	@Override
	public Class<OrderedPair> getType()
	{
		return OrderedPair.class;
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getIdentifierType()
	 */
	@Override
	public String getIdentifierType()
	{
		return "ORDEREDPAIR";
	}

	@Override
	public int hashCode()
	{
		return 77421;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof OrderedPairManager;
	}
}
