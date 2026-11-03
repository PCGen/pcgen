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
package pcgen.base.format.compound;

import java.util.Objects;

import pcgen.base.util.FormatManager;

/**
 * DirectCompound is an AbstractCompound for direct types (Number.class)
 */
public class DirectCompound extends AbstractCompound
{

	/**
	 * The underlying primary object of this DirectCompound.
	 */
	private final Object object;

	/**
	 * Constructs a new DirectCompound with the given primary object and
	 * FormatManager.
	 * 
	 * @param primary
	 *            The primary object for this DirectCompound
	 * @param fmtManager
	 *            The FormatManager for this DirectCompound
	 */
	public DirectCompound(Object primary, FormatManager<Compound> fmtManager)
	{
		super(fmtManager);
		object = Objects.requireNonNull(primary);
		Objects.requireNonNull(getPrimaryUnconverted());
	}

	/**
	 * Constructs a new DirectCompound from the given instructions and with the
	 * given FormatManager.
	 * 
	 * @param primaryInstructions
	 *            the instructions for the primary object of this DirectCompound
	 * @param fmtManager
	 *            The FormatManager to be used for this DirectCompound
	 */
	public DirectCompound(String primaryInstructions,
		FormatManager<Compound> fmtManager)
	{
		super(fmtManager);
		object = fmtManager.getComponentManager().get()
			.convert(Objects.requireNonNull(primaryInstructions));
	}

	@Override
	public final String getPrimaryUnconverted()
	{
		return unconvert(getFormatManager().getComponentManager().get());
	}

	/**
	 * Converts the primary object of this DirectCompount into the persistent
	 * (String) format.
	 * 
	 * @param componentManager
	 *            The FormatManager to be used for the conversion
	 * @return The persistent (String) format of the primary object of this
	 *         DirectCompound
	 */
	@SuppressWarnings("unchecked")
	private <A> String unconvert(FormatManager<A> componentManager)
	{
		return componentManager.unconvert((A) object);
	}

	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + object.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj) && (obj instanceof DirectCompound)
			&& object.equals(((DirectCompound) obj).object);
	}

	@Override
	public Object getPrimary()
	{
		return object;
	}
}
