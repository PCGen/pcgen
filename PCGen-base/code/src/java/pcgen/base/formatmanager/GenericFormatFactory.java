/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formatmanager;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;

/**
 * An GenericFormatFactory builds a FormatManager supporting a class stored in
 * an ObjectDatabase.
 * 
 * @param <T> The format of object managed by this GenericFormatFactory
 */
public class GenericFormatFactory<T> implements FormatManagerFactory
{

	/**
	 * The ObjectDatabase used by GenericFormatManager objects built by this
	 * GenericFormatFactory.
	 */
	private final ObjectDatabase database;

	/**
	 * The class of objects referred to by this GenericFormatFactory.
	 */
	private final Class<T> underlyingClass;

	/**
	 * The identifier of the type of objects referred to by this
	 * GenericFormatFactory.
	 */
	private final String identifier;

	/**
	 * Constructs a new GenericFormatFactory with the given ObjectDatabase to be
	 * used by GenericFormatManager objects built by this GenericFormatFactory.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used by GenericFormatManager objects built
	 *            by this GenericFormatFactory
	 * @param underlyingClass
	 *            The Class of object which will the underlying class of the
	 *            GenericFormatManager objects produced by this
	 *            GenericFormatFactory
	 * @param identifier
	 *            The identifier of the class of objects referred to by the
	 *            GenericFormatManager objects produced by this
	 *            GenericFormatFactory
	 */
	public GenericFormatFactory(ObjectDatabase objDatabase,
		Class<T> underlyingClass, String identifier)
	{
		this.database = Objects.requireNonNull(objDatabase);
		this.underlyingClass = Objects.requireNonNull(underlyingClass);
		this.identifier = Objects.requireNonNull(identifier);
	}

	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormatName, FormatManagerLibrary library)
	{
		if (subFormatName.isPresent())
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (subformat provided in GenericFormat: "
					+ identifier + ")");
		}
		return new GenericFormatManager<>(database, underlyingClass,
			identifier);
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return identifier;
	}

}
