/**
 * pcgen.core.term.TermEvaulatorException.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 06-Aug-2008 06:09:49
 *
 * Current Ver: $Revision:$
 *
 */

package pcgen.core.term;

import pcgen.exception.PcgenException;

public class TermEvaulatorException extends PcgenException{
	/**
	 * Creates a new instance of {@code TermEvaulatorException} without detail message.
	 */
	public TermEvaulatorException()
	{
		super();
	}

	/**
	 * Constructs an instance of {@code TermEvaulatorException} with the specified detail message.
	 * @param msg the detail message.
	 */
	public TermEvaulatorException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs an instance of {@code TermEvaulatorException}
	 *  with the specified {@link Throwable rootCause}
	 * and the specified detail message.
	 * @param rootCause the root cause of the exception.
	 * @param msg the detail message.
	 */
	public TermEvaulatorException(Throwable rootCause, String msg)
	{
		super(rootCause, msg);
	}
}
