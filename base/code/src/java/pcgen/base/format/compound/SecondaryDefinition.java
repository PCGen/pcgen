/*
 * Copyright 2016-7 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format.compound;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formatmanager.FormatManagerLibrary;
import pcgen.base.util.FormatManager;

/**
 * A SecondaryDefinition is a definition of a legal secondary value for a Compound. Thus,
 * this indicates when an secondary value can be used on a Compound.
 */
public class SecondaryDefinition
{

	/**
	 * The FormatManager of the object contained in a secondary value defined by this
	 * SecondaryDefinition.
	 */
	private final FormatManager<?> formatManager;

	/**
	 * The name of the secondary value defined by this SecondaryDefinition.
	 */
	private final String valueName;

	/**
	 * Indicates whether the secondary value defined by this SecondaryDefinition is
	 * required when this SecondaryDefinition exists on a Compound.
	 */
	private final boolean isDefRequired;

	/**
	 * Constructs a new SecondaryDefinition with the given FormatManager for managing the
	 * Format (class) of the secondary value, the given String name of the secondary
	 * value, and the flag indicating if the secondary value is required.
	 * 
	 * @param subManager
	 *            The FormatManager for managing the Format (class) of the secondary value
	 *            defined by this SecondaryDefinition
	 * @param name
	 *            The name of the secondary value defined by this SecondaryDefinition
	 * @param required
	 *            Indicates whether the secondary value defined by this
	 *            SecondaryDefinition is required when this SecondaryDefinition exists on
	 *            a Compound
	 */
	public SecondaryDefinition(FormatManager<?> subManager, String name, boolean required)
	{
		formatManager = Objects.requireNonNull(subManager);
		valueName = Objects.requireNonNull(name);
		isDefRequired = required;
	}

	/**
	 * Returns the FormatManager for the Format (class) of the secondary value defined by
	 * this SecondaryDefinition.
	 * 
	 * @return The FormatManager for the Format (class) of the secondary value defined by
	 *         this SecondaryDefinition
	 */
	public FormatManager<?> getFormatManager()
	{
		return formatManager;
	}

	/**
	 * Returns the name of the secondary value defined by this SecondaryDefinition.
	 * 
	 * @return The name of the secondary value defined by this SecondaryDefinition
	 */
	public String getName()
	{
		return valueName;
	}

	/**
	 * Returns true if the secondary value defined by this SecondaryDefinition is required
	 * when this SecondaryDefinition exists on a Compound.
	 * 
	 * @return true if the secondary value defined by this SecondaryDefinition is required
	 *         when this SecondaryDefinition exists on a Compound; false otherwise
	 */
	public boolean isRequired()
	{
		return isDefRequired;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getFormatManager().getIdentifierType());
		if (!isRequired())
		{
			sb.append('?');
		}
		sb.append('=');
		sb.append(getName());
		return sb.toString();
	}

	/**
	 * Returns a new SecondaryDefinition built from the given FormatManagerLibrary and
	 * instructions.
	 * 
	 * @param library
	 *            The FormatManagerLibrary from which the format provided in the
	 *            instructions will be retrieved
	 * @param instructions
	 *            The instructions to build this SecondaryDefinition
	 * @return A new SecondaryDefinition built from the given FormatManagerLibrary and
	 *         instructions
	 */
	public static SecondaryDefinition valueOf(FormatManagerLibrary library,
		String instructions)
	{
		int equalLoc = instructions.indexOf('=');
		if (equalLoc == -1)
		{
			throw new IllegalArgumentException(
				"Format must be FORMAT=Name, found: " + instructions);
		}
		if (equalLoc != instructions.lastIndexOf('='))
		{
			throw new IllegalArgumentException(
				"Format must be FORMAT=Name, but found two = :" + instructions);
		}
		if (equalLoc == 0)
		{
			throw new IllegalArgumentException(
				"Format must be FORMAT=Name, but started with = :" + instructions);
		}
		String formatID;
		boolean required;
		if (instructions.charAt(equalLoc - 1) == '?')
		{
			required = false;
			formatID = instructions.substring(0, equalLoc - 1);
		}
		else
		{
			required = true;
			formatID = instructions.substring(0, equalLoc);
		}
		String name = instructions.substring(equalLoc + 1);
		if (name.isEmpty())
		{
			throw new IllegalArgumentException(
				"Format must be FORMAT=Name, but ended with = :" + instructions);
		}
		FormatManager<?> formatManager =
				library.getFormatManager(Optional.of("COMPOUND"), formatID);
		return new SecondaryDefinition(formatManager, name, required);
	}
}
