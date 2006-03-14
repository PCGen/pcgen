/*
 * PersistenceLayerException.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence;

import pcgen.exception.PcgenException;

/**
 * Exception thrown by the Persistance Layer
 * 
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PersistenceLayerException extends PcgenException
{
	/**
	 * Creates a new instance of <code>Exception</code> without detail message.
	 */
	public PersistenceLayerException()
	{
		super();
	}

	/**
	 * Constructs an instance of <code>PersistenceLayerException</code> with the specified detail message.
	 * @param msg the detail message.
	 */
	public PersistenceLayerException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs an instance of <code>PersistenceLayerException</code> with the specified {@link Throwable rootCause}
	 * and the specified detail message.
	 * @param rootCause the root cause of the exception.
	 * @param msg the detail message.
	 */
	public PersistenceLayerException(Throwable rootCause, String msg)
	{
		super(rootCause, msg);
	}
}
