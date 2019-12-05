/*
 * Copyright (c) Thomas Parker, 2018.
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
package pcgen.facade.util;

/**
 * This facade holds a reference to a list of objects but with the objects that it holds a
 * reference to can be changed
 * <p>
 * When the reference changes a ListEvent should be fired to all registered listeners.
 */
public interface WriteableListFacade<E> extends ListFacade<E>
{

    /**
     * Add an element to this ListFacade.
     *
     * @param element The element to be added to the List
     */
    void addElement(E element);

    /**
     * Add an element to this ListFacade at the given index.
     *
     * @param element The element to be added to the List at the given index
     * @param index   The index at which the element should be added
     */
    void addElement(int index, E element);

    /**
     * Remove an element from the ListFacade.
     *
     * @param element The element to be removed
     * @return true if the element was present and removed; false if not present and thus
     * not removed
     */
    boolean removeElement(E element);

    /**
     * Removes the element at the given index from the ListFacade.
     *
     * @param index The index of the element to be removed
     */
    void removeElement(int index);

}
