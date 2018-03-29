/*
 * Copyright 2018 (C) Thomas Parker
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
package pcgen.gui2.facade;

import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;

/**
 * Facades is a Utility Class for items in the pcgen.gui2.facade package.
 */
public final class Facades
{

	private Facades()
	{
		//Do not allow construction of a utility class
	}

	/**
	 * Returns a ListFacade that wraps a singleton reference stored in a
	 * DefaultReferenceFacade.
	 * 
	 * @param ref
	 *            The DefaultReferenceFacade to be decorated into a ListFacade
	 * @return The ListFacade that wraps the DefaultReferenceFacade and makes it appear as
	 *         a ListFacade
	 */
	public static <T> ListFacade<T> singletonList(DefaultReferenceFacade<T> ref)
	{
		return new DelegatingSingleton<>(ref);
	}
}
