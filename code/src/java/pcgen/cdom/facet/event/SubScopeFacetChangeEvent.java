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

import pcgen.cdom.enumeration.CharID;

/**
 * An SubScopeFacetChangeEvent is an event that indicates when a Facet has
 * changed.
 * <p>
 * The object that implements the SubScopeFacetChangeListener interface gets
 * this SubScopeFacetChangeEvent when the event occurs.
 * <p>
 * NOTE: This Object is reference-semantic. It carries a reference to the
 * affected CDOMObject. Use of this Event does not provide protection from
 * mutability for that CDOMObject by listeners. SubScopeFacetChangeEvent,
 * however, makes the guarantee that no modifications are made by
 * SubScopeFacetChangeEvent to the CDOMObject.
 *
 * @param <S1> The Type of object of the first scope of the
 *             SubScopeFacetChangeEvent
 * @param <S2> The Type of object of the second scope of the
 *             SubScopeFacetChangeEvent
 * @param <T>  The Type of object changed in the SubScopeFacetChangeEvent
 */
public class SubScopeFacetChangeEvent<S1, S2, T> extends EventObject
{
    /**
     * The constant ID used by an SubScopeFacetChangeEvent to indicate that a
     * SubScopeFacetChangeEvent was the result of a CDOMObject being added to a
     * PlayerCharacter.
     */
    public static final int DATA_ADDED = 0;

    /**
     * The constant ID used by an SubScopeFacetChangeEvent to indicate that a
     * SubScopeFacetChangeEvent was the result of a CDOMObject being removed
     * from a PlayerCharacter.
     */
    public static final int DATA_REMOVED = 1;

    /**
     * The ID indicating the type of this SubScopeFacetChangeEvent (addition to
     * or removal from a PlayerCharacter)
     */
    private final int eventID;

    /**
     * The ID indicating the owning character for this SubScopeFacetChangeEvent
     */
    private final CharID charID;

    private final S1 scope1;
    private final S2 scope2;

    /**
     * The CDOMObject that was added to or removed from the PlayerCharacter.
     */
    private final T node;

    /**
     * Constructs a new SubScopeFacetChangeEvent for the given CharID. The
     * CDOMObject which was added or removed and an indication of the action
     * (Addition or Removal) is also provided.
     *
     * @param id     The CharID identifying the PlayerCharacter in which the event
     *               took place
     * @param cdo    The CDOMObject which was added to or removed from the Graph
     * @param source The base event object
     * @param type   An integer identifying whether the given CDOMObject was added
     *               or removed from the PlayerCharacter
     */
    public SubScopeFacetChangeEvent(CharID id, S1 scope1, S2 scope2, T cdo, Object source, int type)
    {
        super(source);
        Objects.requireNonNull(source, "Source Object cannot be null");
        Objects.requireNonNull(id, "CharID cannot be null");
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Objects.requireNonNull(cdo, "CDOMObject cannot be null");
        this.scope1 = scope1;
        this.scope2 = scope2;
        charID = id;
        node = cdo;
        eventID = type;
    }

    /**
     * Returns the CDOMObject which was added to or removed from the
     * PlayerCharacter.
     *
     * @return The CDOMObject which was added to or removed from the
     * PlayerCharacter
     */
    public T getCDOMObject()
    {
        return node;
    }

    /**
     * Returns an identifier indicating if the CDOMObject returned by
     * getCDOMObject() was added to or removed from the PlayerCharacter. This
     * identifier is either SubScopeFacetChangeEvent.NODE_ADDED or
     * SubScopeFacetChangeEvent.NODE_REMOVED
     *
     * @return A identifier indicating if the CDOMObject was added to or removed
     * from the PlayerCharacter
     */
    public int getEventType()
    {
        return eventID;
    }

    /**
     * Returns an identifier indicating the PlayerCharacter on which this event
     * occurred.
     *
     * @return A identifier indicating the PlayerCharacter on which this event
     * occurred.
     */
    public CharID getCharID()
    {
        return charID;
    }

    public S1 getScope1()
    {
        return scope1;
    }

    public S2 getScope2()
    {
        return scope2;
    }
}
