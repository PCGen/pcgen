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
package pcgen.core.term;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Campaign;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PCTLTermEvaluatorTest checks the fucntion of the TL variable.
 */
public class PCTLTermEvaluatorTest extends AbstractCharacterTestCase
{
    private Race bugbearRace;
    private PCClass humanoidClass;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        Campaign customCampaign = new Campaign();
        customCampaign.setName("Unit Test");
        customCampaign.setName("KEY_Unit Test");
        customCampaign.addToListFor(ListKey.DESCRIPTION, new Description("Unit Test data"));
        CampaignSourceEntry source = new CampaignSourceEntry(customCampaign,
                new URI("file:/" + getClass().getName() + ".java"));

        // Create the humanoid class
        String classDef =
                "CLASS:Humanoid	KEY:KEY_Humanoid	HD:8		CLASSTYPE:Monster	STARTSKILLPTS:1	"
                        + "MODTOSKILLS:NO	MONSKILL:6+INT	MONNONSKILLHD:1|PRESIZELTEQ:M	"
                        + "MONNONSKILLHD:2|PRESIZEEQ:L";
        PCClassLoader classLoader = new PCClassLoader();
        LoadContext context = Globals.getContext();
        humanoidClass = classLoader.parseLine(context, null, classDef, source);
        Globals.getContext().getReferenceContext().importObject(humanoidClass);

        // Create the BugBear race
        bugbearRace = new Race();
        bugbearRace.setName("Bugbear");
        bugbearRace.put(StringKey.KEY_NAME, "KEY_Bugbear");
        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        bugbearRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        bugbearRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        bugbearRace.put(IntegerKey.INITIAL_SKILL_MULT, 1);
        Globals.getContext().getReferenceContext().importObject(bugbearRace);

    }

    /**
     * Check that TL works with a monster style class.
     */
    @Test
    public void testResolveTlMonster()
    {
        PCTLTermEvaluator tlEval = new PCTLTermEvaluator("TL");

        PlayerCharacter pc = getCharacter();

        assertEquals(0, tlEval.resolve(pc.getDisplay()), 0.001, "Before adding levels, shold be 0th level");

        pc.setRace(bugbearRace);
        assertEquals(0, tlEval.resolve(pc.getDisplay()), 0.001, "With monster race shold be 0th level");

        pc.incrementClassLevel(1, humanoidClass);
        assertEquals(1, tlEval.resolve(pc.getDisplay()), 0.001, "Incorrect level");

        pc.incrementClassLevel(5, humanoidClass);
        assertEquals(6, tlEval.resolve(pc.getDisplay()), 0.001, "Incorrect level");

        pc.incrementClassLevel(-2, humanoidClass);
        assertEquals(4, tlEval.resolve(pc.getDisplay()), 0.001, "Incorrect level");

    }
}
