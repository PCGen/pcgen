/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PCGVer2ParserCharacterTest runs tests on PCGVer2Parser which require a
 * character to be supplied.
 */
public class PCGVer2ParserCharacterTest extends AbstractCharacterTestCase
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();
        Race rakshasha =
                context.getReferenceContext().constructCDOMObject(Race.class, "Rakshasa");
        context
                .unconditionallyProcess(rakshasha, "ADD", "SPELLCASTER|Sorcerer");
        context.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        finishLoad();
    }

    /**
     * Check that a racial ADD:SPELLCASTER happens exactly once on character
     * load. Duplication of the association has occurred a couple of times in
     * the past.
     *
     * @throws PCGParseException the PCG parse exception
     */
    @Test
    public void testRaceAddSpellcaster() throws PCGParseException
    {
        LoadContext context = Globals.getContext();
        Race rakshasha =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Rakshasa");
        PlayerCharacter pc = getCharacter();
        pc.setImporting(true);
        PCGVer2Parser pcgParser = new PCGVer2Parser(pc);

        String[] pcgLines =
                {"RACE:Rakshasa|ADD:[SPELLCASTER:Sorcerer|CHOICE:Sorcerer]"};
        pcgParser.parsePCG(pcgLines);

        PersistentTransitionChoice<?> tc = rakshasha.getListFor(ListKey.ADD).get(0);
        List<Object> assocList = pc.getAssocList(tc, AssociationListKey.ADD);
        assertEquals("Number of associations for ADD " + assocList, 1, assocList.size());
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
