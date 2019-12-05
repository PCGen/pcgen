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

import java.util.EventObject;
import java.util.Objects;

import pcgen.cdom.base.PCGenIdentifier;

/**
 * An ScopeFacetChangeEvent is an event that indicates when a Facet has changed.
 * <p>
 * The object that implements the ScopeFacetChangeListener interface gets this
 * ScopeFacetChangeEvent when the event occurs.
 * <p>
 * NOTE: This Object is reference-semantic. It carries a reference to the
 * affected CDOMObject. Use of this Event does not provide protection from
 * mutability for that CDOMObject by listeners. ScopeFacetChangeEvent, however,
 * makes the guarantee that no modifications are made by ScopeFacetChangeEvent
 * to the CDOMObject.
 *
 * @param <IDT> The type of Identifier (e.g. CharID) used by this
 *              ScopeFacetChangeEvent
 * @param <S>   The Type of object of the scope of the ScopeFacetChangeEvent
 * @param <T>   The Type object of changed in the ScopeFacetChangeEvent
 */
public class ScopeFacetChangeEvent<IDT extends PCGenIdentifier, S, T> extends EventObject
{
    /**
     * The constant ID used by an ScopeFacetChangeEvent to indicate that a
     * ScopeFacetChangeEvent was the result of a CDOMObject being added to a
     * resource.
     */
    public static final int DATA_ADDED = 0;

    /**
     * The constant ID used by an ScopeFacetChangeEvent to indicate that a
     * ScopeFacetChangeEvent was the result of a CDOMObject being removed from a
     * resource.
     */
    public static final int DATA_REMOVED = 1;

    /**
     * The ID indicating the type of this ScopeFacetChangeEvent (addition to or
     * removal from a resource)
     */
    private final int eventID;

    /**
     * The ID indicating the owning character for this ScopeFacetChangeEvent
     */
    private final IDT charID;

    private final S scope;

    /**
     * The CDOMObject that was added to or removed from the resource.
     */
    private final T node;

    /**
     * Constructs a new ScopeFacetChangeEvent for the given PCGenIdentifier. The
     * CDOMObject which was added or removed and an indication of the action
     * (Addition or Removal) is also provided.
     *
     * @param id     The PCGenIdentifier identifying the resource in which the
     *               event took place
     * @param cdo    The CDOMObject which was added to or removed from the Graph
     * @param source The base event object
     * @param type   An integer identifying whether the given CDOMObject was added
     *               or removed from the resource
     */
    public ScopeFacetChangeEvent(IDT id, S scope, T cdo, Object source, int type)
    {
        super(source);
        Objects.requireNonNull(source, "Source Object cannot be null");
        Objects.requireNonNull(id, "PCGenIdentifier cannot be null");
        Objects.requireNonNull(scope, "Scope cannot be null");
        Objects.requireNonNull(cdo, "CDOMObject cannot be null");
        this.scope = scope;
        charID = id;
        node = cdo;
        eventID = type;
    }

    /**
     * Returns the CDOMObject which was added to or removed from the resource.
     *
     * @return The CDOMObject which was added to or removed from the resource
     */
    public T getCDOMObject()
    {
        return node;
    }

    /**
     * Returns an identifier indicating if the CDOMObject returned by
     * getCDOMObject() was added to or removed from the resource. This
     * identifier is either ScopeFacetChangeEvent.NODE_ADDED or
     * ScopeFacetChangeEvent.NODE_REMOVED
     *
     * @return A identifier indicating if the CDOMObject was added to or removed
     * from the resource
     */
    public int getEventType()
    {
        return eventID;
    }

    /**
     * Returns an identifier indicating the resource on which this event
     * occurred.
     *
     * @return A identifier indicating the resource on which this event
     * occurred.
     */
    public IDT getCharID()
    {
        return charID;
    }

    public S getScope()
    {
        return scope;
    }
}
