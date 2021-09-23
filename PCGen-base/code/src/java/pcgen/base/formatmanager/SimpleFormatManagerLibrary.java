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

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;

/**
 * A SimpleFormatManagerLibrary stores FormatManagers that can be used and can
 * build compound formats by using FormatManagerFactory objects. Formats are
 * stored by their identifier String (e.g. "STRING" for StringManager).
 */
public final class SimpleFormatManagerLibrary implements FormatManagerLibrary
{

	/**
	 * A Map storing the FormatManagerBuilders by (case-insensitive) name
	 */
	private final CaseInsensitiveMap<FormatManagerFactory> builderByIdentifier =
			new CaseInsensitiveMap<>();

	@Override
	public FormatManager<?> getFormatManager(Optional<String> parent,
		String formatName)
	{
		return internalGetFormatManager(parent, formatName);
	}

	private FormatManager<?> internalGetFormatManager(Optional<String> parent, String formatName)
	{
		FormatManagerFactory fmtManagerBuilder = builderByIdentifier.get(formatName);
		if (fmtManagerBuilder != null)
		{
			return fmtManagerBuilder.build(Optional.empty(), Optional.empty(), this);
		}
		int sqBracketLoc = formatName.indexOf('[');
		if (sqBracketLoc == -1)
		{
			//There was no brackets for a subformat, so we fail
			throw new IllegalArgumentException(
				"No FormatManager available for " + formatName);
		}
		int lengthMinusOne = formatName.length() - 1;
		if (formatName.lastIndexOf(']') != lengthMinusOne)
		{
			throw new IllegalArgumentException(
				"Format Name must have matching open and close brackets, found: "
					+ formatName);
		}
		String formatRoot = formatName.substring(0, sqBracketLoc);
		String formatSub =
				formatName.substring(sqBracketLoc + 1, lengthMinusOne);
		fmtManagerBuilder = builderByIdentifier.get(formatRoot);
		if (fmtManagerBuilder == null)
		{
			//Parent format doesn't exist even after removing subformat, so we fail
			throw new IllegalArgumentException("No FormatManager available for "
				+ formatRoot + " (called with subformat " + formatSub + ")");
		}
		return fmtManagerBuilder.build(parent, Optional.of(formatSub), this);
	}

	/**
	 * Adds a FormatManager to the FormatManagerLibrary.
	 * 
	 * @param fmtManager
	 *            The FormatManager to be added to this FormatManagerLibrary
	 * @throws IllegalArgumentException
	 *             if this FormatManagerLibrary already has a
	 *             FormatManagerBuilder with a matching identifier
	 */
	public void addFormatManager(FormatManager<?> fmtManager)
	{
		addFormatManagerBuilder(new FormatManagerWrapper(fmtManager));
	}

	/**
	 * Adds a FormatManagerBuilder to the FormatManagerLibrary.
	 * 
	 * @param builder
	 *            The FormatManagerBuilder to be added to this
	 *            FormatManagerLibrary
	 * @throws IllegalArgumentException
	 *             if this FormatManagerLibrary already has a
	 *             FormatManagerBuilder with a matching identifier
	 */
	public void addFormatManagerBuilder(FormatManagerFactory builder)
	{
		String fmIdent = builder.getBuilderBaseFormat();
		FormatManagerFactory byIdentifier = builderByIdentifier.get(fmIdent);
		if ((byIdentifier != null) && !byIdentifier.equals(builder))
		{
			throw new IllegalArgumentException(
				"Cannot set another Format Manager Builder for " + fmIdent);
		}
		builderByIdentifier.put(fmIdent, builder);
	}

	@Override
	public boolean hasFormatManager(String formatName)
	{
		try
		{
			return internalGetFormatManager(Optional.empty(), formatName) != null;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}
}
