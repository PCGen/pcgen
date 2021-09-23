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
package pcgen.base.format;

import java.util.Optional;

import pcgen.base.math.OrderedPair;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * A OrderedPairManager is a FormatManager that provides services for
 * OrderedPair objects.
 */
public class OrderedPairManager implements FormatManager<OrderedPair>
{

	/**
	 * Converts the given String to an object of the type processed by this
	 * FormatManager.
	 */
	@Override
	public OrderedPair convert(String s)
	{
		return OrderedPair.valueOf(s);
	}

	/**
	 * Converts the given String to an Indirect containing an object of the type
	 * processed by this FormatManager.
	 */
	@Override
	public Indirect<OrderedPair> convertIndirect(String s)
	{
		return new BasicIndirect<>(this, convert(s));
	}

	/**
	 * "Unconverts" the object (converts the object to a "serializable" String
	 * format that can be reinterpreted by the convert* methods).
	 */
	@Override
	public String unconvert(OrderedPair op)
	{
		return op.getPreciseX() + "," + op.getPreciseY();
	}

	/**
	 * The Class that this FormatManager can convert or unconvert.
	 */
	@Override
	public Class<OrderedPair> getManagedClass()
	{
		return OrderedPair.class;
	}

	/**
	 * The String used to refer to this format in files like the variable
	 * definition file.
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
		return (o == this) || (o instanceof OrderedPairManager);
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.empty();
	}

	@Override
	public boolean isDirect()
	{
		return true;
	}
}
