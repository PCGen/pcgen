/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;
import java.util.Collections;

/**
 * PassResult contains a shortcut ComplexResult response for a successful set of
 * processing (returns TRUE with no messages)
 */
public class PassResult
{
	private PassResult()
	{
		//Don't construct utility class
	}

	/**
	 * Object to be returned from operations that succeeded with no messages.
	 */
	public static final OperationSuccessful SUCCESS = new OperationSuccessful();

	/**
	 * Represents successful processing of an operation (return TRUE with no messages).
	 */
	private static final class OperationSuccessful implements ComplexResult<Boolean>
	{
		@Override
		public Boolean get()
		{
			return Boolean.TRUE;
		}

		@Override
		public Collection<String> getMessages()
		{
			//No messages because we passed
			return Collections.emptyList();
		}
	}
}
