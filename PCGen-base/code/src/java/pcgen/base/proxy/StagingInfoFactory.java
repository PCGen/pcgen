/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * A StagingInfoFactory is a factory for StagingInfo objects. This class holds the master
 * list of PropertyProcessor objects which will be available to StagingProxy objects built
 * by this StagingInfoFactory.
 */
public class StagingInfoFactory
{
	/**
	 * The List of PropertyProcessor objects to be provided to the StagingProxy objects
	 * constructed by this StagingInfoFactory.
	 */
	private final List<PropertyProcessor> processors = new ArrayList<>();

	/**
	 * Adds a new PropertyProcessor to the List of PropertyProcessor objects in this
	 * StagingInfoFactory.
	 * 
	 * @param processor
	 *            The PropertyProcessor to be added to the List of PropertyProcessor
	 *            objects in this StagingInfoFactory
	 */
	public void addProcessor(PropertyProcessor processor)
	{
		processors.add(processor);
	}

	/**
	 * Constructs a new StagingProxy for the given read and write interfaces. This returns
	 * a StagingInfo that contains the necessary Proxy objects for the interfaces as well
	 * as a Staging for the write interface.
	 * 
	 * @param readInterface
	 *            The read interface to be used to construct the returned Staging
	 * @param writeInterface
	 *            The write interface to be used to construct both the returned Proxy
	 *            object and the returned Staging
	 * @param underlying
	 *            The underlying object to be used for @ReadOnly methods
	 * @return A StagingInfo containing the necessary Proxy objects for the interfaces as
	 *         well as a Staging for the write interface
	 * @param <R>
	 *            The component type of the read interface of the object to be proxied
	 * @param <W>
	 *            The component type of the write interface of the object to be proxied
	 */
	public <R, W> StagingInfo<R, W> produceStaging(Class<R> readInterface,
		Class<W> writeInterface, R underlying)
	{
		StagingProxy<R, W> factory =
				new StagingProxy<>(processors, readInterface, writeInterface, underlying);
		@SuppressWarnings("unchecked")
		W writeProxy = (W) Proxy.newProxyInstance(
			writeInterface.getClassLoader(), new Class[]{writeInterface}, factory);
		@SuppressWarnings("unchecked")
		R readProxy = (R) Proxy.newProxyInstance(
			readInterface.getClassLoader(), new Class[]{readInterface}, factory);
		return new StagingInfo<>(readProxy, writeProxy, factory);
	}

}
