/*
 * Copyright (c) Thomas Parker, 2012.
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

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.core.spell.Spell;

/**
 * KnownSpellFacet is a Facet that tracks the Known Spells (and target objects)
 * that are contained in a Player Character. These are post-resolution of spells
 * for which the PC is qualified.
 */
public class KnownSpellFacet extends AbstractSubScopeFacet<CDOMList<Spell>, Integer, Spell>
{
}
