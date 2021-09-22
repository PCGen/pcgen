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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import pcgen.base.format.DispatchingFormatManager;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.Converter;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.NamedIndirect;
import pcgen.base.util.Tuple;
import pcgen.base.util.ValueStore;

/**
 * CompoundFormatManager is a FormatManager that handles the Compound format. A Compound
 * has both a primary value as well as any number of secondary values. Secondary values
 * can be optional or required.
 * 
 * @param <T>
 *            The format of the primary object contained by the Compound objects produced
 *            by this CompoundFormatManager.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class CompoundFormatManager<T> implements DispatchingFormatManager<Compound>
{

	/**
	 * Contains the underlying FormatManager for the primary value of the Compound object.
	 */
	private final FormatManager<T> formatManager;

	/**
	 * Contains the definitions of the secondary values for the Compound objects managed
	 * by this CompoundFormatManager.
	 */
	private final CaseInsensitiveMap<SecondaryDefinition> secondaryDefs =
			new CaseInsensitiveMap<SecondaryDefinition>();

	/**
	 * Contains the separator character used to separate the primary object from the
	 * secondary values and the secondary values from each other, in the String
	 * representation of Compound objects managed by this CompoundFormatManager.
	 */
	private final char separator;

	/**
	 * Constructs a new CompoundFormatManager with the given FormatManager and separator.
	 * 
	 * @param fmtManager
	 *            The FormatManager used to handle the primary value of Compound objects
	 *            managed by this CompoundFormatManager
	 * @param separator
	 *            The separator character used to separate the primary object from the
	 *            secondary values and the secondary values from each other, in the String
	 *            representation of Compound objects managed by this
	 *            CompoundFormatManager.
	 */
	public CompoundFormatManager(FormatManager<T> fmtManager, char separator)
	{
		formatManager = Objects.requireNonNull(fmtManager);
		this.separator = separator;
	}

	/**
	 * Adds a new secondary definition to this CompoundFormatManager. These secondary
	 * definitions define the legal secondary values on the Compound objects managed by
	 * this CompoundFormatManager.
	 * 
	 * @param subManager
	 *            The FormatManager managing the Format (class) of the secondary value to
	 *            be added
	 * @param varName
	 *            The name of the secondary value to be added
	 * @param required
	 *            Indicates whether the secondary value is required
	 */
	public void addSecondary(FormatManager<?> subManager, String varName,
		boolean required)
	{
		secondaryDefs.put(varName,
			new SecondaryDefinition(subManager, varName, required));
	}

	/**
	 * Adds a new secondary definition to this CompoundFormatManager. These secondary
	 * definitions define the legal secondary values on the Compound objects managed by
	 * this CompoundFormatManager.
	 * 
	 * @param definition
	 *            The secondary definition to be added to this CompoundFormatManager
	 */
	public void addSecondary(SecondaryDefinition definition)
	{
		secondaryDefs.put(definition.getName(), definition);
	}

	@Override
	public Compound convert(String inputStr)
	{
		String[] parts = splitCompound(inputStr);
		DirectCompound c = new DirectCompound(parts[0], this);
		compileSecondaryItems(parts).forEach(c::addSecondary);
		return c;
	}

	@Override
	public Indirect<Compound> convertIndirect(String inputStr)
	{
		String[] parts = splitCompound(inputStr);
		IndirectCompound c = new IndirectCompound(parts[0], this);
		compileSecondaryItems(parts).forEach(c::addSecondary);
		return c;
	}

	private String[] splitCompound(String inputStr)
	{
		if ((inputStr == null) || inputStr.isEmpty())
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (empty): " + inputStr);
		}
		if (!StringUtil.hasValidSeparators(inputStr, separator))
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (bad separator location): " + inputStr);
		}
		return StringUtil.split(inputStr, separator);
	}

	private List<NamedIndirect<?>> compileSecondaryItems(String[] parts)
	{
		List<SecondaryDefinition> found = new ArrayList<>();
		List<NamedIndirect<?>> assocs = new ArrayList<>();
		for (int i = 1; i < parts.length; i++)
		{
			Tuple<SecondaryDefinition, NamedIndirect<?>> valueOf =
					CompoundFormatManager.getSecondary(secondaryDefs::get, parts[i]);
			found.add(valueOf.getFirst());
			assocs.add(valueOf.getSecond());
		}
		for (SecondaryDefinition cc : secondaryDefs.values())
		{
			if (cc.isRequired() && !found.contains(cc))
			{
				throw new IllegalArgumentException(
					"Unable to construct Compound because " + cc.getName()
						+ " is a required secondary value, but was not provided");
			}
		}
		return assocs;
	}

	@Override
	public String unconvert(Compound c)
	{
		if (!c.isCompatible(this))
		{
			throw new IllegalArgumentException(
				"Attempt to unconvert an incompatible Compound");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(c.getPrimaryUnconverted());
		for (String name : c.getSecondaryNames())
		{
			sb.append('|');
			sb.append(c.getSecondary(name).toString());
		}
		return sb.toString();
	}

	@Override
	public Tuple<String, String> unconvertSeparated(Compound c)
	{
		if (!c.isCompatible(this))
		{
			throw new IllegalArgumentException(
				"Attempt to unconvert an incompatible Compound");
		}
		String secondaryFormats =
				c.getSecondaryNames().stream()
									 .map(c::getSecondary)
									 .map(Object::toString)
									 .collect(StringUtil.joining(separator));
		return new Tuple<>(c.getPrimaryUnconverted(), separator + secondaryFormats);
	}

	@Override
	public Class<Compound> getManagedClass()
	{
		return Compound.class;
	}

	@Override
	public String getIdentifierType()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("COMPOUND[");
		sb.append(formatManager.getIdentifierType());
		for (SecondaryDefinition component : secondaryDefs.values())
		{
			sb.append(',');
			sb.append(component.toString());
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.of(formatManager);
	}

	@Override
	public int hashCode()
	{
		return formatManager.hashCode() ^ secondaryDefs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CompoundFormatManager)
		{
			CompoundFormatManager<?> other = (CompoundFormatManager<?>) o;
			return formatManager.equals(other.formatManager)
				&& secondaryDefs.equals(other.secondaryDefs);
		}
		return false;
	}

	@Override
	public boolean isDirect()
	{
		return formatManager.isDirect() && secondaryDefs.values().stream()
			.allMatch(cad -> cad.getFormatManager().isDirect());
	}

	@Override
	public <OUT> OUT convertViaDispatch(
		Function<FormatManager<Compound>, Converter<OUT>> myProcessor, String inputStr)
	{
		String[] parts = splitCompound(inputStr);
		List<NamedIndirect<?>> assocs = compileSecondaryItems(parts);
		FormatManager<Compound> fm = new PreparedCompoundManager(assocs);
		return myProcessor.apply(fm).convert(parts[0]);
	}

	/**
	 * Returns a Tuple of the SecondaryDefinition and the NamedIndirect based on the
	 * provided Function and String.
	 * 
	 * @param lookupDef
	 *            The Function used to look up the SecondaryDefinition based on its name
	 * @param instructions
	 *            The String representation of the NamedIndirect (which has the
	 *            SecondaryDefinition name embedded within it)
	 * @return A Tuple of the SecondaryDefinition and the NamedIndirect based on the
	 *         provided Function and String
	 */
	private static Tuple<SecondaryDefinition, NamedIndirect<?>> getSecondary(
		Function<String, SecondaryDefinition> lookupDef, String instructions)
	{
		int equalLoc = instructions.indexOf('=');
		String secondaryName = instructions.substring(0, equalLoc);
		SecondaryDefinition definition = lookupDef.apply(secondaryName);
		if (definition == null)
		{
			throw new IllegalArgumentException("Unable to construct Compound because "
				+ secondaryName + " is not a recognized secondary value name");
		}
		String assocInstructions = instructions.substring(equalLoc + 1);
		NamedIndirect<?> assoc = new NamedIndirect<>(secondaryName,
			definition.getFormatManager(), assocInstructions);
		return new Tuple<>(definition, assoc);
	}

	/**
	 * A PreparedCompoundManager is a derivative of the parent CompoundFormatManager that
	 * is designed to apply a series of secondary values to any Compound that is built.
	 * 
	 * Note: This is private to CompoundFormatManager as a form of validation for the
	 * resulting Compound objects. Currently, the validation of required and optional
	 * secondary values is enforced in one place (as the instruction String is parsed into
	 * the secondary values). If this is moved to a separate non-embedded class, then
	 * there is a risk that the items passed to the constructor of this class could not be
	 * valid.
	 */
	private final class PreparedCompoundManager implements FormatManager<Compound>
	{
		private final List<NamedIndirect<?>> assocs;

		private PreparedCompoundManager(List<NamedIndirect<?>> assocs)
		{
			this.assocs = Objects.requireNonNull(assocs);
		}

		@Override
		public Compound convert(String inputStr)
		{
			Compound c = new DirectCompound(inputStr, CompoundFormatManager.this);
			assocs.stream().forEach(c::addSecondary);
			return c;
		}

		@Override
		public Indirect<Compound> convertIndirect(String inputStr)
		{
			IndirectCompound c =
					new IndirectCompound(inputStr, CompoundFormatManager.this);
			assocs.stream().forEach(c::addSecondary);
			return c;
		}

		@Override
		public boolean isDirect()
		{
			return CompoundFormatManager.this.formatManager.isDirect();
		}

		@Override
		public String unconvert(Compound obj)
		{
			/*
			 * This is unsupported because one can't really unconvert with assumptions.
			 * That would imply this would have to capture the secondary values or some
			 * such...
			 */
			throw new UnsupportedOperationException(
				"unconvert not supported on PreparedCompoundManager");
		}

		@Override
		public Class<Compound> getManagedClass()
		{
			return CompoundFormatManager.this.getManagedClass();
		}

		@Override
		public String getIdentifierType()
		{
			/*
			 * This is unsupported because the actual identifier type is not the same as
			 * the original. It is really undefined because this attaches things
			 * automatically
			 */
			throw new UnsupportedOperationException(
				"unconvert not supported on PreparedCompoundManager");
		}

		@Override
		public Optional<FormatManager<?>> getComponentManager()
		{
			return CompoundFormatManager.this.getComponentManager();
		}

		@Override
		public int hashCode()
		{
			return 31 * getOuterType().hashCode() + assocs.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof CompoundFormatManager.PreparedCompoundManager)
			{
				CompoundFormatManager<?>.PreparedCompoundManager other =
						(CompoundFormatManager<?>.PreparedCompoundManager) obj;
				return (getOuterType().equals(other.getOuterType()))
					&& assocs.equals(other.assocs);
			}
			return false;
		}

		private CompoundFormatManager<?> getOuterType()
		{
			return CompoundFormatManager.this;
		}
	}

	@Override
	public Compound initializeFrom(ValueStore valueStore)
	{
		T primary = formatManager.initializeFrom(valueStore);
		Compound c = new DirectCompound(primary, this);
		secondaryDefs.values()
			.stream()
			.filter(SecondaryDefinition::isRequired)
			.map(sd -> processSecondary(valueStore, sd.getName(), sd.getFormatManager()))
			.forEach(c::addSecondary);
		return c;
	}

	private <S> NamedIndirect<S> processSecondary(ValueStore valueStore,
		String name, FormatManager<S> secondaryManager)
	{
		S secondaryValue = secondaryManager.initializeFrom(valueStore);
		return new NamedIndirect<>(name, secondaryValue, secondaryManager);
	}
}
