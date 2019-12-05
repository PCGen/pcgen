/*
 * Copyright 2010 (C) Thomas Parker
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NatureTest
{
    @Test
    public void testGetBest()
    {
        assertNull(Nature.getBestNature(null, null));
        assertEquals(Nature.AUTOMATIC, Nature.getBestNature(null, Nature.AUTOMATIC));
        assertEquals(Nature.AUTOMATIC, Nature.getBestNature(Nature.AUTOMATIC, null));
        assertEquals(Nature.VIRTUAL, Nature.getBestNature(null, Nature.VIRTUAL));
        assertEquals(Nature.VIRTUAL, Nature.getBestNature(Nature.VIRTUAL, null));
        assertEquals(Nature.NORMAL, Nature.getBestNature(null, Nature.NORMAL));
        assertEquals(Nature.NORMAL, Nature.getBestNature(Nature.NORMAL, null));
        assertEquals(Nature.NORMAL, Nature.getBestNature(Nature.AUTOMATIC, Nature.NORMAL));
        assertEquals(Nature.NORMAL, Nature.getBestNature(Nature.NORMAL, Nature.AUTOMATIC));
        assertEquals(Nature.NORMAL, Nature.getBestNature(Nature.VIRTUAL, Nature.NORMAL));
        assertEquals(Nature.NORMAL, Nature.getBestNature(Nature.NORMAL, Nature.VIRTUAL));
        assertEquals(Nature.VIRTUAL, Nature.getBestNature(Nature.VIRTUAL, Nature.AUTOMATIC));
        assertEquals(Nature.VIRTUAL, Nature.getBestNature(Nature.AUTOMATIC, Nature.VIRTUAL));
    }


}
