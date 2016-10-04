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

public class CategorizedClassIdentity<T extends Categorized<T>> implements
		ClassIdentity<T>
{

	private final Class<T> underlyingClass;
	private final Category<T> underlyingCategory;

	public CategorizedClassIdentity(Class<T> cl, Category<T> cat)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
				"Class for BasicClassIdentity cannot be null");
		}
		if (!Categorized.class.isAssignableFrom(cl))
		{
			throw new InternalError(cl
					+ " is not categorized but was identified with a category");
		}
		if (cat == null)
		{
			throw new IllegalArgumentException(
				"Category for BasicClassIdentity cannot be null");
		}
		underlyingClass = cl;
		underlyingCategory = cat;
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

	public static <T extends Categorized<T>> ClassIdentity<T> getIdentity(
		Class<T> cl, Category<T> cat)
	{
		return new CategorizedClassIdentity<>(cl, cat);
	}

	public Category<T> getCategory()
	{
		return underlyingCategory;
	}

}
