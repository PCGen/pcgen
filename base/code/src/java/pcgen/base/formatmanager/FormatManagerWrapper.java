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

import pcgen.base.util.FormatManager;

/**
 * A FormatManagerWrapper is a decorator to convert a FormatManager into a
 * FormatManagerFactory.
 */
public class FormatManagerWrapper implements FormatManagerFactory
{
	/**
	 * The underlying FormatManager.
	 */
	private final FormatManager<?> formatManager;

	/**
	 * Constructs a new FormatManagerWrapper for the given FormatManager.
	 * 
	 * @param fmtManager
	 *            The underlying FormatManager for this FormatManagerWrapper
	 * @throws IllegalArgumentException
	 *             if the given FormatManager has no identifier type or if the
	 *             given FormatManager has no managed class
	 */
	public FormatManagerWrapper(FormatManager<?> fmtManager)
	{
		formatManager = FormatUtilities.ifValid(fmtManager);
	}

	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> formatSub, FormatManagerLibrary library)
	{
		if (formatSub.isEmpty())
		{
			return formatManager;
		}
		throw new IllegalArgumentException("Format: "
			+ formatManager.getIdentifierType()
			+ " may not contain a subformat");
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return formatManager.getIdentifierType();
	}

	@Override
	public int hashCode()
	{
		return formatManager.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof FormatManagerWrapper)
			&& formatManager.equals(((FormatManagerWrapper) obj).formatManager);
	}

}
