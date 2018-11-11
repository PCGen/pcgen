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

	/**
	 * Gets the FormatManager for the given String identifying a format of
	 * object.
	 * 
	 * @param formatName
	 *            The String identifying the format for which the FormatManager
	 *            should be returned
	 * @return The FormatManager for the given String identifying a format of
	 *         object
	 * @throws NullPointerException
	 *             if the given format does not have an associated FormatManager
	 */
	@Override
	public FormatManager<?> getFormatManager(String formatName)
	{
		FormatManager<?> fm = internalGetFormatManager(formatName);
		return Objects.requireNonNull(fm, "No FormatManager available for " + formatName);
	}

	private FormatManager<?> internalGetFormatManager(String formatName)
	{
		FormatManagerFactory fmtManagerBuilder = builderByIdentifier.get(formatName);
		if (fmtManagerBuilder != null)
		{
			return fmtManagerBuilder.build(null, this);
		}
		String formatSub = null;
		int sqBracketLoc = formatName.indexOf('[');
		if (sqBracketLoc != -1)
		{
			int lengthMinusOne = formatName.length() - 1;
			if (formatName.lastIndexOf(']') != lengthMinusOne)
			{
				throw new IllegalArgumentException(
					"Format Name must have matching open and close brackets, found: "
						+ formatName);
			}
			String formatRoot = formatName.substring(0, sqBracketLoc);
			formatSub = formatName.substring(sqBracketLoc + 1, lengthMinusOne);
			fmtManagerBuilder = builderByIdentifier.get(formatRoot);
		}
		if (fmtManagerBuilder == null)
		{
			return null;
		}
		return fmtManagerBuilder.build(formatSub, this);
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
		return internalGetFormatManager(formatName) != null;
	}
}
