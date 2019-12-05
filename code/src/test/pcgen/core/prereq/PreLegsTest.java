/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.Test;

/**
 * {@code PreLegsTest} tests that the PRELEGS tag is
 * working correctly.
 */
class PreLegsTest extends AbstractCharacterTestCase
{
    /**
     * Test the PRELEGS code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testLegs() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        Race race = new Race();
        race.put(IntegerKey.LEGS, 2);

        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PRELEGSLT:2");

        assertFalse("Character has more than 1 leg", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRELEGSEQ:2");

        assertTrue("Character has 2 legs", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PRELEGSGT:2");

        assertFalse("Character does not have more than 2 legs", PrereqHandler
                .passes(prereq, character, null));

        PCTemplate tmpl = new PCTemplate();
        tmpl.put(IntegerKey.LEGS, 3);

        character.addTemplate(tmpl);
        assertTrue("Character does have more than 2 legs", PrereqHandler
                .passes(prereq, character, null));
    }
}
