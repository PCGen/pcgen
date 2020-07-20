/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formatmanager;

import pcgen.base.format.compound.SecondaryDefinition;

import java.util.Optional;

import pcgen.base.format.compound.CompoundFormatManager;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.FormatManager;

/**
 * An ArrayFormatFactory builds a FormatManager supporting Arrays from the name of the
 * format of the component of the Array
 */
public class CompoundFormatFactory implements FormatManagerFactory
{

	/**
	 * The separator character used to parse separate the definition formats of secondary
	 * values that will be part of a Compound built by a FormatManager produced by this
	 * CompoundFormatFactory.
	 */
	private final char definitionSeparator;

	/**
	 * The separator character used to parse instructions - meaning it will separate
	 * secondary key=value combinations that will be placed into a Compound built by a
	 * FormatManager produced by this CompoundFormatFactory.
	 */
	private final char applicationSeparator;

	/**
	 * Constructs a new CompoundFormatFactory using the given separator characters.
	 * 
	 * @param definitionSeparator
	 *            The separator character for separating different definitions in format
	 *            instructions.
	 * @param applicationSeparator
	 *            The separator character used to separate the primary object from the
	 *            secondary values and the secondary values from each other, in the String
	 *            representation of Compound objects managed by a CompoundFormatManager
	 *            produced by this factory.
	 */
	public CompoundFormatFactory(char definitionSeparator, char applicationSeparator)
	{
		this.definitionSeparator = definitionSeparator;
		this.applicationSeparator = applicationSeparator;
	}

	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormat, FormatManagerLibrary library)
	{
		if (subFormat.isEmpty())
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (missing subformat in Compound)");
		}
		String subFormatName = subFormat.get();
		if (!StringUtil.hasValidSeparators(subFormatName, definitionSeparator))
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (bad separator location): "
					+ subFormatName);
		}
		String[] instructions = StringUtil.split(subFormatName, definitionSeparator);
		FormatManager<?> primaryFM =
				library.getFormatManager(parentFormat, instructions[0]);
		CompoundFormatManager<?> manager =
				new CompoundFormatManager<>(primaryFM, applicationSeparator);
		for (int i = 1; i < instructions.length; i++)
		{
			manager
				.addSecondary(SecondaryDefinition.valueOf(library, instructions[i]));
		}
		return manager;
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "COMPOUND";
	}
}
