/*
 * Copyright (c) Thomas Parker, 2013.
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
 */
package pcgen.cdom.facet.event;

import java.util.EventListener;

import pcgen.cdom.base.PCGenIdentifier;

/**
 * The listener interface for receiving ScopeFacetChangeEvents. When a
 * CDOMObject has been added to or removed from a resource, the respective
 * method in the listener object is invoked, and the ScopeFacetChangeEvent is
 * passed to it.
 * 
 * @param <IDT>
 *            The type of Identifier (e.g. CharID) used by this
 *            ScopeFacetChangeListener
 * @param <S>
 *            The Type of object of the scope of the SubScopeFacetChangeListener
 * @param <T>
 *            The Type of object changed in the events received by a
 *            ScopeFacetChangeListener
 */
public interface ScopeFacetChangeListener<IDT extends PCGenIdentifier, S, T> extends EventListener
{
	/**
	 * Method called when a CDOMObject has been added to a resource and this
	 * ScopeFacetChangeListener has been added as a ScopeFacetChangeListener to
	 * the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The ScopeFacetChangeEvent that occurred.
	 */
    void dataAdded(ScopeFacetChangeEvent<IDT, S, T> dfce);

	/**
	 * Method called when a CDOMObject has been removed from a resource and this
	 * ScopeFacetChangeListener has been added as a ScopeFacetChangeListener to
	 * the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The ScopeFacetChangeEvent that occurred.
	 */
    void dataRemoved(ScopeFacetChangeEvent<IDT, S, T> dfce);
}
