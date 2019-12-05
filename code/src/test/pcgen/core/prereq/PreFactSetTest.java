/*
 * Copyright James Dempsey, 2015
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
package pcgen.core.prereq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreFactSetTest} tests that the PREFACTSET tag is
 * working correctly.
 */
public class PreFactSetTest extends AbstractCharacterTestCase
{

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();
        BuildUtilities.createFactSet(context, "PANTHEON", Deity.class);
        finishLoad();
    }

    /**
     * Test the PREFACT code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testFact() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        Deity deity = new Deity();
        BuildUtilities.addToFactSet(deity, "PANTHEON", "Greek");
        BuildUtilities.addToFactSet(deity, "PANTHEON", "War");
        deity.setName("Ares");

        character.setDeity(deity);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREFACTSET:1,DEITY,PANTHEON=Roman");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character's deity should not match requirement");

        prereq = factory.parse("PREFACTSET:1,DEITY,PANTHEON=War");

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character's deity should match pantheon");

        prereq = factory.parse("PREFACTSET:1,DEITY,PANTHEON=Greek");

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character's deity should match pantheon");
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
