/*
 * Copyright (c) Thomas Parker, 2004-06, 2013.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Aug 26, 2004
 */
package pcgen.base.graph.core;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An UnsupportedGraphOperationException is thrown when an attempt is made to
 * perform an operation on a Graph which is not possible. This could include
 * various Graph state problems, depending on the implementation of the Graph.
 * 
 * Note that (consistent with the Collections Framework) an attempt to remove an
 * object which is not present in a Graph should not regularly cause an
 * exception. Rather, the remove* method should return false. Similar rules
 * apply for when an object cannot be added to a Graph.
 * 
 * An UnsupportedGraphOperationException should be limited to circumstances
 * where the Graph truly cannot support the operation and there is not another
 * method for communicating such a state back to the user. Like other
 * exceptions, it should also only occur in situations which are preventable by
 * performing a test (consider how an Iterator can throw NoSuchElementException
 * when the next() method is called, but will only do so for code which fails to
 * properly test the hasNext() method).
 */
public class UnsupportedGraphOperationException extends
		UnsupportedOperationException
{

	/**
	 * Creates a new UnsupportedGraphOperationException with the given message.
	 * 
	 * @param message
	 *            The message indicating the cause of the exception.
	 */
	public UnsupportedGraphOperationException(String message)
	{
		super(message);
	}

}
