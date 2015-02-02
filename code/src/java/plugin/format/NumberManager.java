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

import pcgen.base.lang.NumberUtilities;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.BasicObjectContainer;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;

/**
 * A NumberManager is a FormatManager that provides services for Numbers
 * (effectively Double and Integer)
 */
public class NumberManager implements FormatManager<Number>
{

	/**
	 * @see pcgen.rules.types.FormatManager#convert(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public Number convert(LoadContext context, String s)
	{
		return NumberUtilities.getNumber(s);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertIndirect(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public Indirect<Number> convertIndirect(LoadContext context, String s)
	{
		return new BasicIndirect<Number>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertObjectContainer(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public ObjectContainer<Number> convertObjectContainer(LoadContext context,
		String s)
	{
		return new BasicObjectContainer<Number>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#unconvert(java.lang.Object)
	 */
	@Override
	public String unconvert(Number s)
	{
		return s.toString();
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getType()
	 */
	@Override
	public Class<Number> getType()
	{
		return Number.class;
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getIdentifierType()
	 */
	@Override
	public String getIdentifierType()
	{
		return "NUMBER";
	}

	@Override
	public int hashCode()
	{
		return 7987;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof NumberManager;
	}
}
