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
 * A DeferredMethodController is an object used to push actions from a Staging into a
 * target object of a given type.
 * 
 * @param <T>
 *            The Format of the relevant Interface that is being used by the CommitTask.
 */
public class DeferredMethodController<T>
{

	/**
	 * The Staging object that contains the information to be pushed into the targetObject
	 * when process() is called.
	 */
	private final Staging<T> staging;

	/**
	 * The target Object to be modified when process() is called.
	 */
	private final T targetObject;

	/**
	 * Constructs a new DeferredMethodController from the given Staging and target Object.
	 * 
	 * @param staging
	 *            The Staging object that contains the information to be pushed into the
	 *            targetObject when process() is called
	 * @param targetObject
	 *            The target Object to be modified when process() is called
	 */
	public DeferredMethodController(Staging<T> staging, T targetObject)
	{
		this.staging = Objects.requireNonNull(staging);
		this.targetObject = Objects.requireNonNull(targetObject);
		Class<T> cl = staging.getInterface();
		if (!cl.isAssignableFrom(targetObject.getClass()))
		{
			throw new IllegalArgumentException(
				"Target must be compatible with the given Staging");
		}
	}

	/**
	 * Performs the actual commitment of information from the Staging to the target
	 * Object.
	 */
	public void run()
	{
		staging.applyTo(targetObject);
	}
}
