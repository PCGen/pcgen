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

import java.util.Objects;

/**
 * A StagingInfo is a container for the read and write Proxy objects and the related
 * Staging object for information to be staged for later use.
 * 
 * @param <R>
 *            The Interface (Class) of Object which is the read interface supported by
 *            this StagingInfo.
 * @param <W>
 *            The Interface (Class) of Object which is the write interface supported by
 *            this StagingInfo.
 */
public class StagingInfo<R, W>
{

	/**
	 * The Proxy for the read interface supported by this StagingInfo.
	 */
	private final R readProxy;

	/**
	 * The Proxy for the write interface supported by this StagingInfo.
	 */
	private final W writeProxy;

	/**
	 * The Staging object supported by this StagingInfo.
	 */
	private final Staging<W> stagingObject;

	/**
	 * Constructs a new StagingInfo with the given read Proxy, write Proxy, and Staging
	 * object.
	 * 
	 * @param readProxy
	 *            The Proxy for the read interface supported by this StagingInfo
	 * @param writeProxy
	 *            The Proxy for the write interface supported by this StagingInfo
	 * @param stagingObject
	 *            The Staging object supported by this StagingInfo
	 */
	public StagingInfo(R readProxy, W writeProxy, Staging<W> stagingObject)
	{
		this.readProxy = Objects.requireNonNull(readProxy);
		this.writeProxy = Objects.requireNonNull(writeProxy);
		this.stagingObject = Objects.requireNonNull(stagingObject);
	}

	/**
	 * Returns the Proxy for the read interface supported by this StagingInfo.
	 * 
	 * @return The Proxy for the read interface supported by this StagingInfo
	 */
	public R getReadProxy()
	{
		return readProxy;
	}

	/**
	 * Returns the Proxy for the write interface supported by this StagingInfo.
	 * 
	 * @return The Proxy for the write interface supported by this StagingInfo
	 */
	public W getWriteProxy()
	{
		return writeProxy;
	}

	/**
	 * Returns the Staging object supported by this StagingInfo.
	 * 
	 * @return The Staging object supported by this StagingInfo
	 */
	public Staging<W> getStagingObject()
	{
		return stagingObject;
	}
}
