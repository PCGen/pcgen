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

import java.util.Optional;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.util.FormatManager;

/**
 * An ArrayFormatFactory builds a FormatManager supporting Arrays from the name
 * of the format of the component of the Array
 */
public class ArrayFormatFactory implements FormatManagerFactory
{
	/**
	 * The list separator character used to parse instructions and separate list items
	 * that will be part of an array built by a FormatManager produced by this
	 * ArrayFormatFactory.
	 */
	private final char listSep;

	/**
	 * The list separator character used to parse instructions and separate groups of
	 * lists that will be part of an array built by a FormatManager produced by this
	 * ArrayFormatFactory.
	 */
	private final char groupSep;

	/**
	 * Constructs a new ArrayFormatFactory with the given separator characters.
	 * 
	 * @param groupSep
	 *            The group separator character used to parse instructions and separate
	 *            groups of lists that will be part of the array in an ArrayFormatManager
	 *            produced by this factory.
	 * @param listSep
	 *            The separator character used to parse lists in the instructions and
	 *            separate list items that will be part of the array processed by an
	 *            ArrayFormatManager produced by this factory.
	 */
	public ArrayFormatFactory(char groupSep, char listSep)
	{
		this.groupSep = groupSep;
		this.listSep = listSep;
	}
	
	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormatName, FormatManagerLibrary library)
	{
		if (subFormatName.isEmpty())
		{
			throw new IllegalArgumentException("Poorly formatted instructions "
				+ "(subformat not provided in ArrayFormatFactory)");
		}
		if (parentFormat.isPresent())
		{
			/*
			 * This is currently prohibited because - among other things -
			 * ArrayFormatManager has no way to convert a multi-dimensional
			 * array
			 */
			throw new IllegalArgumentException(
				"Array format not supported inside another format: "
					+ parentFormat.get() + " may not contain an array");
		}
		return new ArrayFormatManager<>(
			library.getFormatManager(Optional.of(getBuilderBaseFormat()),
				subFormatName.get()),
			groupSep, listSep);
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "ARRAY";
	}

}
