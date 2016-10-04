/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

public class BasicClassIdentity<T> implements ClassIdentity<T>
{

	private final Class<T> underlyingClass;

	public BasicClassIdentity(Class<T> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
				"Class for BasicClassIdentity cannot be null");
		}
		if (Categorized.class.isAssignableFrom(cl))
		{
			throw new InternalError(cl
					+ " is categorized but was identified without a category");
		}
		underlyingClass = cl;
	}

	@Override
	public String getName()
	{
		return underlyingClass.getSimpleName();
	}

	@Override
	public Class<T> getChoiceClass()
	{
		return underlyingClass;
	}

	public static <T> ClassIdentity<T> getIdentity(Class<T> cl)
	{
		//TODO Need .equals and .hashCode due to this, right?
		return new BasicClassIdentity<>(cl);
	}

}
