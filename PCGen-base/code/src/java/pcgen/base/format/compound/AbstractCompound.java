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
package pcgen.base.format.compound;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pcgen.base.util.FormatManager;
import pcgen.base.util.NamedIndirect;

/**
 * An AbstractCompound pulls together the common behaviors of Compound objects
 */
abstract class AbstractCompound implements Compound
{

	/**
	 * The FormatManager for this Compound.
	 */
	private final FormatManager<Compound> formatManager;

	/**
	 * The Map (by name) of the secondary values of this Compound.
	 */
	private Map<String, NamedIndirect<?>> components;

	/**
	 * Constructs a new AbstractCompound with the given controlling FormatManager.
	 * 
	 * @param fmtManager
	 *            The FormatManager for this Compound.
	 */
	public AbstractCompound(FormatManager<Compound> fmtManager)
	{
		formatManager = Objects.requireNonNull(fmtManager);
	}

	@Override
	public void addSecondary(NamedIndirect<?> assoc)
	{
		getComponentMap().put(assoc.getName(), assoc);
	}

	/**
	 * Returns the secondary value map for this AbstractCompound. Used to provide lazy
	 * instantiation of the map.
	 * 
	 * @return Returns the secondary value map for this AbstractCompound
	 */
	private Map<String, NamedIndirect<?>> getComponentMap()
	{
		/*
		 * Lazy because if the secondary values are all optional and are not used, we
		 * don't want to waste the memory.
		 */
		if (components == null)
		{
			components =
					new TreeMap<String, NamedIndirect<?>>(String.CASE_INSENSITIVE_ORDER);
		}
		return components;
	}

	@Override
	public boolean isCompatible(FormatManager<?> fm)
	{
		return formatManager.equals(fm);
	}

	@Override
	public Collection<String> getSecondaryNames()
	{
		return components.keySet().stream().map(Object::toString)
			.collect(Collectors.toSet());
	}

	@Override
	public NamedIndirect<?> getSecondary(String assocName)
	{
		return components.get(assocName);
	}

	/**
	 * Returns the FormatManager for this Compound.
	 * 
	 * @return The FormatManager for this Compound
	 */
	protected FormatManager<Compound> getFormatManager()
	{
		return formatManager;
	}

	@Override
	public int hashCode()
	{
		int result = 0;
		if (components != null)
		{
			for (Object o : components.values())
			{
				result = 31 * result + o.hashCode();
			}
		}
		return 31 * result + formatManager.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj instanceof AbstractCompound)
		{
			AbstractCompound other = (AbstractCompound) obj;
			if (!formatManager.equals(other.formatManager))
			{
				return false;
			}
			if (components == null)
			{
				if (other.components != null)
				{
					return false;
				}
			}
			else if (!components.equals(other.components))
			{
				return false;
			}
		}
		return true;
	}
}
