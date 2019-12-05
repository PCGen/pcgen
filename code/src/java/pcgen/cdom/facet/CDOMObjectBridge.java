/*
 * Copyright (c) Thomas Parker, 2010.
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
package pcgen.cdom.facet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;

/**
 * CDOMObjectBridge is a class that performs the breaking of cycles in the
 * connection of Facets that form the CDOM core of PCGen. In particular, there
 * are events that add objects (such as Templates) and as those objects are
 * added, they can themselves add objects which result in the addition of
 * objects of the same type. In order to deal with this (eventually)
 * self-referencing cycle, CDOMObjectBridge acts as the underlying storage for
 * two different Facets: CDOMObjectConsolidationFacet and CDOMObjectSourceFacet.
 * <p>
 * Note that listening to CDOMObjectConsolidationFacet is the preferred method
 * of listening to addition of (all) CDOMObjects, where possible.
 */
public final class CDOMObjectBridge extends AbstractSourcedListFacet<CharID, CDOMObject>
{
}
