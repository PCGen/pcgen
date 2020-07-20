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

import java.util.Objects;

import pcgen.base.format.Dispatched;
import pcgen.base.format.DispatchingFormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.Tuple;

/**
 * An IndirectCompound is a Compound that implements Indirect<Compound>. This is necessary
 * as some Compound objects cannot be produced directly due to race conditions at load.
 */
class IndirectCompound extends AbstractCompound implements Indirect<Compound>, Dispatched
{

	/**
	 * The underlying primary object of this AbstractCompound, stored as an Indirect.
	 */
	private final Indirect<?> object;

	/**
	 * The controlling DispatchingFormatManager for this Compound
	 */
	private final DispatchingFormatManager<Compound> dispatchManager;

	/**
	 * Constructs a new IndirectCompound
	 * 
	 * @param primaryIdentifier
	 *            The identifier of the primary object of this IndirectCompound
	 * @param fmtManager
	 *            The controlling FormatManager for this IndirectCompound
	 */
	public IndirectCompound(String primaryIdentifier,
		DispatchingFormatManager<Compound> fmtManager)
	{
		super(fmtManager);
		dispatchManager = fmtManager;
		object = fmtManager.getComponentManager().get()
			.convertIndirect(Objects.requireNonNull(primaryIdentifier));
	}

	@Override
	public String getPrimaryUnconverted()
	{
		return object.getUnconverted();
	}

	@Override
	public Compound get()
	{
		return this;
	}

	@Override
	public String getUnconverted()
	{
		return getFormatManager().unconvert(this);
	}

	@Override
	public Tuple<String, String> unconvertSeparated()
	{
		return dispatchManager.unconvertSeparated(this);
	}

	@Override
	public Object getPrimary()
	{
		return object.get();
	}
}
