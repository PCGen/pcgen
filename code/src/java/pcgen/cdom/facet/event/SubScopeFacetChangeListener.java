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

/**
 * The listener interface for receiving SubScopeFacetChangeEvents. When a
 * CDOMObject has been added to or removed from a PlayerCharacter, the
 * respective method in the listener object is invoked, and the
 * SubScopeFacetChangeEvent is passed to it.
 * 
 * @param <S1>
 *            The Type of object of the first scope of the
 *            SubScopeFacetChangeListener
 * @param <S2>
 *            The Type of object of the second scope of the
 *            SubScopeFacetChangeListener
 * @param <T>
 *            The Type of object changed in the events received by a
 *            SubScopeFacetChangeListener
 */
public interface SubScopeFacetChangeListener<S1, S2, T> extends EventListener
{
	/**
	 * Method called when a CDOMObject has been added to a PlayerCharacter and
	 * this SubScopeFacetChangeListener has been added as a
	 * SubScopeFacetChangeListener to the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The SubScopeFacetChangeEvent that occurred.
	 */
    void dataAdded(SubScopeFacetChangeEvent<S1, S2, T> dfce);

	/**
	 * Method called when a CDOMObject has been removed from a PlayerCharacter
	 * and this SubScopeFacetChangeListener has been added as a
	 * SubScopeFacetChangeListener to the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The SubScopeFacetChangeEvent that occurred.
	 */
    void dataRemoved(SubScopeFacetChangeEvent<S1, S2, T> dfce);
}
