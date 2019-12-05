/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.rules.persistence.TokenLibrary.ModifierIterator;
import pcgen.rules.persistence.token.ModifierFactory;
import plugin.modifier.number.AddModifierFactory;
import plugin.modifier.set.SetModifierFactory;

import org.junit.jupiter.api.Test;

class TokenLibraryTest
{
    @Test
    void testFallUp()
    {
        AddModifierFactory numberAdd = new AddModifierFactory();
        TokenLibrary.addToModifierMap(numberAdd);
        SetModifierFactory<Object> arraySet = new SetModifierFactory<>();
        TokenLibrary.addToModifierMap(arraySet);
        ModifierIterator<Integer, ModifierFactory<? super Integer>> iterator =
                new TokenLibrary.ModifierIterator<>(Integer.class, "ADD");
        assertTrue(iterator.hasNext());
        ModifierFactory<? super Integer> token = iterator.next();
        assertNotNull(token);
        assertEquals(numberAdd, token);

        ModifierIterator<Integer[], ModifierFactory<? super Integer[]>> arrayIterator =
                new TokenLibrary.ModifierIterator<>(Integer[].class, "SET");
        assertTrue(arrayIterator.hasNext());
        ModifierFactory<? super Integer[]> arrayToken = arrayIterator.next();
        assertNotNull(arrayToken);
        assertEquals(arraySet, arrayToken);
    }

}
