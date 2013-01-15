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
 * The listener interface for receiving ScopeFacetChangeEvents. When a
 * CDOMObject has been added to or removed from a PlayerCharacter, the
 * respective method in the listener object is invoked, and the
 * ScopeFacetChangeEvent is passed to it.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public interface ScopeFacetChangeListener<S, T> extends EventListener
{
	/**
	 * Method called when a CDOMObject has been added to a PlayerCharacter and
	 * this ScopeFacetChangeListener has been added as a
	 * ScopeFacetChangeListener to the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The ScopeFacetChangeEvent that occurred.
	 */
	public void dataAdded(ScopeFacetChangeEvent<S, T> dfce);

	/**
	 * Method called when a CDOMObject has been removed from a PlayerCharacter
	 * and this ScopeFacetChangeListener has been added as a
	 * ScopeFacetChangeListener to the source ScopeFacet.
	 * 
	 * @param dfce
	 *            The ScopeFacetChangeEvent that occurred.
	 */
	public void dataRemoved(ScopeFacetChangeEvent<S, T> dfce);
}