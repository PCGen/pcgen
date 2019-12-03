/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.Loadable;

/**
 * A TransparentReference is a Reference that can be resolved using a
 * ReferenceManufacturer.
 * 
 * TransparentReference is designed to be used in situations where a reference
 * to a specific object is required, but the appropriate ReferenceManufacturer
 * has not yet been constructed.
 * 
 * Implementation Note: In a particular example of where TransparentReference is
 * required, a ReferenceManufacturer is typically built during the load of a
 * Campaign (PCC and LST files). Since there are global tokens work in a Game
 * Mode (which is loaded only once at program launch, not when individual
 * Campaigns are loaded), the references in the Game Mode must be reusable for
 * every collection of Campaigns loaded under that Game Mode. By creating
 * TransparentReferences when the Game Mode is loaded, those
 * TransparentReferences can be resolved using the ReferenceManufacturer for the
 * Campaigns, and the TransparentReferences can be reused as the set of
 * Campaigns is changed during runs of PCGen.
 * 
 * It is intended that this interface is used on an object that extends
 * CDOMReference.
 * 
 * @param <T>
 *            The Class of object this TransparentReference references
 */
@FunctionalInterface
public interface TransparentReference<T extends Loadable>
{
	/**
	 * Resolves this TransparentReference using the given ReferenceManufacturer.
	 * The underlying CDOMReference for this TransparentReference will be set to
	 * the appropriate CDOMReference constructed by the given
	 * ReferenceManufacturer.
	 * 
	 * This method may be called more than once; each time it is called it will
	 * overwrite the existing underlying reference to which this
	 * TransparentReference delegates its behavior.
	 * 
	 * @throws IllegalArgumentException
	 *             if the Reference Class of the given ReferenceManufacturer is
	 *             different than the Reference Class of this
	 *             TransparentReference
	 */
    void resolve(ReferenceManufacturer<T> rm);
}
